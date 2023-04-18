package bitter.jnibridge;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: unity-classes.jar:bitter/jnibridge/JNIBridge.class */
public class JNIBridge {

    /* loaded from: unity-classes.jar:bitter/jnibridge/JNIBridge$a.class */
    private static class a implements InvocationHandler {
        private Object a = new Object[0];
        private long b;
        private Constructor c;

        public a(long j) {
            this.b = j;
            try {
                this.c = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
                this.c.setAccessible(true);
            } catch (NoClassDefFoundError unused) {
                this.c = null;
            } catch (NoSuchMethodException unused2) {
                this.c = null;
            }
        }

        private Object a(Object obj, Method method, Object[] objArr) {
            if (objArr == null) {
                objArr = new Object[0];
            }
            Class<?> declaringClass = method.getDeclaringClass();
            return ((MethodHandles.Lookup) this.c.newInstance(declaringClass, 2)).in(declaringClass).unreflectSpecial(method, declaringClass).bindTo(obj).invokeWithArguments(objArr);
        }

        @Override // java.lang.reflect.InvocationHandler
        public final Object invoke(Object obj, Method method, Object[] objArr) {
            synchronized (this.a) {
                if (this.b == 0) {
                    return null;
                }
                try {
                    return JNIBridge.invoke(this.b, method.getDeclaringClass(), method, objArr);
                } catch (NoSuchMethodError e) {
                    if (this.c == null) {
                        System.err.println("JNIBridge error: Java interface default methods are only supported since Android Oreo");
                        throw e;
                    } else if ((method.getModifiers() & 1024) == 0) {
                        return a(obj, method, objArr);
                    } else {
                        throw e;
                    }
                }
            }
        }

        public final void finalize() {
            synchronized (this.a) {
                if (this.b == 0) {
                    return;
                }
                JNIBridge.delete(this.b);
            }
        }

        public final void a() {
            synchronized (this.a) {
                this.b = 0L;
            }
        }
    }

    static native Object invoke(long j, Class cls, Method method, Object[] objArr);

    static native void delete(long j);

    static Object newInterfaceProxy(long j, Class[] clsArr) {
        return Proxy.newProxyInstance(JNIBridge.class.getClassLoader(), clsArr, new a(j));
    }

    static void disableInterfaceProxy(Object obj) {
        if (obj != null) {
            ((a) Proxy.getInvocationHandler(obj)).a();
        }
    }
}
