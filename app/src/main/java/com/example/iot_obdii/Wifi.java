package com.example.iot_obdii;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class Wifi {
    private WifiManager wifiManager;
    private WifiConfiguration conf;

    private String ssid = "WiFi_OBDII";
    private String key = "";

    public Wifi(WifiManager wifiManager){
        this.wifiManager = wifiManager;
    }

    public void connectWifi() {

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";
        conf.wepKeys[0] = "\"" + key + "\"";

        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            System.out.println(i.SSID);
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                System.out.println("Connected SSID : " + i.SSID);
                break;
            }
        }
    }
}
