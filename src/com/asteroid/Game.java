package com.asteroid;

public class Game {

  private Screen screen;

  private Game() {
    screen = new Screen();
  }

  public static void main(String[] args) {
    new Game().screen.start();
  }
}