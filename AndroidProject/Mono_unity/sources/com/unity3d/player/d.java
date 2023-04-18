package com.unity3d.player;

import android.app.Activity;

/* loaded from: unity-classes.jar:com/unity3d/player/d.class */
interface d {
    void a(String[] strArr, IAssetPackManagerStatusQueryCallback iAssetPackManagerStatusQueryCallback);

    void a(String[] strArr, IAssetPackManagerDownloadStatusCallback iAssetPackManagerDownloadStatusCallback);

    Object a(IAssetPackManagerDownloadStatusCallback iAssetPackManagerDownloadStatusCallback);

    void a(Object obj);

    void a(Activity activity, IAssetPackManagerMobileDataConfirmationCallback iAssetPackManagerMobileDataConfirmationCallback);

    String a(String str);

    void a(String[] strArr);

    void b(String str);
}
