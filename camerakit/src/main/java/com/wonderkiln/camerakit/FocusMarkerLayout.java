//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.wonderkiln.camerakit.core.R.id;
import com.wonderkiln.camerakit.core.R.layout;

public class FocusMarkerLayout extends FrameLayout {
    private FrameLayout mFocusMarkerContainer;
    private ImageView mFill;

    public FocusMarkerLayout(@NonNull Context context) {
        this(context, (AttributeSet)null);
    }

    public FocusMarkerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(this.getContext()).inflate(layout.layout_focus_marker, this);
        this.mFocusMarkerContainer = (FrameLayout)this.findViewById(id.focusMarkerContainer);
        this.mFill = (ImageView)this.findViewById(id.fill);
        this.mFocusMarkerContainer.setAlpha(0.0F);
    }

    public void focus(float mx, float my) {
        mx *= (float)this.getWidth();
        my *= (float)this.getHeight();
        int x = (int)(mx - (float)(this.mFocusMarkerContainer.getWidth() / 2));
        int y = (int)(my - (float)(this.mFocusMarkerContainer.getWidth() / 2));
        this.mFocusMarkerContainer.setTranslationX((float)x);
        this.mFocusMarkerContainer.setTranslationY((float)y);
        this.mFocusMarkerContainer.animate().setListener((AnimatorListener)null).cancel();
        this.mFill.animate().setListener((AnimatorListener)null).cancel();
        this.mFill.setScaleX(0.0F);
        this.mFill.setScaleY(0.0F);
        this.mFill.setAlpha(1.0F);
        this.mFocusMarkerContainer.setScaleX(1.36F);
        this.mFocusMarkerContainer.setScaleY(1.36F);
        this.mFocusMarkerContainer.setAlpha(1.0F);
        this.mFocusMarkerContainer.animate().scaleX(1.0F).scaleY(1.0F).setStartDelay(0L).setDuration(330L).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                FocusMarkerLayout.this.mFocusMarkerContainer.animate().alpha(0.0F).setStartDelay(750L).setDuration(800L).setListener((AnimatorListener)null).start();
            }
        }).start();
        this.mFill.animate().scaleX(1.0F).scaleY(1.0F).setDuration(330L).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                FocusMarkerLayout.this.mFill.animate().alpha(0.0F).setDuration(800L).setListener((AnimatorListener)null).start();
            }
        }).start();
    }
}
