package com.unity3d.player;

import android.app.Activity;
import android.content.Context;

/* loaded from: unity-classes.jar:com/unity3d/player/PlayAssetDeliveryUnityWrapper.class */
class PlayAssetDeliveryUnityWrapper {
    private static PlayAssetDeliveryUnityWrapper a;
    private d b;

    private PlayAssetDeliveryUnityWrapper(Context context) {
        if (a != null) {
            throw new RuntimeException("PlayAssetDeliveryUnityWrapper should be created only once. Use getInstance() instead.");
        }
        try {
            Class.forName("com.google.android.play.core.assetpacks.AssetPackManager");
            this.b = a(context);
        } catch (ClassNotFoundException unused) {
            this.b = null;
        }
    }

    public static synchronized PlayAssetDeliveryUnityWrapper getInstance() {
        while (a == null) {
            try {
                PlayAssetDeliveryUnityWrapper.class.wait(3000L);
            } catch (InterruptedException e) {
                f.Log(6, e.getMessage());
            }
        }
        if (a == null) {
            throw new RuntimeException("PlayAssetDeliveryUnityWrapper is not yet initialised.");
        }
        return a;
    }

    public static synchronized PlayAssetDeliveryUnityWrapper init(Context context) {
        if (a != null) {
            throw new RuntimeException("PlayAssetDeliveryUnityWrapper.init() should be called only once. Use getInstance() instead.");
        }
        a = new PlayAssetDeliveryUnityWrapper(context);
        PlayAssetDeliveryUnityWrapper.class.notifyAll();
        return a;
    }

    private static d a(Context context) {
        return a.a(context);
    }

    public void getAssetPackState(String str, IAssetPackManagerStatusQueryCallback iAssetPackManagerStatusQueryCallback) {
        getAssetPackStates(new String[]{str}, iAssetPackManagerStatusQueryCallback);
    }

    public void getAssetPackStates(String[] strArr, IAssetPackManagerStatusQueryCallback iAssetPackManagerStatusQueryCallback) {
        a();
        this.b.a(strArr, iAssetPackManagerStatusQueryCallback);
    }

    public void downloadAssetPack(String str, IAssetPackManagerDownloadStatusCallback iAssetPackManagerDownloadStatusCallback) {
        downloadAssetPacks(new String[]{str}, iAssetPackManagerDownloadStatusCallback);
    }

    public void downloadAssetPacks(String[] strArr, IAssetPackManagerDownloadStatusCallback iAssetPackManagerDownloadStatusCallback) {
        a();
        this.b.a(strArr, iAssetPackManagerDownloadStatusCallback);
    }

    public Object registerDownloadStatusListener(IAssetPackManagerDownloadStatusCallback iAssetPackManagerDownloadStatusCallback) {
        a();
        return this.b.a(iAssetPackManagerDownloadStatusCallback);
    }

    public void unregisterDownloadStatusListener(Object obj) {
        a();
        this.b.a(obj);
    }

    public void requestToUseMobileData(Activity activity, IAssetPackManagerMobileDataConfirmationCallback iAssetPackManagerMobileDataConfirmationCallback) {
        a();
        this.b.a(activity, iAssetPackManagerMobileDataConfirmationCallback);
    }

    public String getAssetPackPath(String str) {
        a();
        return this.b.a(str);
    }

    public void cancelAssetPackDownload(String str) {
        cancelAssetPackDownloads(new String[]{str});
    }

    public void cancelAssetPackDownloads(String[] strArr) {
        a();
        this.b.a(strArr);
    }

    public void removeAssetPack(String str) {
        a();
        this.b.b(str);
    }

    public boolean playCoreApiMissing() {
        return this.b == null;
    }

    private void a() {
        if (playCoreApiMissing()) {
            throw new RuntimeException("AssetPackManager API is not available! Make sure your gradle project includes \"com.google.android.play:core\" dependency.");
        }
    }
}
