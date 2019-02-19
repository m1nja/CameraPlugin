//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class EventDispatcher {
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private List<CameraKitEventListener> listeners = new ArrayList();
    private List<EventDispatcher.BindingHandler> bindings = new ArrayList();

    public EventDispatcher() {
    }

    public void addListener(CameraKitEventListener listener) {
        this.listeners.add(listener);
    }

    public void addBinding(Object binding) {
        this.bindings.add(new EventDispatcher.BindingHandler(binding));
    }

    public void dispatch(final CameraKitEvent event) {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                Iterator var1 = EventDispatcher.this.listeners.iterator();

                while(var1.hasNext()) {
                    CameraKitEventListener listener = (CameraKitEventListener)var1.next();
                    listener.onEvent(event);
                    if (event instanceof CameraKitError) {
                        listener.onError((CameraKitError)event);
                    }

                    if (event instanceof CameraKitImage) {
                        listener.onImage((CameraKitImage)event);
                    }

                    if (event instanceof CameraKitVideo) {
                        listener.onVideo((CameraKitVideo)event);
                    }
                }

                var1 = EventDispatcher.this.bindings.iterator();

                while(var1.hasNext()) {
                    EventDispatcher.BindingHandler handler = (EventDispatcher.BindingHandler)var1.next();

                    try {
                        handler.dispatchEvent(event);
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }
                }

            }
        });
    }

    private class BindingHandler {
        private Map<Class, List<EventDispatcher.BindingHandler.MethodHolder>> methods = new HashMap();

        public BindingHandler(@NonNull Object binding) {
            Method[] var3 = binding.getClass().getDeclaredMethods();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Method method = var3[var5];
                if (method.isAnnotationPresent(OnCameraKitEvent.class)) {
                    OnCameraKitEvent annotation = (OnCameraKitEvent)method.getAnnotation(OnCameraKitEvent.class);
                    Class<? extends CameraKitEvent> eventType = annotation.value();
                    this.addMethod(binding, method, eventType, this.methods);
                }
            }

        }

        private void addMethod(Object binding, Method method, Class<? extends CameraKitEvent> type, Map<Class, List<EventDispatcher.BindingHandler.MethodHolder>> store) {
            if (!store.containsKey(type)) {
                store.put(type, new ArrayList());
            }

            ((List)store.get(type)).add(new EventDispatcher.BindingHandler.MethodHolder(binding, method));
        }

        public void dispatchEvent(@NonNull CameraKitEvent event) throws IllegalAccessException, InvocationTargetException {
            List<EventDispatcher.BindingHandler.MethodHolder> baseMethods = (List)this.methods.get(CameraKitEvent.class);
            if (baseMethods != null) {
                Iterator var3 = baseMethods.iterator();

                while(var3.hasNext()) {
                    EventDispatcher.BindingHandler.MethodHolder methodHolder = (EventDispatcher.BindingHandler.MethodHolder)var3.next();
                    methodHolder.getMethod().invoke(methodHolder.getBinding(), event);
                }
            }

            List<EventDispatcher.BindingHandler.MethodHolder> targetMethods = (List)this.methods.get(event.getClass());
            if (targetMethods != null) {
                Iterator var7 = targetMethods.iterator();

                while(var7.hasNext()) {
                    EventDispatcher.BindingHandler.MethodHolder methodHolderx = (EventDispatcher.BindingHandler.MethodHolder)var7.next();
                    methodHolderx.getMethod().invoke(methodHolderx.getBinding(), event);
                }
            }

        }

        private class MethodHolder {
            private Object binding;
            private Method method;

            public MethodHolder(Object binding, Method method) {
                this.binding = binding;
                this.method = method;
            }

            public Object getBinding() {
                return this.binding;
            }

            public Method getMethod() {
                return this.method;
            }
        }
    }
}
