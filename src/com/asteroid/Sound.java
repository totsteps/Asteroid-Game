package com.asteroid;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;

abstract class Sound implements Constants {

  // Sound clips.
  private static Clip crashSound;
  private static Clip explosionSound;
  private static Clip fireSound;
  private static Clip missileSound;
  private static Clip saucerSound;
  private static Clip thrustersSound;
  private static Clip warpSound;

  // Counter and total used to track the loading of the sound clips.
  private static int clipTotal   = 0;
  private static int clipsLoaded = 0;

  static Clip getCrashSound() {
    return crashSound;
  }

  static Clip getExplosionSound() {
    return explosionSound;
  }

  static Clip getFireSound() {
    return fireSound;
  }

  static Clip getMissileSound() {
    return missileSound;
  }

  static Clip getSaucerSound() {
    return saucerSound;
  }

  static Clip getThrustersSound() {
    return thrustersSound;
  }

  static Clip getWarpSound() {
    return warpSound;
  }

  static int getClipTotal() {
    return clipTotal;
  }

  static int getClipsLoaded() {
    return clipsLoaded;
  }

  static void loadSounds(Component component) {
    // Load all sound clips by playing and immediately stopping them. Update
    // counter and total for display.
    try {
      crashSound = AudioSystem.getClip();
      crashSound.open(AudioSystem.getAudioInputStream(new File("sounds/crash.wav")));
      clipTotal++;

      explosionSound = AudioSystem.getClip();
      explosionSound.open(AudioSystem.getAudioInputStream(new File("sounds/explosion.wav")));
      clipTotal++;

      fireSound = AudioSystem.getClip();
      fireSound.open(AudioSystem.getAudioInputStream(new File("sounds/fire.wav")));
      clipTotal++;

      missileSound = AudioSystem.getClip();
      missileSound.open(AudioSystem.getAudioInputStream(new File("sounds/missile.wav")));
      clipTotal++;

      saucerSound = AudioSystem.getClip();
      saucerSound.open(AudioSystem.getAudioInputStream(new File("sounds/saucer.wav")));
      clipTotal++;

      thrustersSound = AudioSystem.getClip();
      thrustersSound.open(AudioSystem.getAudioInputStream(new File("sounds/thrusters.wav")));
      clipTotal++;

      warpSound = AudioSystem.getClip();
      warpSound.open(AudioSystem.getAudioInputStream(new File("sounds/warp.wav")));
      clipTotal++;
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      stopSound(component, crashSound, explosionSound, fireSound);
      stopSound(component, missileSound, saucerSound, thrustersSound);
      warpSound.start();
      warpSound.stop();
      clipsLoaded++;
      component.repaint();
      Thread.sleep(DELAY);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void stopSound(Component component, Clip crashSound, Clip explosionSound, Clip fireSound)
      throws InterruptedException
  {
    crashSound.start();
    crashSound.stop();
    clipsLoaded++;
    component.repaint();
    Thread.sleep(DELAY);

    explosionSound.start();
    explosionSound.stop();
    clipsLoaded++;
    component.repaint();
    Thread.sleep(DELAY);

    fireSound.start();
    fireSound.stop();
    clipsLoaded++;
    component.repaint();
    Thread.sleep(DELAY);
  }
}