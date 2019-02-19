//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.support.annotation.Nullable;

public class CameraKitError extends CameraKitEvent {
    private String type;
    private String message;
    private Exception exception;

    CameraKitError() {
        super("CameraKitError");
    }

    CameraKitError(Exception exception) {
        super("CameraKitError");
        this.exception = exception;
    }

    @Nullable
    public Exception getException() {
        return this.exception;
    }
}
