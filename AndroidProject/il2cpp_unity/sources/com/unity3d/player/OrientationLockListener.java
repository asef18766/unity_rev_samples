package com.unity3d.player;

import android.content.Context;
import android.provider.Settings;
import com.unity3d.player.k;

/* loaded from: unity-classes.jar:com/unity3d/player/OrientationLockListener.class */
public class OrientationLockListener implements k.a {
    private k a;
    private Context b;

    public final native void nativeUpdateOrientationLockState(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public OrientationLockListener(Context context) {
        this.b = context;
        this.a = new k(context);
        nativeUpdateOrientationLockState(Settings.System.getInt(this.b.getContentResolver(), "accelerometer_rotation", 0));
        this.a.a(this, "accelerometer_rotation");
    }

    public final void a() {
        this.a.a();
        this.a = null;
    }

    @Override // com.unity3d.player.k.a
    public final void b() {
        nativeUpdateOrientationLockState(Settings.System.getInt(this.b.getContentResolver(), "accelerometer_rotation", 0));
    }
}
