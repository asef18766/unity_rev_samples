package com.unity3d.player;

import android.content.Context;
import com.unity3d.player.b;

/* loaded from: unity-classes.jar:com/unity3d/player/AudioVolumeHandler.class */
public class AudioVolumeHandler implements b.InterfaceC0003b {
    private b a;

    @Override // com.unity3d.player.b.InterfaceC0003b
    public final native void onAudioVolumeChanged(int i);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioVolumeHandler(Context context) {
        this.a = new b(context);
        this.a.a(this);
    }

    public final void a() {
        this.a.a();
        this.a = null;
    }
}
