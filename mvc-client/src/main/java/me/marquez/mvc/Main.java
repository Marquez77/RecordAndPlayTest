package me.marquez.mvc;

import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;

public class Main {
    public static void main(String[] args) {
        System.out.println(ALUtil.getStringList(0L, ALC11.ALC_CAPTURE_DEVICE_SPECIFIER));
    }
}
