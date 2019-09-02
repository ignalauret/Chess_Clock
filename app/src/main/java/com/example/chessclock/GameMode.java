package com.example.chessclock;

class GameMode {
    String name;
    boolean symmetric;
    long startTimeP1;
    long startTimeP2;
    long incrementP1;
    long incrementP2;
    int timeControlP1;
    int timeControlP2;


    GameMode(String n, boolean s, long st1, long st2, long i1, long i2, int tc1, int tc2) {
        name = n;
        symmetric = s;
        startTimeP1 = st1;
        startTimeP2 = st2;
        incrementP1 = i1;
        incrementP2 = i2;
        timeControlP1 = tc1;
        timeControlP2 = tc2;
    }

    GameMode(GameMode other) {
        name = other.name;
        symmetric = other.symmetric;
        startTimeP1 = other.startTimeP1;
        startTimeP2 = other.startTimeP2;
        incrementP1 = other.incrementP1;
        incrementP2 = other.incrementP2;
        timeControlP1 = other.timeControlP1;
        timeControlP2 = other.timeControlP2;
    }
}
