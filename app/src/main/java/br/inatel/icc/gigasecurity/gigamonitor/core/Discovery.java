package br.inatel.icc.gigasecurity.gigamonitor.core;

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

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

import br.inatel.icc.gigasecurity.gigamonitor.model.Device;
import br.inatel.icc.gigasecurity.gigamonitor.util.Utils;

//import android.util.Log;

/*
 * This class tries to send a broadcast UDP packet over your wifi network to discover the boxee service.
 */

public class Discovery extends Thread {
    private static final String TAG = "Discovery";
    private static final int DISCOVERY_PORT = 34569;
    private static final int TIMEOUT_MS = 5000;

    private WifiManager mWifi;

    private DiscoveryReceiver mReceiver;

    WifiManager.MulticastLock mLock;

    public interface DiscoveryReceiver {
        void onReceiveDevices(ArrayList<Device> devices);
        void onFailedSearch();
    }

    public Discovery(Context context, DiscoveryReceiver receiver) {
        mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mReceiver = receiver;
    }

    private DatagramSocket mSocket;

    public void run() {
        try {
            mSocket = new DatagramSocket(DISCOVERY_PORT);
            mSocket.setBroadcast(true);
            mSocket.setSoTimeout(TIMEOUT_MS);

            sendDiscoveryRequest(mSocket);
            listenForResponses(mSocket);
        } catch (IOException e) {
            Log.e(TAG, "Could not send discovery request", e);
            mReceiver.onFailedSearch();
//            run();
        }
    }

    @Override
    public void interrupt() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.close();
        }
        if (mLock != null) {
            mLock.release();
        }
        super.interrupt();
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

        ArrayList<Device> devices = new ArrayList<>();
        Device device;
        try {
            while (true) {
                boolean existent = false;
                mLock =  mWifi.createMulticastLock("br.inatel.icc.gigasecurity.gigamonitor.tests");
                mLock.acquire();

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                mLock.release();
                byte[] data = packet.getData();
                byte [] subArray = Arrays.copyOfRange(data, 20, data.length);
                String s = new String(subArray, 0, packet.getLength());
                Log.d(TAG, "Received response " + s);

                try {
                    JSONObject responseObject = new JSONObject(s);

                    JSONObject netCommon = responseObject.getJSONObject("NetWork.NetCommon");

                    device = new Device();
                    if (netCommon.has("HostName")) device.setHostname(netCommon.getString("HostName"));
                    if (netCommon.has("HostIP")) device.setIpAddress(Utils.hexStringToIP(
                            netCommon.getString("HostIP")));
                    if (netCommon.has("Submask")) device.setSubmask(Utils.hexStringToIP(
                            netCommon.getString("Submask")));
                    if (netCommon.has("GateWay")) device.setGateway(Utils.hexStringToIP(
                            netCommon.getString("GateWay")));
                    if (netCommon.has("TCPPort")) device.setTCPPort(netCommon.getInt("TCPPort"));
                    if (netCommon.has("HttpPort")) device.setHttpPort(netCommon.getInt("HttpPort"));
                    if (netCommon.has("SslPort")) device.setSslPort(netCommon.getInt("SSLPort"));
                    if (netCommon.has("UDPPort")) device.setSslPort(netCommon.getInt("UDPPort"));
                    if (netCommon.has("MonMode")) device.setMonMode(netCommon.getString("MonMode"));
                    if (netCommon.has("DvrMac")) device.setMacAddress(netCommon.getString("DvrMac"));
                    if (netCommon.has("SN")) device.setSerialNumber(netCommon.getString("SN"));
                    if (netCommon.has("MAC")) device.setMacAddress(netCommon.getString("MAC"));
                    if(netCommon.has("ChannelNum")) device.setChannelNumber(netCommon.getInt("ChannelNum"));

                    for(Device dev : devices){
                        if(dev.getIpAddress().equals(device.getIpAddress())){
                            existent = true;
                            break;
                        }
                    }
                    if(!existent)
                        devices.add(device);

                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }

            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out: " + e.getMessage());
        } finally {
            socket.disconnect();
            socket.close();
        }
        mReceiver.onReceiveDevices(devices);
    }
}