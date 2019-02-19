//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.OrientationEventListener;

public abstract class DisplayOrientationDetector {
    private final OrientationEventListener mOrientationEventListener;
    static final SparseIntArray DISPLAY_ORIENTATIONS = new SparseIntArray();
    private Display mDisplay;
    private int mLastKnownDisplayOrientation = 0;
    private int mLastKnownDeviceOrientation = 0;

    public DisplayOrientationDetector(Context context) {
        this.mOrientationEventListener = new OrientationEventListener(context) {
            private int mLastKnownDisplayRotation = -1;

            public void onOrientationChanged(int orientation) {
                if (orientation != -1 && DisplayOrientationDetector.this.mDisplay != null) {
                    boolean displayOrDeviceOrientationChanged = false;
                    int displayRotation = DisplayOrientationDetector.this.mDisplay.getRotation();
                    if (this.mLastKnownDisplayRotation != displayRotation) {
                        this.mLastKnownDisplayRotation = displayRotation;
                        displayOrDeviceOrientationChanged = true;
                    }

                    short deviceOrientation;
                    if (orientation >= 60 && orientation <= 140) {
                        deviceOrientation = 270;
                    } else if (orientation >= 140 && orientation <= 220) {
                        deviceOrientation = 180;
                    } else if (orientation >= 220 && orientation <= 300) {
                        deviceOrientation = 90;
                    } else {
                        deviceOrientation = 0;
                    }

                    if (DisplayOrientationDetector.this.mLastKnownDeviceOrientation != deviceOrientation) {
                        DisplayOrientationDetector.this.mLastKnownDeviceOrientation = deviceOrientation;
                        displayOrDeviceOrientationChanged = true;
                    }

                    if (displayOrDeviceOrientationChanged) {
                        DisplayOrientationDetector.this.dispatchOnDisplayOrDeviceOrientationChanged(DisplayOrientationDetector.DISPLAY_ORIENTATIONS.get(displayRotation));
                    }

                }
            }
        };
    }

    public void enable(Display display) {
        this.mDisplay = display;
        this.mOrientationEventListener.enable();
        this.dispatchOnDisplayOrDeviceOrientationChanged(DISPLAY_ORIENTATIONS.get(display.getRotation()));
    }

    public void disable() {
        this.mOrientationEventListener.disable();
        this.mDisplay = null;
    }

    public int getLastKnownDisplayOrientation() {
        return this.mLastKnownDisplayOrientation;
    }

    void dispatchOnDisplayOrDeviceOrientationChanged(int displayOrientation) {
        this.mLastKnownDisplayOrientation = displayOrientation;
        if (this.mOrientationEventListener.canDetectOrientation()) {
            this.onDisplayOrDeviceOrientationChanged(displayOrientation, this.mLastKnownDeviceOrientation);
        } else {
            this.onDisplayOrDeviceOrientationChanged(displayOrientation, displayOrientation);
        }

    }

    public abstract void onDisplayOrDeviceOrientationChanged(int var1, int var2);

    static {
        DISPLAY_ORIENTATIONS.put(0, 0);
        DISPLAY_ORIENTATIONS.put(1, 90);
        DISPLAY_ORIENTATIONS.put(2, 180);
        DISPLAY_ORIENTATIONS.put(3, 270);
    }
}
