package com.oo.games.movement;

enum Direction {
    TOP(0, -1),
    RIGHT(1, 0),
    BOTTOM(0, 1),
    LEFT(-1, 0),
    TOP_LEFT(-1, -1),
    TOP_RIGHT(1, -1),
    BOTTOM_RIGHT(1, 1),
    BOTTOM_LEFT(-1, 1);

    final int deltaX;
    final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
}
