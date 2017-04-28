package br.inatel.icc.gigasecurity.gigamonitor.listeners;

import org.json.JSONObject;

/**
 * Created by zappts on 2/21/17.
 */

public interface ConfigListener {
    void onReceivedConfig();
    void onSetConfig();
    void onError();
}
