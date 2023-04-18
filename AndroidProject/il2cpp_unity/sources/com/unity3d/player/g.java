package com.unity3d.player;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.unity3d.player.UnityPermissions;

/* loaded from: unity-classes.jar:com/unity3d/player/g.class */
public final class g extends Fragment {
    private final IPermissionRequestCallbacks a;
    private final Activity b;
    private final Looper c;

    /* loaded from: unity-classes.jar:com/unity3d/player/g$a.class */
    class a implements Runnable {
        private IPermissionRequestCallbacks b;
        private String c;
        private int d;
        private boolean e;

        a(IPermissionRequestCallbacks iPermissionRequestCallbacks, String str, int i, boolean z) {
            this.b = iPermissionRequestCallbacks;
            this.c = str;
            this.d = i;
            this.e = z;
        }

        @Override // java.lang.Runnable
        public final void run() {
            if (this.d != -1) {
                if (this.d == 0) {
                    this.b.onPermissionGranted(this.c);
                }
            } else if (this.e) {
                this.b.onPermissionDenied(this.c);
            } else {
                this.b.onPermissionDeniedAndDontAskAgain(this.c);
            }
        }
    }

    public g() {
        this.a = null;
        this.b = null;
        this.c = null;
    }

    public g(Activity activity, IPermissionRequestCallbacks iPermissionRequestCallbacks) {
        this.a = iPermissionRequestCallbacks;
        this.b = activity;
        this.c = Looper.myLooper();
    }

    @Override // android.app.Fragment
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestPermissions(getArguments().getStringArray("PermissionNames"), 96489);
    }

    @Override // android.app.Fragment
    public final void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 96489) {
            return;
        }
        if (strArr.length != 0) {
            for (int i2 = 0; i2 < strArr.length && i2 < iArr.length; i2++) {
                if (this.a != null && this.b != null && this.c != null) {
                    if (this.a instanceof UnityPermissions.ModalWaitForPermissionResponse) {
                        this.a.onPermissionGranted(strArr[i2]);
                    } else {
                        String str = strArr[i2] == null ? "<null>" : strArr[i2];
                        new Handler(this.c).post(new a(this.a, str, iArr[i2], this.b.shouldShowRequestPermissionRationale(str)));
                    }
                }
            }
        } else if (this.a != null && this.b != null && this.c != null) {
            final String[] stringArray = getArguments().getStringArray("PermissionNames");
            if (this.a instanceof UnityPermissions.ModalWaitForPermissionResponse) {
                a(stringArray);
            } else {
                new Handler(this.c).post(new Runnable() { // from class: com.unity3d.player.g.1
                    @Override // java.lang.Runnable
                    public final void run() {
                        g.this.a(stringArray);
                    }
                });
            }
        }
        FragmentTransaction beginTransaction = getActivity().getFragmentManager().beginTransaction();
        beginTransaction.remove(this);
        beginTransaction.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String[] strArr) {
        for (String str : strArr) {
            this.a.onPermissionDenied(str);
        }
    }
}
