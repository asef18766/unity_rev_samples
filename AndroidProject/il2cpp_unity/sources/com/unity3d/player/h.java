package com.unity3d.player;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: unity-classes.jar:com/unity3d/player/h.class */
public final class h implements Application.ActivityLifecycleCallbacks {
    Activity b;
    WeakReference a = new WeakReference(null);
    a c = null;

    /* loaded from: unity-classes.jar:com/unity3d/player/h$a.class */
    class a extends View implements PixelCopy.OnPixelCopyFinishedListener {
        Bitmap a;

        a(Context context) {
            super(context);
        }

        @Override // android.view.PixelCopy.OnPixelCopyFinishedListener
        public final void onPixelCopyFinished(int i) {
            if (i == 0) {
                setBackground(new LayerDrawable(new Drawable[]{new ColorDrawable(-16777216), new BitmapDrawable(getResources(), this.a)}));
            }
        }

        public final void a(SurfaceView surfaceView) {
            this.a = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
            PixelCopy.request(surfaceView, this.a, this, new Handler(Looper.getMainLooper()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public h(Context context) {
        if (context instanceof Activity) {
            this.b = (Activity) context;
            this.b.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    public final void a() {
        if (this.b != null) {
            this.b.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }

    public final void a(SurfaceView surfaceView) {
        if (PlatformSupport.NOUGAT_SUPPORT && this.c == null) {
            this.c = new a(this.b);
            this.c.a(surfaceView);
        }
    }

    public final void a(ViewGroup viewGroup) {
        if (this.c == null || this.c.getParent() != null) {
            return;
        }
        viewGroup.addView(this.c);
        viewGroup.bringChildToFront(this.c);
    }

    public final void b(ViewGroup viewGroup) {
        if (this.c == null || this.c.getParent() == null) {
            return;
        }
        viewGroup.removeView(this.c);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityResumed(Activity activity) {
        this.a = new WeakReference(activity);
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityStopped(Activity activity) {
    }
}
