package com.unity3d.player;

import android.util.Log;

/* loaded from: unity-classes.jar:com/unity3d/player/f.class */
final class f {
    protected static boolean a = false;

    /* JADX INFO: Access modifiers changed from: protected */
    public static void Log(int i, String str) {
        if (a) {
            return;
        }
        if (i == 6) {
            Log.e("Unity", str);
        }
        if (i == 5) {
            Log.w("Unity", str);
        }
    }
}
