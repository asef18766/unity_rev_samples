package com.unity3d.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

/* loaded from: unity-classes.jar:com/unity3d/player/HFPStatus.class */
public class HFPStatus {
    private Context a;
    private AudioManager e;
    private BroadcastReceiver b = null;
    private Intent c = null;
    private boolean d = false;
    private boolean f = false;
    private int g = a.a;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: $VALUES field not found */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: unity-classes.jar:com/unity3d/player/HFPStatus$a.class */
    public static final class a {
        public static final int a = 1;
        public static final int b = 2;
        private static final /* synthetic */ int[] c = {a, b};
    }

    private final native void initHFPStatusJni();

    private final native void deinitHFPStatusJni();

    public HFPStatus(Context context) {
        this.e = null;
        this.a = context;
        this.e = (AudioManager) this.a.getSystemService("audio");
        initHFPStatusJni();
    }

    public final void a() {
        clearHFPStat();
        deinitHFPStatusJni();
    }

    protected void requestHFPStat() {
        clearHFPStat();
        this.b = new BroadcastReceiver() { // from class: com.unity3d.player.HFPStatus.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                switch (intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", -1)) {
                    case 1:
                        HFPStatus.this.g = a.b;
                        HFPStatus.this.c();
                        if (HFPStatus.this.d) {
                            HFPStatus.this.e.setMode(3);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        };
        this.c = this.a.registerReceiver(this.b, new IntentFilter("android.media.ACTION_SCO_AUDIO_STATE_UPDATED"));
        try {
            this.f = true;
            this.e.startBluetoothSco();
        } catch (NullPointerException unused) {
            f.Log(5, "startBluetoothSco() failed. no bluetooth device connected.");
        }
    }

    protected boolean getHFPStat() {
        return this.g == a.b;
    }

    protected void clearHFPStat() {
        b();
        c();
    }

    protected void setHFPRecordingStat(boolean z) {
        this.d = z;
        if (this.d) {
            return;
        }
        this.e.setMode(0);
    }

    private void b() {
        if (this.b != null) {
            this.a.unregisterReceiver(this.b);
            this.b = null;
            this.c = null;
        }
        this.g = a.a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void c() {
        if (this.f) {
            this.f = false;
            this.e.stopBluetoothSco();
        }
    }
}
