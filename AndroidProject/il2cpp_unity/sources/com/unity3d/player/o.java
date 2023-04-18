package com.unity3d.player;

import android.app.Activity;
import android.content.Context;
import com.unity3d.player.n;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: unity-classes.jar:com/unity3d/player/o.class */
public final class o {
    private UnityPlayer a;
    private a c;
    private Context b = null;
    private final Semaphore d = new Semaphore(0);
    private final Lock e = new ReentrantLock();
    private n f = null;
    private int g = 2;
    private boolean h = false;
    private boolean i = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.unity3d.player.o$1  reason: invalid class name */
    /* loaded from: unity-classes.jar:com/unity3d/player/o$1.class */
    public class AnonymousClass1 implements Runnable {
        final /* synthetic */ String a;
        final /* synthetic */ int b;
        final /* synthetic */ int c;
        final /* synthetic */ int d;
        final /* synthetic */ boolean e;
        final /* synthetic */ long f;
        final /* synthetic */ long g;

        AnonymousClass1(String str, int i, int i2, int i3, boolean z, long j, long j2) {
            this.a = str;
            this.b = i;
            this.c = i2;
            this.d = i3;
            this.e = z;
            this.f = j;
            this.g = j2;
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (o.this.f != null) {
                f.Log(5, "Video already playing");
                o.this.g = 2;
                o.this.d.release();
                return;
            }
            o.this.f = new n(o.this.b, this.a, this.b, this.c, this.d, this.e, this.f, this.g, new n.a() { // from class: com.unity3d.player.o.1.1
                @Override // com.unity3d.player.n.a
                public final void a(int i) {
                    o.this.e.lock();
                    o.this.g = i;
                    if (i == 3 && o.this.i) {
                        o.this.runOnUiThread(new Runnable() { // from class: com.unity3d.player.o.1.1.1
                            @Override // java.lang.Runnable
                            public final void run() {
                                o.this.d();
                                o.this.a.resume();
                            }
                        });
                    }
                    if (i != 0) {
                        o.this.d.release();
                    }
                    o.this.e.unlock();
                }
            });
            if (o.this.f != null) {
                o.this.a.addView(o.this.f);
            }
        }
    }

    /* loaded from: unity-classes.jar:com/unity3d/player/o$a.class */
    public interface a {
        void a();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public o(UnityPlayer unityPlayer) {
        this.a = null;
        this.a = unityPlayer;
    }

    public final boolean a(Context context, String str, int i, int i2, int i3, boolean z, long j, long j2, a aVar) {
        this.e.lock();
        this.c = aVar;
        this.b = context;
        this.d.drainPermits();
        this.g = 2;
        runOnUiThread(new AnonymousClass1(str, i, i2, i3, z, j, j2));
        boolean z2 = false;
        try {
            this.e.unlock();
            this.d.acquire();
            this.e.lock();
            z2 = this.g != 2;
        } catch (InterruptedException unused) {
        }
        runOnUiThread(new Runnable() { // from class: com.unity3d.player.o.2
            @Override // java.lang.Runnable
            public final void run() {
                o.this.a.pause();
            }
        });
        if (!z2 || this.g == 3) {
            runOnUiThread(new Runnable() { // from class: com.unity3d.player.o.4
                @Override // java.lang.Runnable
                public final void run() {
                    o.this.d();
                    o.this.a.resume();
                }
            });
        } else {
            runOnUiThread(new Runnable() { // from class: com.unity3d.player.o.3
                @Override // java.lang.Runnable
                public final void run() {
                    if (o.this.f != null) {
                        o.this.a.addViewToPlayer(o.this.f, true);
                        o.this.i = true;
                        o.this.f.requestFocus();
                    }
                }
            });
        }
        this.e.unlock();
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d() {
        if (this.f != null) {
            this.a.removeViewFromPlayer(this.f);
            this.i = false;
            this.f.destroyPlayer();
            this.f = null;
            if (this.c != null) {
                this.c.a();
            }
        }
    }

    public final void a() {
        this.e.lock();
        if (this.f != null) {
            if (this.g == 0) {
                this.f.CancelOnPrepare();
            } else if (this.i) {
                this.h = this.f.a();
                if (!this.h) {
                    this.f.pause();
                }
            }
        }
        this.e.unlock();
    }

    public final void b() {
        this.e.lock();
        if (this.f != null && this.i && !this.h) {
            this.f.start();
        }
        this.e.unlock();
    }

    public final void c() {
        this.e.lock();
        if (this.f != null) {
            this.f.updateVideoLayout();
        }
        this.e.unlock();
    }

    protected final void runOnUiThread(Runnable runnable) {
        if (this.b instanceof Activity) {
            ((Activity) this.b).runOnUiThread(runnable);
        } else {
            f.Log(5, "Not running from an Activity; Ignoring execution request...");
        }
    }
}
