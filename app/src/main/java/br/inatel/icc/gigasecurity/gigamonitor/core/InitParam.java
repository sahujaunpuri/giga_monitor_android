package br.inatel.icc.gigasecurity.gigamonitor.core;

import com.basic.G;

/**
 * Created by zappts on 1/6/17.
 */

public class InitParam{
    public int st_0_nAppType;
    public byte[] st_1_nSource = new byte[64];
    public byte[] st_2_language = new byte[32];

    public InitParam()
    {
        G.SetValue(st_1_nSource, "mobile");
        G.SetValue(st_2_language, "en");
        st_0_nAppType = 4;
    }
}