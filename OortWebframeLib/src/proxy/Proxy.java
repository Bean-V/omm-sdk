package proxy;

import org.apache.cordova.CordovaPlugin;

public class Proxy {
    public static CordovaPlugin getProxyCordovaPlugin() {
        return proxyCordovaPlugin;
    }

    public static void setProxyCordovaPlugin(CordovaPlugin proxyCordovaPlugin) {
        Proxy.proxyCordovaPlugin = proxyCordovaPlugin;
    }

    private static CordovaPlugin proxyCordovaPlugin;

}
