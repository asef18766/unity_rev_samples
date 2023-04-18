package com.unity3d.player;

/* loaded from: unity-classes.jar:com/unity3d/player/IPermissionRequestCallbacks.class */
public interface IPermissionRequestCallbacks {
    void onPermissionGranted(String str);

    void onPermissionDenied(String str);

    void onPermissionDeniedAndDontAskAgain(String str);
}
