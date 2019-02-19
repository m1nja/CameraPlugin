//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;

public class AspectRatio implements Comparable<AspectRatio>, Parcelable {
    private static final SparseArrayCompat<SparseArrayCompat<AspectRatio>> sCache = new SparseArrayCompat(16);
    private final int mX;
    private final int mY;
    public static final Creator<AspectRatio> CREATOR = new Creator<AspectRatio>() {
        public AspectRatio createFromParcel(Parcel source) {
            int x = source.readInt();
            int y = source.readInt();
            return AspectRatio.of(x, y);
        }

        public AspectRatio[] newArray(int size) {
            return new AspectRatio[size];
        }
    };

    private AspectRatio(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public boolean matches(Size size) {
        int gcd = gcd(size.getWidth(), size.getHeight());
        int x = size.getWidth() / gcd;
        int y = size.getHeight() / gcd;
        return this.mX == x && this.mY == y;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof AspectRatio)) {
            return false;
        } else {
            AspectRatio ratio = (AspectRatio)o;
            return this.mX == ratio.mX && this.mY == ratio.mY;
        }
    }

    public String toString() {
        return this.mX + ":" + this.mY;
    }

    public float toFloat() {
        return (float)this.mX / (float)this.mY;
    }

    public int hashCode() {
        return this.mY ^ (this.mX << 16 | this.mX >>> 16);
    }

    public int compareTo(@NonNull AspectRatio another) {
        if (this.equals(another)) {
            return 0;
        } else {
            return this.toFloat() - another.toFloat() > 0.0F ? 1 : -1;
        }
    }

    public AspectRatio inverse() {
        return of(this.mY, this.mX);
    }

    public static AspectRatio of(int x, int y) {
        int gcd = gcd(x, y);
        x /= gcd;
        y /= gcd;
        SparseArrayCompat<AspectRatio> arrayX = (SparseArrayCompat)sCache.get(x);
        AspectRatio ratio;
        if (arrayX == null) {
            ratio = new AspectRatio(x, y);
            arrayX = new SparseArrayCompat();
            arrayX.put(y, ratio);
            sCache.put(x, arrayX);
            return ratio;
        } else {
            ratio = (AspectRatio)arrayX.get(y);
            if (ratio == null) {
                ratio = new AspectRatio(x, y);
                arrayX.put(y, ratio);
            }

            return ratio;
        }
    }

    private static int gcd(int a, int b) {
        while(b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }

        return a;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mX);
        dest.writeInt(this.mY);
    }
}
