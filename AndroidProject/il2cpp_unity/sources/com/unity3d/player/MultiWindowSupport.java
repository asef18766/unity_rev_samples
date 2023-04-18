package com.unity3d.player;

import android.app.Activity;
import android.content.pm.ApplicationInfo;

/* loaded from: unity-classes.jar:com/unity3d/player/MultiWindowSupport.class */
public class MultiWindowSupport {
    private static final String RESIZABLE_WINDOW = "unity.allow-resizable-window";
    private static boolean s_LastMultiWindowMode = false;

    static boolean isInMultiWindowMode(Activity activity) {
        if (PlatformSupport.NOUGAT_SUPPORT) {
            return activity.isInMultiWindowMode();
        }
        return false;
    }

    public static boolean getAllowResizableWindow(Activity activity) {
        try {
            ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), 128);
            if (isInMultiWindowMode(activity)) {
                return applicationInfo.metaData.getBoolean(RESIZABLE_WINDOW);
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void saveMultiWindowMode(Activity activity) {
        s_LastMultiWindowMode = isInMultiWindowMode(activity);
    }

    public static boolean isMultiWindowModeChangedToTrue(Activity activity) {
        return !s_LastMultiWindowMode && isInMultiWindowMode(activity);
    }
}
