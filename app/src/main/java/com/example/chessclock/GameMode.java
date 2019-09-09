package com.example.chessclock;

class GameMode {
    private String[] name = new String[2];
    private String[] predefinedName = new String[2];
    private boolean symmetric;
    private long[] startTime = new long[2];
    private long[] delay = new long[2];
    private long[] increment = new long[2];
    private int[] timeControl = new int[2];


    GameMode(String n1, String n2, String pn1, String pn2, boolean s, long st1, long st2, long d1, long d2, long i1, long i2, int tc1, int tc2) {
        name[0] = n1;
        name[1] = n2;
        predefinedName[0] = pn1;
        predefinedName[1] = pn2;
        symmetric = s;
        startTime[0] = st1;
        startTime[1] = st2;
        delay[0] = d1;
        delay[1] = d2;
        increment[0] = i1;
        increment[1] = i2;
        timeControl[0] = 0; //TODO: Use time control.
        timeControl[1] = 0;
    }

    GameMode(GameMode other) {
        name = other.name;
        predefinedName = other.predefinedName;
        symmetric = other.symmetric;
        startTime = other.startTime;
        delay = other.delay;
        increment = other.increment;
        timeControl = other.timeControl;
    }

    void copyValues(GameMode other, int player) {
        if(player == 1) {
            name[0] = other.name[0];
            predefinedName[0] = other.predefinedName[0];
            symmetric = other.symmetric;
            startTime[0] = other.startTime[0];
            delay[0] = other.delay[0];
            increment[0] = other.increment[0];
            timeControl[0] = other.timeControl[0];
        } else {
            name[1] = other.name[1];
            predefinedName[1] = other.predefinedName[1];
            symmetric = other.symmetric;
            startTime[1] = other.startTime[1];
            delay[1] = other.delay[1];
            increment[1] = other.increment[1];
            timeControl[1] = other.timeControl[1];
        }
    }

    String getName(int player) {
        return name[player-1];
    }

    void setName(int player, String value) {
        name[player-1] = value;
    }

    String getPredefinedName(int player) {
        return predefinedName[player - 1];
    }

    void setPredefinedName(int player, String value) {
        predefinedName[player - 1] = value;
    }

    boolean isSymmetric() {
        return symmetric;
    }

    void setSymmetry(boolean value) {
        symmetric = value;
    }
    long getStartTime(int player) {
        return startTime[player - 1];
    }

    void setStartTime(int player, long value) {
        startTime[player - 1] = value;
    }

    long getDelay(int player) {
        return delay[player - 1];
    }

    void setDelay(int player, long value) {
        delay[player - 1] = value;
    }

    long getIncrement(int player) {
        return increment[player - 1];
    }

    void setIncrement(int player, long value) {
        increment[player - 1] = value;
    }

    int getTimeControl(int player) {
        return timeControl[player - 1];
    }

    void setTimeControl(int player, int value) {
        timeControl[player - 1] = value;
    }
}
