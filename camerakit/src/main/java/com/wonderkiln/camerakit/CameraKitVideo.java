//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import java.io.File;

public class CameraKitVideo extends CameraKitEvent {
    private File videoFile;

    CameraKitVideo(File videoFile) {
        super("CKVideoCapturedEvent");
        this.videoFile = videoFile;
    }

    public File getVideoFile() {
        return this.videoFile;
    }
}
