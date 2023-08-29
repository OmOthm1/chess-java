package com.oo.games.gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private final Map<Sound, Clip> clips;

    SoundManager() {
        clips = new HashMap<>();

        try {
            for (Sound sound : Sound.values()) {
                addAudio(sound, "src/main/resources/sound/" + sound.fileName);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void addAudio(Sound sound, String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream moveAudioIn = AudioSystem.getAudioInputStream(new File(path));
        Clip moveClip = AudioSystem.getClip();
        moveClip.open(moveAudioIn);
        clips.put(sound, moveClip);
    }

    void play(Sound sound) {
        Clip clip = clips.get(sound);
        clip.setFramePosition(0);
        clip.start();
    }

    void playMoveSound() {
        play(Sound.MOVE);
    }

    void playCaptureSound() {
        play(Sound.CAPTURE);
    }

    enum Sound {
        MOVE("Move.wav"),
        CAPTURE("Capture.wav");

        final String fileName;

        Sound(String fileName) {
            this.fileName = fileName;
        }
    }

}
