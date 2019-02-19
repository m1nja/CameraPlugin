//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

public class CameraKitTextDetect extends CameraKitEvent {
    private CameraKitTextBlock textBlock;

    public CameraKitTextDetect(CameraKitTextBlock textBlock) {
        super("CKTextDetectedEvent");
        this.textBlock = textBlock;
    }

    public CameraKitTextBlock getTextBlock() {
        return this.textBlock;
    }
}
