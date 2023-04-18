package com.unity3d.player;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.unity3d.player.UnityPermissions;
import com.unity3d.player.j;
import com.unity3d.player.o;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer.class */
public class UnityPlayer extends FrameLayout implements IUnityPlayerLifecycleEvents {
    Handler mHandler;
    public static Activity currentActivity = null;
    private int mInitialScreenOrientation;
    private boolean mMainDisplayOverride;
    private boolean mIsFullscreen;
    private m mState;
    private final ConcurrentLinkedQueue m_Events;
    private BroadcastReceiver mKillingIsMyBusiness;
    private OrientationEventListener mOrientationListener;
    private int mNaturalOrientation;
    private static final int ANR_TIMEOUT_SECONDS = 4;
    private static final int RUN_STATE_CHANGED_MSG_CODE = 2269;
    e m_MainThread;
    private boolean m_AddPhoneCallListener;
    private c m_PhoneCallListener;
    private TelephonyManager m_TelephonyManager;
    private ClipboardManager m_ClipboardManager;
    private j m_SplashScreen;
    private h m_PersistentUnitySurface;
    private GoogleARCoreApi m_ARCoreApi;
    private a m_FakeListener;
    private Camera2Wrapper m_Camera2Wrapper;
    private HFPStatus m_HFPStatus;
    private AudioVolumeHandler m_AudioVolumeHandler;
    private OrientationLockListener m_OrientationLockListener;
    private Uri m_launchUri;
    private NetworkConnectivity m_NetworkConnectivity;
    private IUnityPlayerLifecycleEvents m_UnityPlayerLifecycleEvents;
    private Context mContext;
    private Activity mActivity;
    private SurfaceView mGlView;
    private boolean mQuitting;
    private boolean mProcessKillRequested;
    private o mVideoPlayerProxy;
    i mSoftInputDialog;
    private static final String SPLASH_ENABLE_METADATA_NAME = "unity.splash-enable";
    private static final String SPLASH_MODE_METADATA_NAME = "unity.splash-mode";
    private static final String LAUNCH_FULLSCREEN = "unity.launch-fullscreen";
    private static final String ARCORE_ENABLE_METADATA_NAME = "unity.arcore-enable";

    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$a.class */
    class a implements SensorEventListener {
        a() {
        }

        @Override // android.hardware.SensorEventListener
        public final void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public final void onSensorChanged(SensorEvent sensorEvent) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: $VALUES field not found */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$b.class */
    public static final class b {
        public static final int a = 1;
        public static final int b = 2;
        public static final int c = 3;
        private static final /* synthetic */ int[] d = {a, b, c};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$c.class */
    public class c extends PhoneStateListener {
        private c() {
        }

        @Override // android.telephony.PhoneStateListener
        public final void onCallStateChanged(int i, String str) {
            UnityPlayer.this.nativeMuteMasterAudio(i == 1);
        }

        /* synthetic */ c(UnityPlayer unityPlayer, byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$d.class */
    public enum d {
        PAUSE,
        RESUME,
        QUIT,
        SURFACE_LOST,
        SURFACE_ACQUIRED,
        FOCUS_LOST,
        FOCUS_GAINED,
        NEXT_FRAME,
        URL_ACTIVATED,
        ORIENTATION_ANGLE_CHANGE
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$e.class */
    public class e extends Thread {
        Handler a;
        boolean b;
        boolean c;
        int d;
        int e;
        int f;
        int g;
        int h;

        private e() {
            this.b = false;
            this.c = false;
            this.d = b.b;
            this.e = 0;
            this.h = 5;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public final void run() {
            setName("UnityMain");
            Looper.prepare();
            this.a = new Handler(new Handler.Callback() { // from class: com.unity3d.player.UnityPlayer.e.1
                private void a() {
                    if (e.this.d == b.c && e.this.c) {
                        UnityPlayer.this.nativeFocusChanged(true);
                        e.this.d = b.a;
                    }
                }

                @Override // android.os.Handler.Callback
                public final boolean handleMessage(Message message) {
                    if (message.what != UnityPlayer.RUN_STATE_CHANGED_MSG_CODE) {
                        return false;
                    }
                    d dVar = (d) message.obj;
                    if (dVar == d.NEXT_FRAME) {
                        e.this.e--;
                        UnityPlayer.this.executeGLThreadJobs();
                        if (!e.this.b || !e.this.c) {
                            return true;
                        }
                        if (e.this.h >= 0) {
                            if (e.this.h == 0 && UnityPlayer.this.getSplashEnabled()) {
                                UnityPlayer.this.DisableStaticSplashScreen();
                            }
                            e.this.h--;
                        }
                        if (!UnityPlayer.this.isFinishing() && !UnityPlayer.this.nativeRender()) {
                            UnityPlayer.this.finish();
                        }
                    } else if (dVar == d.QUIT) {
                        Looper.myLooper().quit();
                    } else if (dVar == d.RESUME) {
                        e.this.b = true;
                    } else if (dVar == d.PAUSE) {
                        e.this.b = false;
                    } else if (dVar == d.SURFACE_LOST) {
                        e.this.c = false;
                    } else if (dVar == d.SURFACE_ACQUIRED) {
                        e.this.c = true;
                        a();
                    } else if (dVar == d.FOCUS_LOST) {
                        if (e.this.d == b.a) {
                            UnityPlayer.this.nativeFocusChanged(false);
                        }
                        e.this.d = b.b;
                    } else if (dVar == d.FOCUS_GAINED) {
                        e.this.d = b.c;
                        a();
                    } else if (dVar == d.URL_ACTIVATED) {
                        UnityPlayer.this.nativeSetLaunchURL(UnityPlayer.this.getLaunchURL());
                    } else if (dVar == d.ORIENTATION_ANGLE_CHANGE) {
                        UnityPlayer.this.nativeOrientationChanged(e.this.f, e.this.g);
                    }
                    if (!e.this.b || e.this.e > 0) {
                        return true;
                    }
                    Message.obtain(e.this.a, UnityPlayer.RUN_STATE_CHANGED_MSG_CODE, d.NEXT_FRAME).sendToTarget();
                    e.this.e++;
                    return true;
                }
            });
            Looper.loop();
        }

        public final void a() {
            a(d.QUIT);
        }

        public final void b() {
            a(d.RESUME);
        }

        public final void a(Runnable runnable) {
            if (this.a == null) {
                return;
            }
            a(d.PAUSE);
            Message.obtain(this.a, runnable).sendToTarget();
        }

        public final void c() {
            a(d.FOCUS_GAINED);
        }

        public final void d() {
            a(d.FOCUS_LOST);
        }

        public final void b(Runnable runnable) {
            if (this.a == null) {
                return;
            }
            a(d.SURFACE_LOST);
            Message.obtain(this.a, runnable).sendToTarget();
        }

        public final void c(Runnable runnable) {
            if (this.a == null) {
                return;
            }
            Message.obtain(this.a, runnable).sendToTarget();
            a(d.SURFACE_ACQUIRED);
        }

        public final void d(Runnable runnable) {
            if (this.a != null) {
                Message.obtain(this.a, runnable).sendToTarget();
            }
        }

        public final void e() {
            a(d.URL_ACTIVATED);
        }

        private void a(d dVar) {
            if (this.a != null) {
                Message.obtain(this.a, UnityPlayer.RUN_STATE_CHANGED_MSG_CODE, dVar).sendToTarget();
            }
        }

        public final void a(int i, int i2) {
            this.f = i;
            this.g = i2;
            a(d.ORIENTATION_ANGLE_CHANGE);
        }

        /* synthetic */ e(UnityPlayer unityPlayer, byte b) {
            this();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: unity-classes.jar:com/unity3d/player/UnityPlayer$f.class */
    public abstract class f implements Runnable {
        private f() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (UnityPlayer.this.isFinishing()) {
                return;
            }
            a();
        }

        public abstract void a();

        /* synthetic */ f(UnityPlayer unityPlayer, byte b) {
            this();
        }
    }

    public UnityPlayer(Context context) {
        this(context, null);
    }

    public UnityPlayer(Context context, IUnityPlayerLifecycleEvents iUnityPlayerLifecycleEvents) {
        super(context);
        this.mHandler = new Handler();
        this.mInitialScreenOrientation = -1;
        this.mMainDisplayOverride = false;
        this.mIsFullscreen = true;
        this.mState = new m();
        this.m_Events = new ConcurrentLinkedQueue();
        this.mKillingIsMyBusiness = null;
        this.mOrientationListener = null;
        this.m_MainThread = new e(this, (byte) 0);
        this.m_AddPhoneCallListener = false;
        this.m_PhoneCallListener = new c(this, (byte) 0);
        this.m_ARCoreApi = null;
        this.m_FakeListener = new a();
        this.m_Camera2Wrapper = null;
        this.m_HFPStatus = null;
        this.m_AudioVolumeHandler = null;
        this.m_OrientationLockListener = null;
        this.m_launchUri = null;
        this.m_NetworkConnectivity = null;
        this.m_UnityPlayerLifecycleEvents = null;
        this.mProcessKillRequested = true;
        this.mSoftInputDialog = null;
        this.m_UnityPlayerLifecycleEvents = iUnityPlayerLifecycleEvents != null ? iUnityPlayerLifecycleEvents : this;
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
            currentActivity = this.mActivity;
            this.mInitialScreenOrientation = this.mActivity.getRequestedOrientation();
            this.m_launchUri = this.mActivity.getIntent().getData();
        }
        this.mContext = context;
        EarlyEnableFullScreenIfEnabled();
        this.mNaturalOrientation = getNaturalOrientation(getResources().getConfiguration().orientation);
        if (this.mActivity != null && getSplashEnabled()) {
            this.m_SplashScreen = new j(this.mContext, j.a.a()[getSplashMode()]);
            addView(this.m_SplashScreen);
        }
        if (currentActivity != null) {
            this.m_PersistentUnitySurface = new h(this.mContext);
        }
        preloadJavaPlugins();
        String loadNative = loadNative(getUnityNativeLibraryPath(this.mContext));
        if (!m.c()) {
            com.unity3d.player.f.Log(6, "Your hardware does not support this application.");
            AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle("Failure to initialize!").setPositiveButton("OK", new DialogInterface.OnClickListener() { // from class: com.unity3d.player.UnityPlayer.1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    UnityPlayer.this.finish();
                }
            }).setMessage("Your hardware does not support this application.\n\n" + loadNative + "\n\n Press OK to quit.").create();
            create.setCancelable(false);
            create.show();
            return;
        }
        initJni(context);
        this.mState.c(true);
        this.mGlView = CreateGlView();
        this.mGlView.setContentDescription(GetGlViewContentDescription(context));
        addView(this.mGlView);
        bringChildToFront(this.m_SplashScreen);
        this.mQuitting = false;
        hideStatusBar();
        this.m_TelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.m_ClipboardManager = (ClipboardManager) this.mContext.getSystemService("clipboard");
        this.m_Camera2Wrapper = new Camera2Wrapper(this.mContext);
        this.m_HFPStatus = new HFPStatus(this.mContext);
        this.m_MainThread.start();
    }

    private int getNaturalOrientation(int i) {
        int rotation = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if ((rotation == 0 || rotation == 2) && i == 2) {
            return 0;
        }
        return ((rotation == 1 || rotation == 3) && i == 1) ? 0 : 1;
    }

    @Override // com.unity3d.player.IUnityPlayerLifecycleEvents
    public void onUnityPlayerUnloaded() {
    }

    @Override // com.unity3d.player.IUnityPlayerLifecycleEvents
    public void onUnityPlayerQuitted() {
    }

    protected void toggleGyroscopeSensor(boolean z) {
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        Sensor defaultSensor = sensorManager.getDefaultSensor(11);
        if (z) {
            sensorManager.registerListener(this.m_FakeListener, defaultSensor, 1);
        } else {
            sensorManager.unregisterListener(this.m_FakeListener);
        }
    }

    private String GetGlViewContentDescription(Context context) {
        return context.getResources().getString(context.getResources().getIdentifier("game_view_content_description", "string", context.getPackageName()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void DisableStaticSplashScreen() {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.18
            @Override // java.lang.Runnable
            public final void run() {
                UnityPlayer.this.removeView(UnityPlayer.this.m_SplashScreen);
                UnityPlayer.this.m_SplashScreen = null;
            }
        });
    }

    private void EarlyEnableFullScreenIfEnabled() {
        View decorView;
        if (this.mActivity == null || this.mActivity.getWindow() == null) {
            return;
        }
        if ((getLaunchFullscreen() || this.mActivity.getIntent().getBooleanExtra("android.intent.extra.VR_LAUNCH", false)) && (decorView = this.mActivity.getWindow().getDecorView()) != null) {
            decorView.setSystemUiVisibility(7);
        }
    }

    private boolean IsWindowTranslucent() {
        if (this.mActivity == null) {
            return false;
        }
        TypedArray obtainStyledAttributes = this.mActivity.getTheme().obtainStyledAttributes(new int[]{16842840});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        return z;
    }

    private SurfaceView CreateGlView() {
        SurfaceView surfaceView = new SurfaceView(this.mContext);
        surfaceView.setId(this.mContext.getResources().getIdentifier("unitySurfaceView", "id", this.mContext.getPackageName()));
        if (IsWindowTranslucent()) {
            surfaceView.getHolder().setFormat(-3);
            surfaceView.setZOrderOnTop(true);
        } else {
            surfaceView.getHolder().setFormat(-1);
        }
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() { // from class: com.unity3d.player.UnityPlayer.19
            @Override // android.view.SurfaceHolder.Callback
            public final void surfaceCreated(SurfaceHolder surfaceHolder) {
                UnityPlayer.this.updateGLDisplay(0, surfaceHolder.getSurface());
                if (UnityPlayer.this.m_PersistentUnitySurface != null) {
                    UnityPlayer.this.m_PersistentUnitySurface.a(UnityPlayer.this);
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public final void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                UnityPlayer.this.updateGLDisplay(0, surfaceHolder.getSurface());
                UnityPlayer.this.sendSurfaceChangedEvent();
            }

            @Override // android.view.SurfaceHolder.Callback
            public final void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (UnityPlayer.this.m_PersistentUnitySurface != null) {
                    UnityPlayer.this.m_PersistentUnitySurface.a(UnityPlayer.this.mGlView);
                }
                UnityPlayer.this.updateGLDisplay(0, null);
            }
        });
        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        return surfaceView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSurfaceChangedEvent() {
        if (m.c() && this.mState.e()) {
            this.m_MainThread.d(new Runnable() { // from class: com.unity3d.player.UnityPlayer.20
                @Override // java.lang.Runnable
                public final void run() {
                    UnityPlayer.this.nativeSendSurfaceChangedEvent();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGLDisplay(int i, Surface surface) {
        if (this.mMainDisplayOverride) {
            return;
        }
        updateDisplayInternal(i, surface);
    }

    private boolean updateDisplayInternal(final int i, final Surface surface) {
        if (m.c() && this.mState.e()) {
            final Semaphore semaphore = new Semaphore(0);
            Runnable runnable = new Runnable() { // from class: com.unity3d.player.UnityPlayer.21
                @Override // java.lang.Runnable
                public final void run() {
                    UnityPlayer.this.nativeRecreateGfxState(i, surface);
                    semaphore.release();
                }
            };
            if (i != 0) {
                runnable.run();
            } else if (surface == null) {
                this.m_MainThread.b(runnable);
            } else {
                this.m_MainThread.c(runnable);
            }
            if (surface == null && i == 0) {
                try {
                    if (!semaphore.tryAcquire(4L, TimeUnit.SECONDS)) {
                        com.unity3d.player.f.Log(5, "Timeout while trying detaching primary window.");
                    }
                    return true;
                } catch (InterruptedException unused) {
                    com.unity3d.player.f.Log(5, "UI thread got interrupted while trying to detach the primary window from the Unity Engine.");
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    public boolean displayChanged(int i, Surface surface) {
        if (i == 0) {
            this.mMainDisplayOverride = surface != null;
            runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.22
                @Override // java.lang.Runnable
                public final void run() {
                    if (UnityPlayer.this.mMainDisplayOverride) {
                        UnityPlayer.this.removeView(UnityPlayer.this.mGlView);
                    } else {
                        UnityPlayer.this.addView(UnityPlayer.this.mGlView);
                    }
                }
            });
        }
        return updateDisplayInternal(i, surface);
    }

    public static void UnitySendMessage(String str, String str2, String str3) {
        if (!m.c()) {
            com.unity3d.player.f.Log(5, "Native libraries not loaded - dropping message for " + str + "." + str2);
            return;
        }
        try {
            nativeUnitySendMessage(str, str2, str3.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException unused) {
        }
    }

    private static native void nativeUnitySendMessage(String str, String str2, byte[] bArr);

    /* JADX INFO: Access modifiers changed from: private */
    public void finish() {
        if (this.mActivity == null || this.mActivity.isFinishing()) {
            return;
        }
        this.mActivity.finish();
    }

    void runOnAnonymousThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    void runOnUiThread(Runnable runnable) {
        if (this.mActivity != null) {
            this.mActivity.runOnUiThread(runnable);
        } else if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            this.mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    void postOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public void init(int i, boolean z) {
    }

    public View getView() {
        return this;
    }

    public Bundle getSettings() {
        return Bundle.EMPTY;
    }

    public void quit() {
        destroy();
    }

    public void newIntent(Intent intent) {
        this.m_launchUri = intent.getData();
        this.m_MainThread.e();
    }

    public void destroy() {
        if (this.m_PersistentUnitySurface != null) {
            this.m_PersistentUnitySurface.a();
            this.m_PersistentUnitySurface = null;
        }
        if (this.m_Camera2Wrapper != null) {
            this.m_Camera2Wrapper.a();
            this.m_Camera2Wrapper = null;
        }
        if (this.m_HFPStatus != null) {
            this.m_HFPStatus.a();
            this.m_HFPStatus = null;
        }
        if (this.m_NetworkConnectivity != null) {
            this.m_NetworkConnectivity.b();
            this.m_NetworkConnectivity = null;
        }
        this.mQuitting = true;
        if (!this.mState.d()) {
            pause();
        }
        this.m_MainThread.a();
        try {
            this.m_MainThread.join(4000L);
        } catch (InterruptedException unused) {
            this.m_MainThread.interrupt();
        }
        if (this.mKillingIsMyBusiness != null) {
            this.mContext.unregisterReceiver(this.mKillingIsMyBusiness);
        }
        this.mKillingIsMyBusiness = null;
        if (m.c()) {
            removeAllViews();
        }
        if (this.mProcessKillRequested) {
            this.m_UnityPlayerLifecycleEvents.onUnityPlayerQuitted();
            kill();
        }
        unloadNative();
    }

    protected void kill() {
        Process.killProcess(Process.myPid());
    }

    public void pause() {
        if (this.m_ARCoreApi != null) {
            this.m_ARCoreApi.pauseARCore();
        }
        if (this.mVideoPlayerProxy != null) {
            this.mVideoPlayerProxy.a();
        }
        if (this.m_AudioVolumeHandler != null) {
            this.m_AudioVolumeHandler.a();
            this.m_AudioVolumeHandler = null;
        }
        if (this.m_OrientationLockListener != null) {
            this.m_OrientationLockListener.a();
            this.m_OrientationLockListener = null;
        }
        pauseUnity();
    }

    private void pauseUnity() {
        reportSoftInputStr(null, 1, true);
        if (this.mState.f()) {
            if (m.c()) {
                final Semaphore semaphore = new Semaphore(0);
                this.m_MainThread.a(isFinishing() ? new Runnable() { // from class: com.unity3d.player.UnityPlayer.23
                    @Override // java.lang.Runnable
                    public final void run() {
                        UnityPlayer.this.shutdown();
                        semaphore.release();
                    }
                } : new Runnable() { // from class: com.unity3d.player.UnityPlayer.24
                    @Override // java.lang.Runnable
                    public final void run() {
                        if (!UnityPlayer.this.nativePause()) {
                            semaphore.release();
                            return;
                        }
                        UnityPlayer.this.mQuitting = true;
                        UnityPlayer.this.shutdown();
                        semaphore.release(2);
                    }
                });
                try {
                    if (!semaphore.tryAcquire(4L, TimeUnit.SECONDS)) {
                        com.unity3d.player.f.Log(5, "Timeout while trying to pause the Unity Engine.");
                    }
                } catch (InterruptedException unused) {
                    com.unity3d.player.f.Log(5, "UI thread got interrupted while trying to pause the Unity Engine.");
                }
                if (semaphore.drainPermits() > 0) {
                    destroy();
                }
            }
            this.mState.d(false);
            this.mState.b(true);
            if (this.m_AddPhoneCallListener) {
                this.m_TelephonyManager.listen(this.m_PhoneCallListener, 0);
            }
        }
    }

    public void resume() {
        if (this.m_ARCoreApi != null) {
            this.m_ARCoreApi.resumeARCore();
        }
        this.mState.b(false);
        if (this.mVideoPlayerProxy != null) {
            this.mVideoPlayerProxy.b();
        }
        checkResumePlayer();
        if (m.c()) {
            nativeRestartActivityIndicator();
        }
        if (this.m_AudioVolumeHandler == null) {
            this.m_AudioVolumeHandler = new AudioVolumeHandler(this.mContext);
        }
        if (this.m_OrientationLockListener == null && m.c()) {
            this.m_OrientationLockListener = new OrientationLockListener(this.mContext);
        }
    }

    public void lowMemory() {
        if (m.c()) {
            queueGLThreadEvent(new Runnable() { // from class: com.unity3d.player.UnityPlayer.2
                @Override // java.lang.Runnable
                public final void run() {
                    UnityPlayer.this.nativeLowMemory();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shutdown() {
        this.mProcessKillRequested = nativeDone();
        this.mState.c(false);
    }

    public void unload() {
        nativeApplicationUnload();
    }

    private void checkResumePlayer() {
        boolean z = false;
        if (this.mActivity != null) {
            z = MultiWindowSupport.getAllowResizableWindow(this.mActivity);
        }
        if (this.mState.e(z)) {
            this.mState.d(true);
            queueGLThreadEvent(new Runnable() { // from class: com.unity3d.player.UnityPlayer.3
                @Override // java.lang.Runnable
                public final void run() {
                    UnityPlayer.this.nativeResume();
                    UnityPlayer.this.runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.3.1
                        @Override // java.lang.Runnable
                        public final void run() {
                            if (UnityPlayer.this.m_PersistentUnitySurface != null) {
                                UnityPlayer.this.m_PersistentUnitySurface.b(UnityPlayer.this);
                            }
                        }
                    });
                }
            });
            this.m_MainThread.b();
        }
    }

    protected boolean skipPermissionsDialog() {
        if (this.mActivity != null) {
            return UnityPermissions.skipPermissionsDialog(this.mActivity);
        }
        return false;
    }

    protected void requestUserAuthorization(String str) {
        if (str == null || str.isEmpty() || this.mActivity == null) {
            return;
        }
        UnityPermissions.ModalWaitForPermissionResponse modalWaitForPermissionResponse = new UnityPermissions.ModalWaitForPermissionResponse();
        UnityPermissions.requestUserPermissions(this.mActivity, new String[]{str}, modalWaitForPermissionResponse);
        modalWaitForPermissionResponse.waitForResponse();
    }

    protected int getNetworkConnectivity() {
        if (PlatformSupport.NOUGAT_SUPPORT) {
            if (this.m_NetworkConnectivity == null) {
                this.m_NetworkConnectivity = new NetworkConnectivity(this.mContext);
            }
            return this.m_NetworkConnectivity.a();
        }
        return 0;
    }

    public void configurationChanged(Configuration configuration) {
        if (this.mGlView instanceof SurfaceView) {
            this.mGlView.getHolder().setSizeFromLayout();
        }
        if (this.mVideoPlayerProxy != null) {
            this.mVideoPlayerProxy.c();
        }
    }

    public void windowFocusChanged(boolean z) {
        this.mState.a(z);
        if (this.mState.e()) {
            if (this.mSoftInputDialog == null || this.mSoftInputDialog.a) {
                if (z) {
                    this.m_MainThread.c();
                } else {
                    this.m_MainThread.d();
                }
                checkResumePlayer();
            }
        }
    }

    protected boolean loadLibrary(String str) {
        try {
            System.loadLibrary(str);
            return true;
        } catch (Exception unused) {
            return false;
        } catch (UnsatisfiedLinkError unused2) {
            return false;
        }
    }

    protected void addPhoneCallListener() {
        this.m_AddPhoneCallListener = true;
        this.m_TelephonyManager.listen(this.m_PhoneCallListener, 32);
    }

    private final native void initJni(Context context);

    /* JADX INFO: Access modifiers changed from: private */
    public final native boolean nativeRender();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSetInputArea(int i, int i2, int i3, int i4);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSetKeyboardIsVisible(boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSetInputString(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSetInputSelection(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSoftInputCanceled();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSoftInputLostFocus();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeReportKeyboardConfigChanged();

    /* JADX INFO: Access modifiers changed from: private */
    public final native boolean nativePause();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeResume();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeLowMemory();

    private final native void nativeApplicationUnload();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeFocusChanged(boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeRecreateGfxState(int i, Surface surface);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSendSurfaceChangedEvent();

    private final native boolean nativeDone();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSoftInputClosed();

    private final native boolean nativeInjectEvent(InputEvent inputEvent);

    /* JADX INFO: Access modifiers changed from: private */
    public final native boolean nativeIsAutorotationOn();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeMuteMasterAudio(boolean z);

    private final native void nativeRestartActivityIndicator();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeSetLaunchURL(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void nativeOrientationChanged(int i, int i2);

    private static String logLoadLibMainError(String str, String str2) {
        String str3 = "Failed to load 'libmain.so'\n\n" + str2;
        com.unity3d.player.f.Log(6, str3);
        return str3;
    }

    private static void preloadJavaPlugins() {
        try {
            Class.forName("com.unity3d.JavaPluginPreloader");
        } catch (ClassNotFoundException unused) {
        } catch (LinkageError e2) {
            com.unity3d.player.f.Log(6, "Java class preloading failed: " + e2.getMessage());
        }
    }

    private static String loadNative(String str) {
        String str2 = str + "/libmain.so";
        try {
            try {
                System.load(str2);
            } catch (UnsatisfiedLinkError unused) {
                System.loadLibrary("main");
            }
            if (NativeLoader.load(str)) {
                m.a();
                return "";
            }
            com.unity3d.player.f.Log(6, "NativeLoader.load failure, Unity libraries were not loaded.");
            return "NativeLoader.load failure, Unity libraries were not loaded.";
        } catch (SecurityException e2) {
            return logLoadLibMainError(str2, e2.toString());
        } catch (UnsatisfiedLinkError e3) {
            return logLoadLibMainError(str2, e3.toString());
        }
    }

    private static void unloadNative() {
        if (m.c()) {
            if (!NativeLoader.unload()) {
                throw new UnsatisfiedLinkError("Unable to unload libraries from libmain.so");
            }
            m.b();
        }
    }

    private static String getUnityNativeLibraryPath(Context context) {
        return context.getApplicationInfo().nativeLibraryDir;
    }

    protected void showSoftInput(final String str, final int i, final boolean z, final boolean z2, final boolean z3, final boolean z4, final String str2, final int i2, final boolean z5, final boolean z6) {
        postOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.4
            @Override // java.lang.Runnable
            public final void run() {
                UnityPlayer.this.mSoftInputDialog = new i(UnityPlayer.this.mContext, this, str, i, z, z2, z3, str2, i2, z5, z6);
                UnityPlayer.this.mSoftInputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.unity3d.player.UnityPlayer.4.1
                    @Override // android.content.DialogInterface.OnCancelListener
                    public final void onCancel(DialogInterface dialogInterface) {
                        UnityPlayer.this.nativeSoftInputLostFocus();
                        UnityPlayer.this.reportSoftInputStr(null, 1, false);
                    }
                });
                UnityPlayer.this.mSoftInputDialog.show();
                UnityPlayer.this.nativeReportKeyboardConfigChanged();
            }
        });
    }

    protected void hideSoftInput() {
        postOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.5
            @Override // java.lang.Runnable
            public final void run() {
                UnityPlayer.this.reportSoftInputArea(new Rect());
                UnityPlayer.this.reportSoftInputIsVisible(false);
                if (UnityPlayer.this.mSoftInputDialog != null) {
                    UnityPlayer.this.mSoftInputDialog.dismiss();
                    UnityPlayer.this.mSoftInputDialog = null;
                    UnityPlayer.this.nativeReportKeyboardConfigChanged();
                }
            }
        });
    }

    protected void setSoftInputStr(final String str) {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.6
            @Override // java.lang.Runnable
            public final void run() {
                if (UnityPlayer.this.mSoftInputDialog == null || str == null) {
                    return;
                }
                UnityPlayer.this.mSoftInputDialog.a(str);
            }
        });
    }

    protected void setCharacterLimit(final int i) {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.7
            @Override // java.lang.Runnable
            public final void run() {
                if (UnityPlayer.this.mSoftInputDialog != null) {
                    UnityPlayer.this.mSoftInputDialog.a(i);
                }
            }
        });
    }

    protected void setHideInputField(final boolean z) {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.8
            @Override // java.lang.Runnable
            public final void run() {
                if (UnityPlayer.this.mSoftInputDialog != null) {
                    UnityPlayer.this.mSoftInputDialog.a(z);
                }
            }
        });
    }

    protected void setSelection(final int i, final int i2) {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.9
            @Override // java.lang.Runnable
            public final void run() {
                if (UnityPlayer.this.mSoftInputDialog != null) {
                    UnityPlayer.this.mSoftInputDialog.a(i, i2);
                }
            }
        });
    }

    protected String getKeyboardLayout() {
        if (this.mSoftInputDialog == null) {
            return null;
        }
        return this.mSoftInputDialog.a();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reportSoftInputStr(final String str, final int i, final boolean z) {
        if (i == 1) {
            hideSoftInput();
        }
        queueGLThreadEvent(new f() { // from class: com.unity3d.player.UnityPlayer.10
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(UnityPlayer.this, (byte) 0);
            }

            @Override // com.unity3d.player.UnityPlayer.f
            public final void a() {
                if (z) {
                    UnityPlayer.this.nativeSoftInputCanceled();
                } else if (str != null) {
                    UnityPlayer.this.nativeSetInputString(str);
                }
                if (i == 1) {
                    UnityPlayer.this.nativeSoftInputClosed();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reportSoftInputSelection(final int i, final int i2) {
        queueGLThreadEvent(new f() { // from class: com.unity3d.player.UnityPlayer.11
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(UnityPlayer.this, (byte) 0);
            }

            @Override // com.unity3d.player.UnityPlayer.f
            public final void a() {
                UnityPlayer.this.nativeSetInputSelection(i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reportSoftInputArea(final Rect rect) {
        queueGLThreadEvent(new f() { // from class: com.unity3d.player.UnityPlayer.12
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(UnityPlayer.this, (byte) 0);
            }

            @Override // com.unity3d.player.UnityPlayer.f
            public final void a() {
                UnityPlayer.this.nativeSetInputArea(rect.left, rect.top, rect.right, rect.bottom);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reportSoftInputIsVisible(final boolean z) {
        queueGLThreadEvent(new f() { // from class: com.unity3d.player.UnityPlayer.13
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(UnityPlayer.this, (byte) 0);
            }

            @Override // com.unity3d.player.UnityPlayer.f
            public final void a() {
                UnityPlayer.this.nativeSetKeyboardIsVisible(z);
            }
        });
    }

    protected void setClipboardText(String str) {
        this.m_ClipboardManager.setPrimaryClip(ClipData.newPlainText("Text", str));
    }

    protected String getClipboardText() {
        ClipData primaryClip = this.m_ClipboardManager.getPrimaryClip();
        return primaryClip != null ? primaryClip.getItemAt(0).coerceToText(this.mContext).toString() : "";
    }

    protected String getLaunchURL() {
        if (this.m_launchUri != null) {
            return this.m_launchUri.toString();
        }
        return null;
    }

    protected boolean initializeGoogleAr() {
        if (this.m_ARCoreApi == null && this.mActivity != null && getARCoreEnabled()) {
            this.m_ARCoreApi = new GoogleARCoreApi();
            this.m_ARCoreApi.initializeARCore(this.mActivity);
            if (this.mState.d()) {
                return false;
            }
            this.m_ARCoreApi.resumeARCore();
            return false;
        }
        return false;
    }

    protected boolean showVideoPlayer(String str, int i, int i2, int i3, boolean z, int i4, int i5) {
        if (this.mVideoPlayerProxy == null) {
            this.mVideoPlayerProxy = new o(this);
        }
        boolean a2 = this.mVideoPlayerProxy.a(this.mContext, str, i, i2, i3, z, i4, i5, new o.a() { // from class: com.unity3d.player.UnityPlayer.14
            @Override // com.unity3d.player.o.a
            public final void a() {
                UnityPlayer.this.mVideoPlayerProxy = null;
            }
        });
        if (a2) {
            runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.15
                @Override // java.lang.Runnable
                public final void run() {
                    if (!UnityPlayer.this.nativeIsAutorotationOn() || UnityPlayer.this.mActivity == null) {
                        return;
                    }
                    ((Activity) UnityPlayer.this.mContext).setRequestedOrientation(UnityPlayer.this.mInitialScreenOrientation);
                }
            });
        }
        return a2;
    }

    protected void pauseJavaAndCallUnloadCallback() {
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.UnityPlayer.16
            @Override // java.lang.Runnable
            public final void run() {
                UnityPlayer.this.pause();
                UnityPlayer.this.windowFocusChanged(false);
                UnityPlayer.this.m_UnityPlayerLifecycleEvents.onUnityPlayerUnloaded();
            }
        });
    }

    protected boolean isUaaLUseCase() {
        String callingPackage;
        return (this.mActivity == null || (callingPackage = this.mActivity.getCallingPackage()) == null || !callingPackage.equals(this.mContext.getPackageName())) ? false : true;
    }

    protected int getUaaLLaunchProcessType() {
        String processName = getProcessName();
        return (processName == null || processName.equals(this.mContext.getPackageName())) ? 0 : 1;
    }

    private String getProcessName() {
        int myPid = Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) this.mContext.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    private ApplicationInfo getApplicationInfo() {
        return this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 128);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getSplashEnabled() {
        try {
            return getApplicationInfo().metaData.getBoolean(SPLASH_ENABLE_METADATA_NAME);
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean getARCoreEnabled() {
        try {
            return getApplicationInfo().metaData.getBoolean(ARCORE_ENABLE_METADATA_NAME);
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean getLaunchFullscreen() {
        try {
            return getApplicationInfo().metaData.getBoolean(LAUNCH_FULLSCREEN);
        } catch (Exception unused) {
            return false;
        }
    }

    protected int getSplashMode() {
        try {
            return getApplicationInfo().metaData.getInt(SPLASH_MODE_METADATA_NAME);
        } catch (Exception unused) {
            return 0;
        }
    }

    protected void executeGLThreadJobs() {
        while (true) {
            Runnable runnable = (Runnable) this.m_Events.poll();
            if (runnable == null) {
                return;
            }
            runnable.run();
        }
    }

    protected void disableLogger() {
        com.unity3d.player.f.a = true;
    }

    private void queueGLThreadEvent(Runnable runnable) {
        if (m.c()) {
            if (Thread.currentThread() == this.m_MainThread) {
                runnable.run();
            } else {
                this.m_Events.add(runnable);
            }
        }
    }

    private void queueGLThreadEvent(f fVar) {
        if (isFinishing()) {
            return;
        }
        queueGLThreadEvent((Runnable) fVar);
    }

    protected boolean isFinishing() {
        if (this.mQuitting) {
            return true;
        }
        if (this.mActivity != null) {
            this.mQuitting = this.mActivity.isFinishing();
        }
        return this.mQuitting;
    }

    private void hideStatusBar() {
        if (this.mActivity != null) {
            this.mActivity.getWindow().setFlags(1024, 1024);
        }
    }

    public boolean injectEvent(InputEvent inputEvent) {
        if (m.c()) {
            return nativeInjectEvent(inputEvent);
        }
        return false;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return injectEvent(keyEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return injectEvent(motionEvent);
    }

    private void swapViews(View view, View view2) {
        boolean z = false;
        if (!this.mState.d()) {
            pause();
            z = true;
        }
        if (view != null) {
            ViewParent parent = view.getParent();
            if (!(parent instanceof UnityPlayer) || ((UnityPlayer) parent) != this) {
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
                addView(view);
                bringChildToFront(view);
                view.setVisibility(0);
            }
        }
        if (view2 != null && view2.getParent() == this) {
            view2.setVisibility(8);
            removeView(view2);
        }
        if (z) {
            resume();
        }
    }

    public boolean addViewToPlayer(View view, boolean z) {
        swapViews(view, z ? this.mGlView : null);
        boolean z2 = view.getParent() == this;
        boolean z3 = z && this.mGlView.getParent() == null;
        boolean z4 = this.mGlView.getParent() == this;
        boolean z5 = z2 && (z3 || z4);
        boolean z6 = z5;
        if (!z5) {
            if (!z2) {
                com.unity3d.player.f.Log(6, "addViewToPlayer: Failure adding view to hierarchy");
            }
            if (!z3 && !z4) {
                com.unity3d.player.f.Log(6, "addViewToPlayer: Failure removing old view from hierarchy");
            }
        }
        return z6;
    }

    public void removeViewFromPlayer(View view) {
        swapViews(this.mGlView, view);
        boolean z = view.getParent() == null;
        boolean z2 = this.mGlView.getParent() == this;
        if (z && z2) {
            return;
        }
        if (!z) {
            com.unity3d.player.f.Log(6, "removeViewFromPlayer: Failure removing view from hierarchy");
        }
        if (z2) {
            return;
        }
        com.unity3d.player.f.Log(6, "removeVireFromPlayer: Failure agging old view to hierarchy");
    }

    public void reportError(String str, String str2) {
        com.unity3d.player.f.Log(6, str + ": " + str2);
    }

    public String getNetworkProxySettings(String str) {
        String str2;
        String str3;
        String str4;
        if (str.startsWith("http:")) {
            str2 = "http.proxyHost";
            str3 = "http.proxyPort";
            str4 = "http.nonProxyHosts";
        } else if (!str.startsWith("https:")) {
            return null;
        } else {
            str2 = "https.proxyHost";
            str3 = "https.proxyPort";
            str4 = "http.nonProxyHosts";
        }
        String property = System.getProperties().getProperty(str2);
        if (property == null || "".equals(property)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(property);
        String property2 = System.getProperties().getProperty(str3);
        if (property2 != null && !"".equals(property2)) {
            sb.append(":").append(property2);
        }
        String property3 = System.getProperties().getProperty(str4);
        if (property3 != null && !"".equals(property3)) {
            sb.append('\n').append(property3);
        }
        return sb.toString();
    }

    public boolean startOrientationListener(int i) {
        if (this.mOrientationListener != null) {
            com.unity3d.player.f.Log(5, "Orientation Listener already started.");
            return false;
        }
        this.mOrientationListener = new OrientationEventListener(this.mContext, i) { // from class: com.unity3d.player.UnityPlayer.17
            @Override // android.view.OrientationEventListener
            public final void onOrientationChanged(int i2) {
                UnityPlayer.this.m_MainThread.a(UnityPlayer.this.mNaturalOrientation, i2);
            }
        };
        if (this.mOrientationListener.canDetectOrientation()) {
            this.mOrientationListener.enable();
            return true;
        }
        com.unity3d.player.f.Log(5, "Orientation Listener cannot detect orientation.");
        return false;
    }

    public boolean stopOrientationListener() {
        if (this.mOrientationListener == null) {
            com.unity3d.player.f.Log(5, "Orientation Listener was not started.");
            return false;
        }
        this.mOrientationListener.disable();
        this.mOrientationListener = null;
        return true;
    }

    static {
        new l().a();
    }
}
