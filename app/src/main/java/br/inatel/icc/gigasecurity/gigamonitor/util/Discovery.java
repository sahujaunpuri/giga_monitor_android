package br.inatel.icc.gigasecurity.gigamonitor.util;

/**
 * File: Discovery.java
 * Creation date: 22/10/2014
 * Author: rinaldo.bueno
 * <p/>
 * Purpose: Declaration of class Discovery.java
 * Copyright 2014, INATEL Competence Center
 * <p/>
 * All rights are reserved. Reproduction in whole or part is
 * prohibited without the written consent of the copyright owner.
 */

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

//import android.util.Log;

/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service.
 */

public class Discovery extends Thread {
    private static final String TAG = "Discovery";
    private static final String REMOTE_KEY = "b0xeeRem0tE!";
    private static final int DISCOVERY_PORT = 34569;
    private static final int TIMEOUT_MS = 8000;

    private WifiManager mWifi;

    interface DiscoveryReceiver {
        void addAnnouncedServers(InetAddress[] host, int port[]);
    }

    public Discovery(WifiManager wifi) {
        mWifi = wifi;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);

            sendDiscoveryRequest(socket);
            listenForResponses(socket);
        } catch (IOException e) {
            Log.e(TAG, "Could not send discovery request", e);
        }
    }

    /**
     * Send a broadcast UDP packet containing a request for boxee services to
     * announce themselves.
     *
     * @throws java.io.IOException
     */
    private void sendDiscoveryRequest(DatagramSocket socket) throws IOException {

        char[] data = new char[]{
                0xff, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0xfa, 0x05,
                0x00, 0x00, 0x00, 0x00
        };



        Log.d(TAG, "Sending data " + data);

        InetAddress doisCintoCinto = InetAddress.getByName("255.255.255.255");
        String sData = new String(data);
        byte[] bytes = sData.getBytes("UTF-8");
        int length = bytes.length;

//        byte[] bytesCharBuffer = Charset.forName("UTF-8").encode(CharBuffer.wrap(data)).array();

        byte[] toRet = new byte[data.length];
        for(int i = 0; i < toRet.length; i++) {
            toRet[i] = (byte) data[i];
        }

        DatagramPacket packet = new DatagramPacket(toRet, toRet.length, doisCintoCinto, DISCOVERY_PORT);
        socket.send(packet);
    }


    /**
     * Calculate the broadcast IP we need to send the packet along. If we send it
     * to 255.255.255.255, it never gets sent. I guess this has something to do
     * with the mobile network not wanting to do broadcast.
     */
    private InetAddress getBroadcastAddress() throws IOException {

        DhcpInfo dhcp = mWifi.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @param socket
     *          socket on which the announcement request was sent
     * @throws java.io.IOException
     */
    private void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            while (true) {
                WifiManager.MulticastLock mlock =  mWifi.createMulticastLock("br.inatel.icc.gigasecurity.gigamonitor.tests");
                mlock.acquire();

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                mlock.release();
                byte[] data = packet.getData();
                byte [] subArray = Arrays.copyOfRange(data, 20, data.length);
                String s = new String(subArray, 0, packet.getLength());

                Log.d(TAG, "Received response " + s);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Log.d(TAG, "Receive timed out");
        }
    }
}