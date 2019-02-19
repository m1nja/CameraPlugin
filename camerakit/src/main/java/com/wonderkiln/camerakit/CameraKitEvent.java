//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.os.Bundle;
import android.support.annotation.NonNull;

public class CameraKitEvent {
    public static final String TYPE_ERROR = "CameraKitError";
    public static final String TYPE_CAMERA_OPEN = "CKCameraOpenedEvent";
    public static final String TYPE_CAMERA_CLOSE = "CKCameraStoppedEvent";
    public static final String TYPE_FACING_CHANGED = "CKFacingChangedEvent";
    public static final String TYPE_FLASH_CHANGED = "CKFlashChangedEvent";
    public static final String TYPE_IMAGE_CAPTURED = "CKImageCapturedEvent";
    public static final String TYPE_VIDEO_CAPTURED = "CKVideoCapturedEvent";
    public static final String TYPE_FOCUS_MOVED = "CKFocusMovedEvent";
    public static final String TYPE_TEXT_DETECTED = "CKTextDetectedEvent";
    private String type;
    private String message;
    private Bundle data;

    private CameraKitEvent() {
    }

    CameraKitEvent(@NonNull String type) {
        this.type = type;
        this.data = new Bundle();
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    public String getType() {
        return this.type;
    }

    @NonNull
    public String getMessage() {
        return this.message != null ? this.message : "";
    }

    @NonNull
    public Bundle getData() {
        return this.data != null ? this.data : new Bundle();
    }

    public String toString() {
        return String.format("%s: %s", this.getType(), this.getMessage());
    }
}
