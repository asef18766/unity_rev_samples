package com.unity3d.player;

/* loaded from: unity-classes.jar:com/unity3d/player/m.class */
final class m {
    private static boolean a = false;
    private boolean b = false;
    private boolean c = false;
    private boolean d = true;
    private boolean e = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void a() {
        a = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void b() {
        a = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean c() {
        return a;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void a(boolean z) {
        this.b = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void b(boolean z) {
        this.d = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean d() {
        return this.d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void c(boolean z) {
        this.e = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean e() {
        return this.e;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void d(boolean z) {
        this.c = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean e(boolean z) {
        if (a) {
            return ((!z && !this.b) || this.d || this.c) ? false : true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean f() {
        return this.c;
    }

    public final String toString() {
        return super.toString();
    }
}
