package com.asteroid;

/**
 * Game object responsible for initiating the game.
 */
public class Game {
  private Screen screen;

  private Game() {
    screen = new Screen();
  }

  public static void main(String[] args) {
    // start the game
    new Game().screen.start();
  }
}