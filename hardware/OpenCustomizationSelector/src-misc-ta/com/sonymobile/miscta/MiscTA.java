package com.sonymobile.miscta;

public final class MiscTA {
    private static final String TAG = "MiscTA";

    private static native byte[] readRAW(int i);

    private static native void writeRAW(int i, byte[] bArr);

    private MiscTA() {
    }

    static {
        System.loadLibrary("MiscTAApi.sony");
    }

    public static byte[] read(int unit) {
        return readRAW(unit);
    }

    public static void write(int unit, byte[] data) {
        writeRAW(unit, data);
    }
}
