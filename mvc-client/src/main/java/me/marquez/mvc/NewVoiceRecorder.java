package me.marquez.mvc;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class NewVoiceRecorder extends Thread{
    public static Logger logger = Logger.getLogger(NewVoiceRecorder.class.getName());
    // record microphone && generate stream/byte array
    private AudioInputStream audioInputStream;
    private AudioFormat format;
    public TargetDataLine line;
    private double duration;

    private boolean recording = true;

    private static final int FREQUENCY = 16000;
    private static final int SAMPLES = 1024;

    private String deviceName;

    public NewVoiceRecorder(String deviceName) {
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
        final PipedOutputStream out = new PipedOutputStream();
        final PipedInputStream in = new PipedInputStream();
        try {
            in.connect(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int frameSizeInBytes = format.getFrameSize();
        final int bufferLengthInFrames = line.getBufferSize() / 8;
        final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        final byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;
        line.start();
        logger.info("라인을 시작함");

        AtomicReference<Clip> clip = null;
        new Thread(() -> {
            try {
                clip.set(AudioSystem.getClip());
                clip.get().open(new AudioInputStream(in, format, bufferLengthInBytes));
                clip.get().start();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        while (recording) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
            try {
                out.write(data, 0, numBytesRead);
//                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("여기는 오나?");
        // we reached the end of the stream. stop and close the line.
        line.stop();
        line.close();
        line = null;
        logger.info("라인을 종료함");

        // stop and close the output stream
        try {
//            out.flush();
            out.close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        // load bytes into the audio input stream for playback
//        final byte audioBytes[] = out.toByteArray();
//        logger.info("Out Stream을 갖어옮");
//
//        logger.info(new String(audioBytes, StandardCharsets.UTF_8));
//        final ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
//        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
//
//        logger.info("오디오 인풋 스트림 저장");
//
//        final long milliseconds = (long) ((audioInputStream.getFrameLength()* 1000) / format.getFrameRate());
//        duration = milliseconds / 1000.0;
//        System.out.println(duration);
//        try {
//            audioInputStream.reset();
//            System.out.println("resetting...");
//        } catch (final Exception ex) {
//            ex.printStackTrace();
//            return;
//        }
//
//        System.out.println("재생");
//        File file = new File("test.wav");
//        try {
//            AudioSystem.write(getAudioInputStream(), AudioFileFormat.Type.WAVE,file);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        try {
//            Clip clip = AudioSystem.getClip();
//            clip.open(getAudioInputStream());
//            clip.start();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
