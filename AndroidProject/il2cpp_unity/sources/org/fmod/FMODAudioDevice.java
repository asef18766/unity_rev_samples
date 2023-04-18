package org.fmod;

import android.media.AudioTrack;
import android.util.Log;
import java.nio.ByteBuffer;

/* loaded from: unity-classes.jar:org/fmod/FMODAudioDevice.class */
public class FMODAudioDevice implements Runnable {
    private volatile Thread a = null;
    private volatile boolean b = false;
    private AudioTrack c = null;
    private boolean d = false;
    private ByteBuffer e = null;
    private byte[] f = null;
    private volatile a g;
    private static int h = 0;
    private static int i = 1;
    private static int j = 2;
    private static int k = 3;
    private static int l = 4;

    public synchronized void start() {
        if (this.a != null) {
            stop();
        }
        this.a = new Thread(this, "FMODAudioDevice");
        this.a.setPriority(10);
        this.b = true;
        this.a.start();
        if (this.g != null) {
            this.g.b();
        }
    }

    public synchronized void stop() {
        while (this.a != null) {
            this.b = false;
            try {
                this.a.join();
                this.a = null;
            } catch (InterruptedException unused) {
            }
        }
        if (this.g != null) {
            this.g.c();
        }
    }

    public synchronized void close() {
        stop();
    }

    public boolean isRunning() {
        return this.a != null && this.a.isAlive();
    }

    @Override // java.lang.Runnable
    public void run() {
        int i2 = 3;
        while (this.b) {
            if (!this.d && i2 > 0) {
                releaseAudioTrack();
                int fmodGetInfo = fmodGetInfo(h);
                int i3 = fmodGetInfo(l) == 1 ? 4 : 12;
                int minBufferSize = AudioTrack.getMinBufferSize(fmodGetInfo, i3, 2);
                int fmodGetInfo2 = 2 * fmodGetInfo(l);
                int round = Math.round(minBufferSize * 1.1f) & ((fmodGetInfo2 - 1) ^ (-1));
                int fmodGetInfo3 = fmodGetInfo(i);
                int fmodGetInfo4 = fmodGetInfo(j);
                if (fmodGetInfo3 * fmodGetInfo4 * fmodGetInfo2 > round) {
                    round = fmodGetInfo3 * fmodGetInfo4 * fmodGetInfo2;
                }
                this.c = new AudioTrack(3, fmodGetInfo, i3, 2, round, 1);
                this.d = this.c.getState() == 1;
                if (this.d) {
                    i2 = 3;
                    this.e = ByteBuffer.allocateDirect(fmodGetInfo3 * fmodGetInfo2);
                    this.f = new byte[this.e.capacity()];
                    this.c.play();
                } else {
                    Log.e("FMOD", "AudioTrack failed to initialize (status " + this.c.getState() + ")");
                    releaseAudioTrack();
                    i2--;
                }
            }
            if (this.d) {
                if (fmodGetInfo(k) == 1) {
                    fmodProcess(this.e);
                    this.e.get(this.f, 0, this.e.capacity());
                    this.c.write(this.f, 0, this.e.capacity());
                    this.e.position(0);
                } else {
                    releaseAudioTrack();
                }
            }
        }
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (this.c != null) {
            if (this.c.getState() == 1) {
                this.c.stop();
            }
            this.c.release();
            this.c = null;
        }
        this.e = null;
        this.f = null;
        this.d = false;
    }

    private native int fmodGetInfo(int i2);

    private native int fmodProcess(ByteBuffer byteBuffer);

    /* JADX INFO: Access modifiers changed from: package-private */
    public native int fmodProcessMicData(ByteBuffer byteBuffer, int i2);

    public synchronized int startAudioRecord(int i2, int i3, int i4) {
        if (this.g == null) {
            this.g = new a(this, i2, i3);
            this.g.b();
        }
        return this.g.a();
    }

    public synchronized void stopAudioRecord() {
        if (this.g != null) {
            this.g.c();
            this.g = null;
        }
    }
}
