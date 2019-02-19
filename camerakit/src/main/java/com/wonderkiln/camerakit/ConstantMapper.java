//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.annotation.TargetApi;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseIntArray;

public class ConstantMapper {
    public ConstantMapper() {
    }

    @TargetApi(21)
    static class Facing2 extends ConstantMapper.BaseMapper<Integer> {
        private static final SparseIntArray FACING_MODES = new SparseIntArray();

        protected Facing2(int cameraKitConstant) {
            super(cameraKitConstant);
        }

        Integer map() {
            return FACING_MODES.get(this.mCameraKitConstant, FACING_MODES.get(0));
        }

        static {
            FACING_MODES.put(0, 1);
            FACING_MODES.put(1, 0);
        }
    }

    static class Facing extends ConstantMapper.BaseMapper<Integer> {
        private static final SparseArrayCompat<Integer> FACING_MODES = new SparseArrayCompat();

        protected Facing(int cameraKitConstant) {
            super(cameraKitConstant);
        }

        Integer map() {
            return (Integer)FACING_MODES.get(this.mCameraKitConstant, FACING_MODES.get(0));
        }

        static {
            FACING_MODES.put(0, 0);
            FACING_MODES.put(1, 1);
        }
    }

    @TargetApi(21)
    static class Flash2 extends ConstantMapper.BaseMapper<String> {
        protected Flash2(int cameraKitConstant) {
            super(cameraKitConstant);
        }

        String map() {
            return null;
        }
    }

    static class Flash extends ConstantMapper.BaseMapper<String> {
        private static final SparseArrayCompat<String> FLASH_MODES = new SparseArrayCompat();

        protected Flash(int cameraKitConstant) {
            super(cameraKitConstant);
        }

        String map() {
            return (String)FLASH_MODES.get(this.mCameraKitConstant, FLASH_MODES.get(0));
        }

        static {
            FLASH_MODES.put(0, "off");
            FLASH_MODES.put(1, "on");
            FLASH_MODES.put(2, "auto");
            FLASH_MODES.put(3, "torch");
        }
    }

    private abstract static class BaseMapper<T> {
        protected int mCameraKitConstant;

        protected BaseMapper(int cameraKitConstant) {
            this.mCameraKitConstant = cameraKitConstant;
        }

        abstract T map();
    }
}
