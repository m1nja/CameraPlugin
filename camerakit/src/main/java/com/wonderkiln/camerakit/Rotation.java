//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

public class Rotation {
    private byte[] rotatedYuv;

    public Rotation(byte[] yuv, int width, int height, int rotation) {
        if (rotation == 0) {
            this.rotatedYuv = yuv;
        }

        if (rotation % 90 == 0 && rotation >= 0 && rotation <= 270) {
            byte[] output = new byte[yuv.length];
            int frameSize = width * height;
            boolean swap = rotation % 180 != 0;
            boolean xflip = rotation % 270 != 0;
            boolean yflip = rotation >= 180;

            for(int j = 0; j < height; ++j) {
                for(int i = 0; i < width; ++i) {
                    int yIn = j * width + i;
                    int uIn = frameSize + (j >> 1) * width + (i & -2);
                    int vIn = uIn + 1;
                    int wOut = swap ? height : width;
                    int hOut = swap ? width : height;
                    int iSwapped = swap ? j : i;
                    int jSwapped = swap ? i : j;
                    int iOut = xflip ? wOut - iSwapped - 1 : iSwapped;
                    int jOut = yflip ? hOut - jSwapped - 1 : jSwapped;
                    int yOut = jOut * wOut + iOut;
                    int uOut = frameSize + (jOut >> 1) * wOut + (iOut & -2);
                    int vOut = uOut + 1;
                    output[yOut] = (byte)(255 & yuv[yIn]);
                    output[uOut] = (byte)(255 & yuv[uIn]);
                    output[vOut] = (byte)(255 & yuv[vIn]);
                }
            }

            this.rotatedYuv = output;
        } else {
            throw new IllegalArgumentException("0 <= rotation < 360, rotation % 90 == 0");
        }
    }

    public byte[] getYuv() {
        return this.rotatedYuv;
    }
}
