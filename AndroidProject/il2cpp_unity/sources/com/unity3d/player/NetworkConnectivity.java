package com.unity3d.player;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

/* loaded from: unity-classes.jar:com/unity3d/player/NetworkConnectivity.class */
public class NetworkConnectivity extends Activity {
    private int d;
    private ConnectivityManager e;
    private final int a = 0;
    private final int b = 1;
    private final int c = 2;
    private final ConnectivityManager.NetworkCallback f = new ConnectivityManager.NetworkCallback() { // from class: com.unity3d.player.NetworkConnectivity.1
        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onAvailable(Network network) {
            super.onAvailable(network);
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasTransport(0)) {
                NetworkConnectivity.this.d = 1;
            } else {
                NetworkConnectivity.this.d = 2;
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onUnavailable() {
            super.onUnavailable();
            NetworkConnectivity.this.d = 0;
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public final void onLost(Network network) {
            super.onLost(network);
            NetworkConnectivity.this.d = 0;
        }
    };

    public final int a() {
        return this.d;
    }

    public NetworkConnectivity(Context context) {
        this.d = 0;
        this.e = (ConnectivityManager) context.getSystemService("connectivity");
        this.e.registerDefaultNetworkCallback(this.f);
        NetworkInfo activeNetworkInfo = this.e.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return;
        }
        this.d = activeNetworkInfo.getType() == 0 ? 1 : 2;
    }

    public final void b() {
        this.e.unregisterNetworkCallback(this.f);
    }
}
