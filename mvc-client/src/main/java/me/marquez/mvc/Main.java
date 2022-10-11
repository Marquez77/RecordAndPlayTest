package me.marquez.mvc;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALUtil;

import java.util.Collections;
import java.util.Optional;

public class Main {

    public void run() {
        MainForm form = new MainForm();

        form.setMicrophoneList(Optional.ofNullable(ALUtil.getStringList(0L, ALC11.ALC_CAPTURE_DEVICE_SPECIFIER)).orElseGet(Collections::emptyList).stream().map(name -> name.replace("OpenAL Soft on ", "")).toList());
        form.setSpeakerList(Optional.ofNullable(ALUtil.getStringList(0L, ALC11.ALC_ALL_DEVICES_SPECIFIER)).orElseGet(Collections::emptyList).stream().map(name -> name.replace("OpenAL Soft on ", "")).toList());

        form.setVisible(true);
        System.out.println(ALC10.alcGetString(0, ALC10.ALC_DEVICE_SPECIFIER));
        form.addMicrophoneSelectListener(event -> {
            var microphone = form.getSelectedMicrophone();
            if(microphone.equalsIgnoreCase("기본 녹음 장치")) {
                microphone = null;
            }else microphone = "OpenAL Soft";
            long device = ALC10.alcOpenDevice(microphone);
            System.out.println(device);
        });
    }


    public static void main(String[] args) {
        new Main().run();
    }

}