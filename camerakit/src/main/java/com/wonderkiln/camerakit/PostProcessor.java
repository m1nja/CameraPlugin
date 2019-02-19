//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Rect;
import android.support.media.ExifInterface;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PostProcessor {
    private byte[] picture;
    private int jpegQuality;
    private int facing;
    private AspectRatio cropAspectRatio;

    public PostProcessor(byte[] picture) {
        this.picture = picture;
    }

    public void setJpegQuality(int jpegQuality) {
        this.jpegQuality = jpegQuality;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public void setCropOutput(AspectRatio aspectRatio) {
        this.cropAspectRatio = aspectRatio;
    }

    public byte[] getJpeg() {
        JpegTransformer jpegTransformer = new JpegTransformer(this.picture);
        int width = jpegTransformer.getWidth();
        int height = jpegTransformer.getHeight();
        PostProcessor.ExifPostProcessor exifPostProcessor = new PostProcessor.ExifPostProcessor(this.picture);
        exifPostProcessor.apply(jpegTransformer);
        if (this.facing == 1) {
            jpegTransformer.flipHorizontal();
        }

        if (this.cropAspectRatio != null) {
            int cropWidth = width;
            int cropHeight = height;
            if (exifPostProcessor.areDimensionsFlipped()) {
                cropWidth = height;
                cropHeight = width;
            }

            (new PostProcessor.CenterCrop(cropWidth, cropHeight, this.cropAspectRatio)).apply(jpegTransformer);
        }

        return jpegTransformer.getJpeg();
    }

    private static class CenterCrop {
        private int width;
        private int height;
        private AspectRatio aspectRatio;

        public CenterCrop(int width, int height, AspectRatio aspectRatio) {
            this.width = width;
            this.height = height;
            this.aspectRatio = aspectRatio;
        }

        public void apply(JpegTransformer transformer) {
            Rect crop = getCrop(this.width, this.height, this.aspectRatio);
            transformer.crop(crop);
        }

        private static Rect getCrop(int currentWidth, int currentHeight, AspectRatio targetRatio) {
            AspectRatio currentRatio = AspectRatio.of(currentWidth, currentHeight);
            Rect crop;
            int width;
            int widthOffset;
            if (currentRatio.toFloat() > targetRatio.toFloat()) {
                width = (int)((float)currentHeight * targetRatio.toFloat());
                widthOffset = (currentWidth - width) / 2;
                crop = new Rect(widthOffset, 0, currentWidth - widthOffset, currentHeight);
            } else {
                width = (int)((float)currentWidth * targetRatio.inverse().toFloat());
                widthOffset = (currentHeight - width) / 2;
                crop = new Rect(0, widthOffset, currentWidth, currentHeight - widthOffset);
            }

            return crop;
        }
    }

    private static class ExifPostProcessor {
        private int orientation = 0;

        public ExifPostProcessor(byte[] picture) {
            try {
                this.orientation = getExifOrientation(new ByteArrayInputStream(picture));
            } catch (IOException var3) {
                var3.printStackTrace();
            }

        }

        public void apply(JpegTransformer transformer) {
            switch(this.orientation) {
                case 0:
                case 1:
                default:
                    break;
                case 2:
                    transformer.flipHorizontal();
                    break;
                case 3:
                    transformer.rotate(180);
                    break;
                case 4:
                    transformer.flipVertical();
                    break;
                case 5:
                    transformer.rotate(90);
                    transformer.flipHorizontal();
                    break;
                case 6:
                    transformer.rotate(90);
                    break;
                case 7:
                    transformer.rotate(270);
                    transformer.flipHorizontal();
                    break;
                case 8:
                    transformer.rotate(90);
            }

        }

        public boolean areDimensionsFlipped() {
            switch(this.orientation) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    return false;
                case 5:
                case 6:
                case 7:
                case 8:
                    return true;
                default:
                    return false;
            }
        }

        private static int getExifOrientation(InputStream inputStream) throws IOException {
            ExifInterface exif = new ExifInterface(inputStream);
            return exif.getAttributeInt("Orientation", 1);
        }
    }
}
