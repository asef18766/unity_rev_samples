package com.unity3d.player;

import android.app.Activity;

/* loaded from: unity-classes.jar:com/unity3d/player/GoogleARCoreApi.class */
public class GoogleARCoreApi {
    public final native void initializeARCore(Activity activity);

    public final native void pauseARCore();

    public final native void resumeARCore();
}
