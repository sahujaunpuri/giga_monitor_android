package br.inatel.icc.gigasecurity.gigamonitor;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import br.inatel.icc.gigasecurity.gigamonitor.util.Discovery;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class UtilTest extends ApplicationTestCase<Application> {
    public UtilTest() {
        super(Application.class);
    }

    public void testSearch() throws Exception {
        new Discovery((android.net.wifi.WifiManager) getContext().getSystemService(Context.WIFI_SERVICE)).start();
        while (true) {
        }

    }
}