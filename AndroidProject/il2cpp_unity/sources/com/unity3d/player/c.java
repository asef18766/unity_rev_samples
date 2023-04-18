package com.unity3d.player;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.Surface;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/* loaded from: unity-classes.jar:com/unity3d/player/c.class */
public final class c {
    private e a;
    private CameraDevice d;
    private HandlerThread f;
    private Handler g;
    private Rect h;
    private Rect i;
    private int j;
    private int k;
    private int n;
    private int o;
    private Range q;
    private Image s;
    private CaptureRequest.Builder t;
    private int w;
    private SurfaceTexture x;
    private static CameraManager b = null;
    private static String[] c = null;
    private static Semaphore e = new Semaphore(1);
    private float l = -1.0f;
    private float m = -1.0f;
    private boolean p = false;
    private ImageReader r = null;
    private CameraCaptureSession u = null;
    private Object v = new Object();
    private Surface y = null;
    private int z = a.c;
    private CameraCaptureSession.CaptureCallback A = new CameraCaptureSession.CaptureCallback() { // from class: com.unity3d.player.c.1
        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public final void onCaptureCompleted(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, TotalCaptureResult totalCaptureResult) {
            c.this.a(captureRequest.getTag());
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public final void onCaptureFailed(CameraCaptureSession cameraCaptureSession, CaptureRequest captureRequest, CaptureFailure captureFailure) {
            f.Log(5, "Camera2: Capture session failed " + captureRequest.getTag() + " reason " + captureFailure.getReason());
            c.this.a(captureRequest.getTag());
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public final void onCaptureSequenceCompleted(CameraCaptureSession cameraCaptureSession, int i, long j) {
        }

        @Override // android.hardware.camera2.CameraCaptureSession.CaptureCallback
        public final void onCaptureSequenceAborted(CameraCaptureSession cameraCaptureSession, int i) {
        }
    };
    private final CameraDevice.StateCallback B = new CameraDevice.StateCallback() { // from class: com.unity3d.player.c.3
        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public final void onOpened(CameraDevice cameraDevice) {
            c.this.d = cameraDevice;
            c.e.release();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public final void onClosed(CameraDevice cameraDevice) {
            c.e.release();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public final void onDisconnected(CameraDevice cameraDevice) {
            f.Log(5, "Camera2: CameraDevice disconnected.");
            c.this.a(cameraDevice);
            c.e.release();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public final void onError(CameraDevice cameraDevice, int i) {
            f.Log(6, "Camera2: Error opeining CameraDevice " + i);
            c.this.a(cameraDevice);
            c.e.release();
        }
    };
    private final ImageReader.OnImageAvailableListener C = new ImageReader.OnImageAvailableListener() { // from class: com.unity3d.player.c.4
        @Override // android.media.ImageReader.OnImageAvailableListener
        public final void onImageAvailable(ImageReader imageReader) {
            if (c.e.tryAcquire()) {
                Image acquireNextImage = imageReader.acquireNextImage();
                if (acquireNextImage != null) {
                    Image.Plane[] planes = acquireNextImage.getPlanes();
                    if (acquireNextImage.getFormat() == 35 && planes != null && planes.length == 3) {
                        c.this.a.a(planes[0].getBuffer(), planes[1].getBuffer(), planes[2].getBuffer(), planes[0].getRowStride(), planes[1].getRowStride(), planes[1].getPixelStride());
                    } else {
                        f.Log(6, "Camera2: Wrong image format.");
                    }
                    if (c.this.s != null) {
                        c.this.s.close();
                    }
                    c.this.s = acquireNextImage;
                }
                c.e.release();
            }
        }
    };
    private final SurfaceTexture.OnFrameAvailableListener D = new SurfaceTexture.OnFrameAvailableListener() { // from class: com.unity3d.player.c.5
        @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
        public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
            c.this.a.a(surfaceTexture);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: $VALUES field not found */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* loaded from: unity-classes.jar:com/unity3d/player/c$a.class */
    public static final class a {
        public static final int a = 1;
        public static final int b = 2;
        public static final int c = 3;
        private static final /* synthetic */ int[] d = {a, b, c};
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public c(e eVar) {
        this.a = null;
        this.a = eVar;
        g();
    }

    private static CameraManager b(Context context) {
        if (b == null) {
            b = (CameraManager) context.getSystemService("camera");
        }
        return b;
    }

    private static String[] c(Context context) {
        if (c == null) {
            try {
                c = b(context).getCameraIdList();
            } catch (CameraAccessException e2) {
                f.Log(6, "Camera2: CameraAccessException " + e2);
                c = new String[0];
            }
        }
        return c;
    }

    public static int a(Context context) {
        return c(context).length;
    }

    public static int a(Context context, int i) {
        try {
            return ((Integer) b(context).getCameraCharacteristics(c(context)[i]).get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
            return 0;
        }
    }

    public static boolean b(Context context, int i) {
        try {
            return ((Integer) b(context).getCameraCharacteristics(c(context)[i]).get(CameraCharacteristics.LENS_FACING)).intValue() == 0;
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
            return false;
        }
    }

    public static boolean c(Context context, int i) {
        try {
            return ((Integer) b(context).getCameraCharacteristics(c(context)[i]).get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)).intValue() > 0;
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
            return false;
        }
    }

    public static int d(Context context, int i) {
        try {
            CameraCharacteristics cameraCharacteristics = b(context).getCameraCharacteristics(c(context)[i]);
            float[] fArr = (float[]) cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
            SizeF sizeF = (SizeF) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
            if (fArr.length > 0) {
                return (int) ((fArr[0] * 36.0f) / sizeF.getWidth());
            }
            return 0;
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
            return 0;
        }
    }

    private static Size[] a(CameraCharacteristics cameraCharacteristics) {
        StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (streamConfigurationMap == null) {
            f.Log(6, "Camera2: configuration map is not available.");
            return null;
        }
        Size[] outputSizes = streamConfigurationMap.getOutputSizes(35);
        if (outputSizes == null || outputSizes.length == 0) {
            return null;
        }
        return outputSizes;
    }

    public static int[] e(Context context, int i) {
        try {
            Size[] a2 = a(b(context).getCameraCharacteristics(c(context)[i]));
            if (a2 != null) {
                int[] iArr = new int[a2.length * 2];
                for (int i2 = 0; i2 < a2.length; i2++) {
                    iArr[i2 * 2] = a2[i2].getWidth();
                    iArr[(i2 * 2) + 1] = a2[i2].getHeight();
                }
                return iArr;
            }
            return null;
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
            return null;
        }
    }

    private void g() {
        this.f = new HandlerThread("CameraBackground");
        this.f.start();
        this.g = new Handler(this.f.getLooper());
    }

    private void h() {
        this.f.quit();
        try {
            this.f.join(4000L);
            this.f = null;
            this.g = null;
        } catch (InterruptedException e2) {
            this.f.interrupt();
            f.Log(6, "Camera2: Interrupted while waiting for the background thread to finish " + e2);
        }
    }

    public final boolean a(Context context, int i, int i2, int i3, int i4, int i5) {
        try {
            CameraCharacteristics cameraCharacteristics = b.getCameraCharacteristics(c(context)[i]);
            if (((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue() == 2) {
                f.Log(5, "Camera2: only LEGACY hardware level is supported.");
                return false;
            }
            Size[] a2 = a(cameraCharacteristics);
            if (a2 == null || a2.length == 0) {
                return false;
            }
            this.h = a(a2, i2, i3);
            Range[] rangeArr = (Range[]) cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            if (rangeArr == null || rangeArr.length == 0) {
                f.Log(6, "Camera2: target FPS ranges are not avialable.");
                return false;
            }
            int a3 = a(rangeArr, i4);
            this.q = new Range(Integer.valueOf(a3), Integer.valueOf(a3));
            try {
                if (!e.tryAcquire(4L, TimeUnit.SECONDS)) {
                    f.Log(5, "Camera2: Timeout waiting to lock camera for opening.");
                    return false;
                }
                try {
                    b.openCamera(c(context)[i], this.B, this.g);
                    try {
                    } catch (InterruptedException e2) {
                        f.Log(6, "Camera2: Interrupted while waiting to open camera " + e2);
                    }
                    if (!e.tryAcquire(4L, TimeUnit.SECONDS)) {
                        f.Log(5, "Camera2: Timeout waiting to open camera.");
                        return false;
                    }
                    e.release();
                    this.w = i5;
                    b(cameraCharacteristics);
                    return this.d != null;
                } catch (CameraAccessException e3) {
                    f.Log(6, "Camera2: CameraAccessException " + e3);
                    e.release();
                    return false;
                }
            } catch (InterruptedException e4) {
                f.Log(6, "Camera2: Interrupted while trying to lock camera for opening " + e4);
                return false;
            }
        } catch (CameraAccessException e5) {
            f.Log(6, "Camera2: CameraAccessException " + e5);
            return false;
        }
    }

    private void b(CameraCharacteristics cameraCharacteristics) {
        this.k = ((Integer) cameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)).intValue();
        if (this.k > 0) {
            this.i = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            float width = this.i.width() / this.i.height();
            float width2 = this.h.width() / this.h.height();
            if (width2 > width) {
                this.n = 0;
                this.o = (int) ((this.i.height() - (this.i.width() / width2)) / 2.0f);
            } else {
                this.o = 0;
                this.n = (int) ((this.i.width() - (this.i.height() * width2)) / 2.0f);
            }
            this.j = Math.min(this.i.width(), this.i.height()) / 20;
        }
    }

    public final boolean a(float f, float f2) {
        if (this.k > 0) {
            if (this.p) {
                f.Log(5, "Camera2: Setting manual focus point already started.");
                return false;
            }
            this.l = f;
            this.m = f2;
            synchronized (this.v) {
                if (this.u != null && this.z != a.b) {
                    k();
                }
            }
            return true;
        }
        return false;
    }

    public final Rect a() {
        return this.h;
    }

    public final void b() {
        if (this.d != null) {
            e();
            i();
            this.A = null;
            this.y = null;
            this.x = null;
            if (this.s != null) {
                this.s.close();
                this.s = null;
            }
            if (this.r != null) {
                this.r.close();
                this.r = null;
            }
        }
        h();
    }

    private void i() {
        try {
            if (!e.tryAcquire(4L, TimeUnit.SECONDS)) {
                f.Log(5, "Camera2: Timeout waiting to lock camera for closing.");
                return;
            }
            this.d.close();
            try {
                if (!e.tryAcquire(4L, TimeUnit.SECONDS)) {
                    f.Log(5, "Camera2: Timeout waiting to close camera.");
                }
            } catch (InterruptedException e2) {
                f.Log(6, "Camera2: Interrupted while waiting to close camera " + e2);
            }
            this.d = null;
            e.release();
        } catch (InterruptedException e3) {
            f.Log(6, "Camera2: Interrupted while trying to lock camera for closing " + e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Object obj) {
        if (obj != "Focus") {
            if (obj == "Cancel focus") {
                synchronized (this.v) {
                    if (this.u != null) {
                        j();
                    }
                }
                return;
            }
            return;
        }
        this.p = false;
        synchronized (this.v) {
            if (this.u != null) {
                try {
                    this.t.set(CaptureRequest.CONTROL_AF_TRIGGER, 0);
                    this.t.setTag("Regular");
                    this.u.setRepeatingRequest(this.t.build(), this.A, this.g);
                } catch (CameraAccessException e2) {
                    f.Log(6, "Camera2: CameraAccessException " + e2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void j() {
        try {
            if (this.k == 0 || this.l < 0.0f || this.l > 1.0f || this.m < 0.0f || this.m > 1.0f) {
                this.t.set(CaptureRequest.CONTROL_AF_MODE, 4);
                this.t.setTag("Regular");
                if (this.u != null) {
                    this.u.setRepeatingRequest(this.t.build(), this.A, this.g);
                    return;
                }
                return;
            }
            this.p = true;
            int width = (int) (((this.i.width() - (this.n * 2)) * this.l) + this.n);
            int height = (int) (((this.i.height() - (this.o * 2)) * (1.0d - this.m)) + this.o);
            this.t.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{new MeteringRectangle(Math.max(1 + this.j, Math.min(width, (this.i.width() - this.j) - 1)) - this.j, Math.max(1 + this.j, Math.min(height, (this.i.height() - this.j) - 1)) - this.j, this.j * 2, this.j * 2, 999)});
            this.t.set(CaptureRequest.CONTROL_AF_MODE, 1);
            this.t.set(CaptureRequest.CONTROL_AF_TRIGGER, 1);
            this.t.setTag("Focus");
            this.u.capture(this.t.build(), this.A, this.g);
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
        }
    }

    private void k() {
        try {
            if (this.u != null) {
                this.u.stopRepeating();
                this.t.set(CaptureRequest.CONTROL_AF_TRIGGER, 2);
                this.t.set(CaptureRequest.CONTROL_AF_MODE, 0);
                this.t.setTag("Cancel focus");
                this.u.capture(this.t.build(), this.A, this.g);
            }
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
        }
    }

    public final void c() {
        if (this.r == null) {
            this.r = ImageReader.newInstance(this.h.width(), this.h.height(), 35, 2);
            this.r.setOnImageAvailableListener(this.C, this.g);
            this.s = null;
            if (this.w != 0) {
                this.x = new SurfaceTexture(this.w);
                this.x.setDefaultBufferSize(this.h.width(), this.h.height());
                this.x.setOnFrameAvailableListener(this.D, this.g);
                this.y = new Surface(this.x);
            }
        }
        try {
            if (this.u == null) {
                this.d.createCaptureSession(this.y != null ? Arrays.asList(this.y, this.r.getSurface()) : Arrays.asList(this.r.getSurface()), new CameraCaptureSession.StateCallback() { // from class: com.unity3d.player.c.2
                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public final void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        if (c.this.d == null) {
                            return;
                        }
                        synchronized (c.this.v) {
                            c.this.u = cameraCaptureSession;
                            try {
                                try {
                                    c.this.t = c.this.d.createCaptureRequest(1);
                                    if (c.this.y != null) {
                                        c.this.t.addTarget(c.this.y);
                                    }
                                    c.this.t.addTarget(c.this.r.getSurface());
                                    c.this.t.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, c.this.q);
                                    c.this.j();
                                } catch (IllegalStateException e2) {
                                    f.Log(6, "Camera2: IllegalStateException " + e2);
                                }
                            } catch (CameraAccessException e3) {
                                f.Log(6, "Camera2: CameraAccessException " + e3);
                            }
                        }
                    }

                    @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
                    public final void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        f.Log(6, "Camera2: CaptureSession configuration failed.");
                    }
                }, this.g);
            } else if (this.z == a.b) {
                this.u.setRepeatingRequest(this.t.build(), this.A, this.g);
            }
            this.z = a.a;
        } catch (CameraAccessException e2) {
            f.Log(6, "Camera2: CameraAccessException " + e2);
        }
    }

    public final void d() {
        synchronized (this.v) {
            if (this.u != null) {
                try {
                    this.u.stopRepeating();
                    this.z = a.b;
                } catch (CameraAccessException e2) {
                    f.Log(6, "Camera2: CameraAccessException " + e2);
                }
            }
        }
    }

    public final void e() {
        synchronized (this.v) {
            if (this.u != null) {
                try {
                    this.u.abortCaptures();
                } catch (CameraAccessException e2) {
                    f.Log(6, "Camera2: CameraAccessException " + e2);
                }
                this.u.close();
                this.u = null;
                this.z = a.c;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(CameraDevice cameraDevice) {
        synchronized (this.v) {
            this.u = null;
        }
        cameraDevice.close();
        this.d = null;
    }

    private static Rect a(Size[] sizeArr, double d, double d2) {
        int i = 0;
        int i2 = 0;
        double d3 = Double.MAX_VALUE;
        for (int i3 = 0; i3 < sizeArr.length; i3++) {
            int width = sizeArr[i3].getWidth();
            int height = sizeArr[i3].getHeight();
            double abs = Math.abs(Math.log(d / width)) + Math.abs(Math.log(d2 / height));
            if (abs < d3) {
                d3 = abs;
                i = width;
                i2 = height;
            }
        }
        return new Rect(0, 0, i, i2);
    }

    private static int a(Range[] rangeArr, int i) {
        int i2 = -1;
        double d = Double.MAX_VALUE;
        for (int i3 = 0; i3 < rangeArr.length; i3++) {
            int intValue = ((Integer) rangeArr[i3].getLower()).intValue();
            int intValue2 = ((Integer) rangeArr[i3].getUpper()).intValue();
            if (i + 0.1f > intValue && i - 0.1f < intValue2) {
                return i;
            }
            float min = Math.min(Math.abs(i - intValue), Math.abs(i - intValue2));
            if (min < d) {
                d = min;
                i2 = i3;
            }
        }
        return i > ((Integer) rangeArr[i2].getUpper()).intValue() ? ((Integer) rangeArr[i2].getUpper()).intValue() : ((Integer) rangeArr[i2].getLower()).intValue();
    }
}
