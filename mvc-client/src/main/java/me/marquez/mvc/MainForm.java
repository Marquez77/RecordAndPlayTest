package me.marquez.mvc;

import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainForm extends JFrame {
    private JComboBox micComBox;
    private JComboBox speakComBox;
    private JLabel micLabel;
    private JLabel speakLabel;
    private JPanel panel;
    private JButton micTestButton;
    private JButton speakTestButton;

    public MainForm() {
        super("MinecraftVoiceChat");
        setContentPane(panel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setResizable(false);

        micTestButton.addActionListener(new ActionListener() {
            private NewVoiceRecorder recorder;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(recorder == null) {
                    recorder = new NewVoiceRecorder(null);
                    recorder.start();
                }else {
                    recorder.stopRecord();
                }
            }
        });
    }

    public void setMicrophoneList(@Nullable List<String> microphones) {
        micComBox.removeAllItems();
        micComBox.addItem("기본 녹음 장치");
        if(microphones != null) microphones.forEach(micComBox::addItem);
    }

    public void setSpeakerList(@Nullable List<String> speakers) {
        speakComBox.removeAllItems();
        speakComBox.addItem("기본 출력 장치");
        if(speakers != null) speakers.forEach(speakComBox::addItem);
    }

    public String getSelectedMicrophone() {
        return (String)micComBox.getSelectedItem();
    }

    public String getSelectedSpeaker() {
        return (String)speakComBox.getSelectedItem();
    }

    public void addMicrophoneSelectListener(ActionListener listener) {
        micComBox.addActionListener(listener);
    }

    public void addSpeakerSelectListener(ActionListener listener) {
        speakComBox.addActionListener(listener);
    }

}
