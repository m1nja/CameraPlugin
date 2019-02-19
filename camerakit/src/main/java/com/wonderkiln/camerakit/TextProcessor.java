//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector.Detections;
import com.google.android.gms.vision.Detector.Processor;
import com.google.android.gms.vision.text.TextBlock;

public class TextProcessor implements Processor<TextBlock> {
    private EventDispatcher mEventDispatcher;
    private CameraKitEventCallback<CameraKitTextDetect> callback;

    public TextProcessor(EventDispatcher mEventDispatcher, CameraKitEventCallback<CameraKitTextDetect> callback) {
        this.mEventDispatcher = mEventDispatcher;
        this.callback = callback;
    }

    public void release() {
    }

    public void receiveDetections(Detections<TextBlock> detections) {
        SparseArray<TextBlock> detectedItems = detections.getDetectedItems();

        for(int i = 0; i < detectedItems.size(); ++i) {
            TextBlock item = (TextBlock)detectedItems.valueAt(i);
            if (item != null && item.getValue() != null) {
                CameraKitTextDetect event = new CameraKitTextDetect(new CameraKitTextBlock(item));
                this.mEventDispatcher.dispatch(event);
                this.callback.callback(event);
            }
        }

    }
}
