package br.inatel.icc.gigasecurity.gigamonitor.listeners;

/**
 * Created by zappts on 1/6/17.
 */

public interface StartDeviceVideoListener {
        void onSuccessStartDevice(long handleID);

        void onErrorStartDevice();
}
