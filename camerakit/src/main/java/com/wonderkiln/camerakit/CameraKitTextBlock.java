//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.vision.text.TextBlock;

public class CameraKitTextBlock {
    private TextBlock textBlock;

    CameraKitTextBlock(TextBlock textBlock) {
        this.textBlock = textBlock;
    }

    public String getText() {
        return this.textBlock.getValue();
    }

    public Rect getBoundingBox() {
        return this.textBlock.getBoundingBox();
    }

    public Point[] getCornerPoints() {
        return this.textBlock.getCornerPoints();
    }

    public String getLanguage() {
        return this.textBlock.getLanguage();
    }
}
