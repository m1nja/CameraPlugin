//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.support.annotation.NonNull;

public class Size implements Comparable<Size> {
    private final int mWidth;
    private final int mHeight;

    public Size(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof Size)) {
            return false;
        } else {
            Size size = (Size)o;
            return this.mWidth == size.mWidth && this.mHeight == size.mHeight;
        }
    }

    public String toString() {
        return this.mWidth + "x" + this.mHeight;
    }

    public int hashCode() {
        return this.mHeight ^ (this.mWidth << 16 | this.mWidth >>> 16);
    }

    public int compareTo(@NonNull Size another) {
        return this.mWidth * this.mHeight - another.mWidth * another.mHeight;
    }
}
