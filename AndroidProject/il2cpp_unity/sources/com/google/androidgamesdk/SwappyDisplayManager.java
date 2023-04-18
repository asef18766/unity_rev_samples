package com.google.androidgamesdk;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: unity-classes.jar:com/google/androidgamesdk/SwappyDisplayManager.class */
public class SwappyDisplayManager implements DisplayManager.DisplayListener {
    private final String LOG_TAG = "SwappyDisplayManager";
    private final boolean DEBUG = false;
    private final long ONE_MS_IN_NS = 1000000;
    private final long ONE_S_IN_NS = 1000000000;
    private long mCookie;
    private Activity mActivity;
    private WindowManager mWindowManager;
    private Display.Mode mCurrentMode;
    private a mLooper;

    /* loaded from: unity-classes.jar:com/google/androidgamesdk/SwappyDisplayManager$a.class */
    private class a extends Thread {
        public Handler a;
        private Lock c;
        private Condition d;

        private a() {
            this.c = new ReentrantLock();
            this.d = this.c.newCondition();
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Thread] */
        /* JADX WARN: Type inference failed for: r0v7, types: [java.util.concurrent.locks.Condition] */
        @Override // java.lang.Thread
        public final void start() {
            this.c.lock();
            InterruptedException interruptedException = this;
            super.start();
            try {
                interruptedException = this.d;
                interruptedException.await();
            } catch (InterruptedException unused) {
                interruptedException.printStackTrace();
            }
            this.c.unlock();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            Log.i("SwappyDisplayManager", "Starting looper thread");
            this.c.lock();
            Looper.prepare();
            this.a = new Handler();
            this.d.signal();
            this.c.unlock();
            Looper.loop();
            Log.i("SwappyDisplayManager", "Terminating looper thread");
        }

        /* synthetic */ a(SwappyDisplayManager swappyDisplayManager, byte b) {
            this();
        }
    }

    private boolean modeMatchesCurrentResolution(Display.Mode mode) {
        return mode.getPhysicalHeight() == this.mCurrentMode.getPhysicalHeight() && mode.getPhysicalWidth() == this.mCurrentMode.getPhysicalWidth();
    }

    public SwappyDisplayManager(long j, Activity activity) {
        String string;
        try {
            ActivityInfo activityInfo = activity.getPackageManager().getActivityInfo(activity.getIntent().getComponent(), 128);
            if (activityInfo.metaData != null && (string = activityInfo.metaData.getString("android.app.lib_name")) != null) {
                System.loadLibrary(string);
            }
        } catch (Throwable th) {
            Log.e("SwappyDisplayManager", th.getMessage());
        }
        this.mCookie = j;
        this.mActivity = activity;
        this.mWindowManager = (WindowManager) this.mActivity.getSystemService(WindowManager.class);
        Display defaultDisplay = this.mWindowManager.getDefaultDisplay();
        this.mCurrentMode = defaultDisplay.getMode();
        updateSupportedRefreshRates(defaultDisplay);
        DisplayManager displayManager = (DisplayManager) this.mActivity.getSystemService(DisplayManager.class);
        synchronized (this) {
            this.mLooper = new a(this, (byte) 0);
            this.mLooper.start();
            displayManager.registerDisplayListener(this, this.mLooper.a);
        }
    }

    private void updateSupportedRefreshRates(Display display) {
        Display.Mode[] supportedModes = display.getSupportedModes();
        int i = 0;
        for (Display.Mode mode : supportedModes) {
            if (modeMatchesCurrentResolution(mode)) {
                i++;
            }
        }
        long[] jArr = new long[i];
        int[] iArr = new int[i];
        int i2 = 0;
        for (int i3 = 0; i3 < supportedModes.length; i3++) {
            if (modeMatchesCurrentResolution(supportedModes[i3])) {
                jArr[i2] = 1.0E9f / supportedModes[i3].getRefreshRate();
                iArr[i2] = supportedModes[i3].getModeId();
                i2++;
            }
        }
        nSetSupportedRefreshPeriods(this.mCookie, jArr, iArr);
    }

    public void setPreferredDisplayModeId(final int i) {
        this.mActivity.runOnUiThread(new Runnable() { // from class: com.google.androidgamesdk.SwappyDisplayManager.1
            @Override // java.lang.Runnable
            public final void run() {
                Window window = SwappyDisplayManager.this.mActivity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.preferredDisplayModeId = i;
                window.setAttributes(attributes);
            }
        });
    }

    public void terminate() {
        this.mLooper.a.getLooper().quit();
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int i) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int i) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int i) {
        synchronized (this) {
            Display defaultDisplay = this.mWindowManager.getDefaultDisplay();
            float refreshRate = defaultDisplay.getRefreshRate();
            Display.Mode mode = defaultDisplay.getMode();
            boolean z = (mode.getPhysicalWidth() != this.mCurrentMode.getPhysicalWidth()) | (mode.getPhysicalHeight() != this.mCurrentMode.getPhysicalHeight());
            boolean z2 = refreshRate != this.mCurrentMode.getRefreshRate();
            this.mCurrentMode = mode;
            if (z) {
                updateSupportedRefreshRates(defaultDisplay);
            }
            if (z2) {
                long appVsyncOffsetNanos = defaultDisplay.getAppVsyncOffsetNanos();
                long j = 1.0E9f / refreshRate;
                nOnRefreshPeriodChanged(this.mCookie, j, appVsyncOffsetNanos, j - (this.mWindowManager.getDefaultDisplay().getPresentationDeadlineNanos() - 1000000));
            }
        }
    }

    private native void nSetSupportedRefreshPeriods(long j, long[] jArr, int[] iArr);

    private native void nOnRefreshPeriodChanged(long j, long j2, long j3, long j4);
}
