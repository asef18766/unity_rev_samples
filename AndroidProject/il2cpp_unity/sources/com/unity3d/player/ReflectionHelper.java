package com.unity3d.player;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: unity-classes.jar:com/unity3d/player/ReflectionHelper.class */
public final class ReflectionHelper {
    protected static final boolean LOGV = false;
    protected static boolean LOG = false;
    private static a[] a = new a[4096];
    private static long b = 0;
    private static long c = 0;
    private static boolean d = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: unity-classes.jar:com/unity3d/player/ReflectionHelper$a.class */
    public static class a {
        private final Class b;
        private final String c;
        private final String d;
        private final int e;
        public volatile Member a;

        a(Class cls, String str, String str2) {
            this.b = cls;
            this.c = str;
            this.d = str2;
            this.e = (31 * ((31 * (527 + this.b.hashCode())) + this.c.hashCode())) + this.d.hashCode();
        }

        public final int hashCode() {
            return this.e;
        }

        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof a) {
                a aVar = (a) obj;
                return this.e == aVar.e && this.d.equals(aVar.d) && this.c.equals(aVar.c) && this.b.equals(aVar.b);
            }
            return false;
        }
    }

    /* loaded from: unity-classes.jar:com/unity3d/player/ReflectionHelper$b.class */
    protected interface b extends InvocationHandler {
        void a(long j, boolean z);
    }

    ReflectionHelper() {
    }

    private static synchronized boolean a(a aVar) {
        a aVar2 = a[aVar.hashCode() & (a.length - 1)];
        if (aVar.equals(aVar2)) {
            aVar.a = aVar2.a;
            return true;
        }
        return false;
    }

    private static synchronized void a(a aVar, Member member) {
        aVar.a = member;
        a[aVar.hashCode() & (a.length - 1)] = aVar;
    }

    protected static Constructor getConstructorID(Class cls, String str) {
        Constructor<?> constructor = LOGV;
        a aVar = new a(cls, "", str);
        if (a(aVar)) {
            constructor = (Constructor) aVar.a;
        } else {
            Class[] a2 = a(str);
            float f = LOGV;
            Constructor<?>[] constructors = cls.getConstructors();
            int length = constructors.length;
            for (int i = LOGV; i < length; i++) {
                Constructor<?> constructor2 = constructors[i];
                float a3 = a(Void.TYPE, constructor2.getParameterTypes(), a2);
                if (a3 > f) {
                    constructor = constructor2;
                    f = a3;
                    if (a3 == 1.0f) {
                        break;
                    }
                }
            }
            a(aVar, constructor);
        }
        if (constructor == null) {
            throw new NoSuchMethodError("<init>" + str + " in class " + cls.getName());
        }
        return constructor;
    }

    protected static Method getMethodID(Class cls, String str, String str2, boolean z) {
        Method method = LOGV;
        a aVar = new a(cls, str, str2);
        if (a(aVar)) {
            method = (Method) aVar.a;
        } else {
            Class[] a2 = a(str2);
            float f = LOGV;
            while (cls != null) {
                Method[] declaredMethods = cls.getDeclaredMethods();
                int length = declaredMethods.length;
                for (int i = LOGV; i < length; i++) {
                    Method method2 = declaredMethods[i];
                    if (z == Modifier.isStatic(method2.getModifiers()) && method2.getName().compareTo(str) == 0) {
                        float a3 = a(method2.getReturnType(), method2.getParameterTypes(), a2);
                        if (a3 > f) {
                            method = method2;
                            f = a3;
                            if (a3 == 1.0f) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if (f == 1.0f || cls.isPrimitive() || cls.isInterface() || cls.equals(Object.class) || cls.equals(Void.TYPE)) {
                    break;
                }
                cls = cls.getSuperclass();
            }
            a(aVar, method);
        }
        if (method == null) {
            Object[] objArr = new Object[4];
            objArr[LOGV] = z ? "static" : "non-static";
            objArr[1] = str;
            objArr[2] = str2;
            objArr[3] = cls.getName();
            throw new NoSuchMethodError(String.format("no %s method with name='%s' signature='%s' in class L%s;", objArr));
        }
        return method;
    }

    protected static Field getFieldID(Class cls, String str, String str2, boolean z) {
        Field field = LOGV;
        a aVar = new a(cls, str, str2);
        if (a(aVar)) {
            field = (Field) aVar.a;
        } else {
            Class[] a2 = a(str2);
            float f = LOGV;
            while (cls != null) {
                Field[] declaredFields = cls.getDeclaredFields();
                int length = declaredFields.length;
                for (int i = LOGV; i < length; i++) {
                    Field field2 = declaredFields[i];
                    if (z == Modifier.isStatic(field2.getModifiers()) && field2.getName().compareTo(str) == 0) {
                        float a3 = a(field2.getType(), (Class[]) null, a2);
                        if (a3 > f) {
                            field = field2;
                            f = a3;
                            if (a3 == 1.0f) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if (f == 1.0f || cls.isPrimitive() || cls.isInterface() || cls.equals(Object.class) || cls.equals(Void.TYPE)) {
                    break;
                }
                cls = cls.getSuperclass();
            }
            a(aVar, field);
        }
        if (field == null) {
            Object[] objArr = new Object[4];
            objArr[LOGV] = z ? "static" : "non-static";
            objArr[1] = str;
            objArr[2] = str2;
            objArr[3] = cls.getName();
            throw new NoSuchFieldError(String.format("no %s field with name='%s' signature='%s' in class L%s;", objArr));
        }
        return field;
    }

    protected static String getFieldSignature(Field field) {
        Class<?> type = field.getType();
        if (!type.isPrimitive()) {
            return type.isArray() ? type.getName().replace('.', '/') : "L" + type.getName().replace('.', '/') + ";";
        }
        String name = type.getName();
        return "boolean".equals(name) ? "Z" : "byte".equals(name) ? "B" : "char".equals(name) ? "C" : "double".equals(name) ? "D" : "float".equals(name) ? "F" : "int".equals(name) ? "I" : "long".equals(name) ? "J" : "short".equals(name) ? "S" : name;
    }

    private static float a(Class cls, Class cls2) {
        if (cls.equals(cls2)) {
            return 1.0f;
        }
        if (cls.isPrimitive() || cls2.isPrimitive()) {
            return 0.0f;
        }
        try {
            if (cls.asSubclass(cls2) != null) {
                return 0.5f;
            }
        } catch (ClassCastException unused) {
        }
        try {
            return cls2.asSubclass(cls) != null ? 0.1f : 0.0f;
        } catch (ClassCastException unused2) {
            return 0.0f;
        }
    }

    private static float a(Class cls, Class[] clsArr, Class[] clsArr2) {
        if (clsArr2.length == 0) {
            return 0.1f;
        }
        if ((clsArr == null ? LOGV : clsArr.length) + 1 != clsArr2.length) {
            return 0.0f;
        }
        float f = 1.0f;
        int i = LOGV;
        if (clsArr != null) {
            int length = clsArr.length;
            for (int i2 = LOGV; i2 < length; i2++) {
                int i3 = i;
                i++;
                f *= a(clsArr[i2], clsArr2[i3]);
            }
        }
        return f * a(cls, clsArr2[clsArr2.length - 1]);
    }

    private static Class[] a(String str) {
        Class a2;
        int[] iArr = {LOGV};
        ArrayList arrayList = new ArrayList();
        while (iArr[LOGV] < str.length() && (a2 = a(str, iArr)) != null) {
            arrayList.add(a2);
        }
        int i = LOGV;
        Class[] clsArr = new Class[arrayList.size()];
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            int i2 = i;
            i++;
            clsArr[i2] = (Class) it.next();
        }
        return clsArr;
    }

    private static Class a(String str, int[] iArr) {
        while (iArr[LOGV] < str.length()) {
            int i = iArr[LOGV];
            iArr[LOGV] = i + 1;
            char charAt = str.charAt(i);
            if (charAt != '(' && charAt != ')') {
                if (charAt == 'L') {
                    int indexOf = str.indexOf(59, iArr[LOGV]);
                    if (indexOf != -1) {
                        String substring = str.substring(iArr[LOGV], indexOf);
                        iArr[LOGV] = indexOf + 1;
                        try {
                            return Class.forName(substring.replace('/', '.'));
                        } catch (ClassNotFoundException unused) {
                            return null;
                        }
                    }
                    return null;
                } else if (charAt == 'Z') {
                    return Boolean.TYPE;
                } else {
                    if (charAt == 'I') {
                        return Integer.TYPE;
                    }
                    if (charAt == 'F') {
                        return Float.TYPE;
                    }
                    if (charAt == 'V') {
                        return Void.TYPE;
                    }
                    if (charAt == 'B') {
                        return Byte.TYPE;
                    }
                    if (charAt == 'C') {
                        return Character.TYPE;
                    }
                    if (charAt == 'S') {
                        return Short.TYPE;
                    }
                    if (charAt == 'J') {
                        return Long.TYPE;
                    }
                    if (charAt == 'D') {
                        return Double.TYPE;
                    }
                    if (charAt == '[') {
                        return Array.newInstance(a(str, iArr), (int) LOGV).getClass();
                    }
                    f.Log(5, "! parseType; " + charAt + " is not known!");
                    return null;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native Object nativeProxyInvoke(long j, String str, Object[] objArr);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeProxyFinalize(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeProxyLogJNIInvokeException(long j);

    protected static synchronized boolean beginProxyCall(long j) {
        if (j == b) {
            c++;
            return true;
        }
        return false;
    }

    protected static synchronized void endProxyCall() {
        c = 0L;
        if (0 == c - 1 && d) {
            ReflectionHelper.class.notifyAll();
        }
    }

    protected static synchronized void endUnityLaunch() {
        try {
            b++;
            d = true;
            while (c > 0) {
                ReflectionHelper.class.wait();
            }
        } catch (InterruptedException unused) {
            f.Log(6, "Interrupted while waiting for all proxies to exit.");
        }
        d = false;
    }

    protected static Object newProxyInstance(long j, Class cls) {
        return newProxyInstance(j, new Class[]{cls});
    }

    protected static void setNativeExceptionOnProxy(Object obj, long j, boolean z) {
        ((b) Proxy.getInvocationHandler(obj)).a(j, z);
    }

    protected static Object newProxyInstance(final long j, final Class[] clsArr) {
        return Proxy.newProxyInstance(ReflectionHelper.class.getClassLoader(), clsArr, new b() { // from class: com.unity3d.player.ReflectionHelper.1
            private long c = ReflectionHelper.b;
            private long d;
            private boolean e;

            private Object a(Object obj, Method method, Object[] objArr) {
                if (objArr == null) {
                    try {
                        objArr = new Object[ReflectionHelper.LOGV];
                    } catch (NoClassDefFoundError unused) {
                        f.Log(6, String.format("Java interface default methods are only supported since Android Oreo", new Object[ReflectionHelper.LOGV]));
                        ReflectionHelper.nativeProxyLogJNIInvokeException(this.d);
                        return null;
                    }
                }
                Class<?> declaringClass = method.getDeclaringClass();
                Constructor declaredConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
                declaredConstructor.setAccessible(true);
                return ((MethodHandles.Lookup) declaredConstructor.newInstance(declaringClass, 2)).in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(obj).invokeWithArguments(objArr);
            }

            @Override // java.lang.reflect.InvocationHandler
            public final Object invoke(Object obj, Method method, Object[] objArr) {
                if (!ReflectionHelper.beginProxyCall(this.c)) {
                    f.Log(6, "Scripting proxy object was destroyed, because Unity player was unloaded.");
                    return null;
                }
                try {
                    this.d = 0L;
                    this.e = false;
                    Object nativeProxyInvoke = ReflectionHelper.nativeProxyInvoke(j, method.getName(), objArr);
                    if (this.e) {
                        if ((method.getModifiers() & 1024) == 0) {
                            Object a2 = a(obj, method, objArr);
                            ReflectionHelper.endProxyCall();
                            return a2;
                        }
                        ReflectionHelper.nativeProxyLogJNIInvokeException(this.d);
                    } else if (this.d != 0) {
                        ReflectionHelper.nativeProxyLogJNIInvokeException(this.d);
                    }
                    ReflectionHelper.endProxyCall();
                    return nativeProxyInvoke;
                } catch (Throwable th) {
                    ReflectionHelper.endProxyCall();
                    throw th;
                }
            }

            @Override // com.unity3d.player.ReflectionHelper.b
            public final void a(long j2, boolean z) {
                this.d = j2;
                this.e = z;
            }

            protected final void finalize() {
                if (ReflectionHelper.beginProxyCall(this.c)) {
                    try {
                        ReflectionHelper.nativeProxyFinalize(j);
                        ReflectionHelper.endProxyCall();
                        super.finalize();
                    } catch (Throwable th) {
                        ReflectionHelper.endProxyCall();
                        super.finalize();
                        throw th;
                    }
                }
            }
        });
    }
}
