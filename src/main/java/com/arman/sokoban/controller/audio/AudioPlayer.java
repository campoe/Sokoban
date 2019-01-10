package com.arman.sokoban.controller.audio;

import com.arman.sokoban.configure.Configurations;
import com.arman.sokoban.configure.Props;
import com.arman.sokoban.util.FileUtils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {

    private static final int NUM_AUDIO_FILES = Props.NUM_AUDIO_FILES;

    private static Map<String, Clip> clips;
    private static int gap;

    static  {
        clips = new HashMap<>();
        gap = 0;
        load("completed.wav");
        load("navigate.wav");
    }

    private AudioPlayer() {

    }

    public static void load(String name) {
        String s = "audiofiles/" + name;
        if (clips.get(name) != null) return;
        Clip clip;
        try {
            InputStream src = FileUtils.getStream(s);
            InputStream bufferedSrc = new BufferedInputStream(src);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedSrc);
            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
            clip = AudioSystem.getClip();
            clip.open(dais);
            clips.put(name, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void play(String s) {
        play(s, gap);
    }

    public static void play(String s, int i) {
        if (!Configurations.enableSound) return;
        Clip c = clips.get(s);
        if (c == null) return;
        if (c.isRunning()) c.stop();
        c.setFramePosition(i);
        while (!c.isRunning()) c.start();
    }

    public static void stop(String s) {
        if (clips.get(s) == null) return;
        if (clips.get(s).isRunning()) clips.get(s).stop();
    }

    public static void resume(String s) {
        if (!Configurations.enableSound) return;
        if (clips.get(s).isRunning()) return;
        clips.get(s).start();
    }

    public static void loop(String s) {
        loop(s, gap, gap, clips.get(s).getFrameLength() - 1);
    }

    public static void loop(String s, int frame) {
        loop(s, frame, gap, clips.get(s).getFrameLength() - 1);
    }

    public static void loop(String s, int start, int end) {
        loop(s, gap, start, end);
    }

    public static void loop(String s, int frame, int start, int end) {
        stop(s);
        if (!Configurations.enableSound) return;
        clips.get(s).setLoopPoints(start, end);
        clips.get(s).setFramePosition(frame);
        clips.get(s).loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void setPosition(String s, int frame) {
        clips.get(s).setFramePosition(frame);
    }

    public static int getFrames(String s) {
        return clips.get(s).getFrameLength();
    }

    public static int getPosition(String s) {
        return clips.get(s).getFramePosition();
    }

    public static void close(String s) {
        stop(s);
        clips.get(s).close();
    }

}
