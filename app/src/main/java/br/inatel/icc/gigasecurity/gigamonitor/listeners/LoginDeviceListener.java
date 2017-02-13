package br.inatel.icc.gigasecurity.gigamonitor.listeners;

import br.inatel.icc.gigasecurity.gigamonitor.model.Device;

/**
 * Created by zappts on 1/6/17.
 */

public interface LoginDeviceListener {
    void onLoginSuccess();
    void onLoginError(long error, Device device);
    void onLogout();
}
