package me.marquez.mvc;

import org.lwjgl.BufferUtils;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC11.*;

public class VoiceRecorder extends Thread{
    public static Logger logger = Logger.getLogger(VoiceRecorder.class.getName());
    // record microphone && generate stream/byte array
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    public TargetDataLine line;
    private double duration;

    private boolean recording = true;

    private static final int FREQUENCY = 16000;
    private static final int SAMPLES = 1024;

    private String deviceName;

    public VoiceRecorder(String deviceName) {
        this.deviceName = deviceName;
        format = new AudioFormat(FREQUENCY,16,1, true, false);
    }

    public void stopRecord() {
        recording = false;
    }

    @Override
    public void run() {
        duration = 0;
        line = getTargetDataLineForRecord();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final int frameSizeInBytes = format.getFrameSize();
        final int bufferLengthInFrames = line.getBufferSize() / 8;
        final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        final byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;
        line.start();
        logger.info("라인을 시작함");

        while (recording) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
//            logger.info(new String(data, StandardCharsets.UTF_8));
            out.write(data, 0, numBytesRead);
        }
        logger.info("여기는 오나?");
        // we reached the end of the stream. stop and close the line.
        line.stop();
        line.close();
        line = null;
        logger.info("라인을 종료함");

        // stop and close the output stream
        try {
            out.flush();
            out.close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        // load bytes into the audio input stream for playback
        final byte audioBytes[] = out.toByteArray();
        logger.info("Out Stream을 갖어옮");

        logger.info(new String(audioBytes, StandardCharsets.UTF_8));
        final ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        logger.info("오디오 인풋 스트림 저장");

        final long milliseconds = (long) ((audioInputStream.getFrameLength()* 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;
        System.out.println(duration);
        try {
            audioInputStream.reset();
            System.out.println("resetting...");
        } catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }
        
        System.out.println("재생");
//        File file = new File("test.wav");
//        try {
//            AudioSystem.write(getAudioInputStream(), AudioFileFormat.Type.WAVE,file);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(getAudioInputStream());
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private TargetDataLine getTargetDataLineForRecord() {
        TargetDataLine line;
        final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            return null;
        }
        // get and open the target data line for capture.
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (final Exception ex) {
            return null;
        }
        return line;
    }
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }
    public AudioFormat getFormat() {
        return format;
    }
    public void setFormat(AudioFormat format) {
        this.format = format;
    }
    public double getDuration() {
        return duration;
    }
}
