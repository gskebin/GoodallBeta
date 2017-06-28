package kr.co.dunet.app.goodallbeta;

/**
 * Created by hanbit on 2017-05-23.
 */

public class NetworkConfig {

    private String MqttHost = "";
    private String Host = "http://203.236.209.245";
    private String Protocal ="http";
    private String Dns = "203.236.209.245";
    private String Port = "80";

    private static NetworkConfig mNetworkConfig = null;

    public synchronized  static NetworkConfig getInstance() {

        if(mNetworkConfig == null){
            mNetworkConfig = new NetworkConfig();
        }
        return mNetworkConfig;
    }

    public NetworkConfig(){
        mNetworkConfig = this;
    }

    public String getMqttHost() {
        return MqttHost;
    }

    public String getHost() {
        return Host;
    }

    public String getProtocal() {
        return Protocal;
    }

    public String getDns() {
        return Dns;
    }

    public String getPort() {
        return Port;
    }
}
