package com.example.chessclock;

final class Constants {

    /* ============================== Modifiable Values ============================== */

    /* === Time === */
    static final long TICK_TIME = 100;
    static final long DEFAULT_STARTING_TIME = 3000;

    /* === Layout === */
    /* Colors */
    static final int IDLE_PLAYER_COLOR = 0xFFaec2e0;
    static final int ACTIVE_PLAYER_COLOR = 0xFFec5855;
    static final int ACTIVE_PLAYER_LOW_COLOR = 0xFFd12f1a;
    static final int WHITE_COLOR = 0xFFFFFFFF;
    static final int BLACK_COLOR = 0xFF000000;

    /* Icons */
    static final int PLAY_ICON = R.drawable.ic_my_play_icon;
    static final int PAUSE_ICON = R.drawable.ic_my_pause_icon;

    /* ============================== Not Modifiable Values ============================== */

    static final int SPEECH_REQUEST_CODE = 9001;

    static final String SAME_TIME_KEY = "same_time";
    /* Same time category keys */
    static final String STANDARD_TIME_CATEGORY_KEY = "standard_time_category";
    static final String STANDARD_GAME_MODE_KEY = "standard_game_mode";
    static final String STANDARD_STARTING_TIME_KEY = "standard_starting_time";
    static final String STANDARD_INCREMENT_KEY = "standard_increment";
    static final String STANDARD_TIME_CONTROL_KEY = "standard_time_control";

    /* Whites time category keys */
    static final String WHITE_TIME_CATEGORY_KEY = "white_time_category";
    static final String WHITE_STARTING_TIME_KEY = "white_starting_time";
    static final String WHITE_INCREMENT_KEY = "white_increment";
    static final String WHITE_TIME_CONTROL_KEY = "white_time_control";

    /* Blacks time category keys */
    static final String BLACK_TIME_CATEGORY_KEY = "black_time_category";
    static final String BLACK_STARTING_TIME_KEY = "black_starting_time";
    static final String BLACK_INCREMENT_KEY = "black_increment";
    static final String BLACK_TIME_CONTROL_KEY = "black_time_control";

    /* Game modes */
    private static final GameMode BLITZ_3MIN = new GameMode("blitz_3min",true,180000,180000,2000,2000,0,0);
    private static final GameMode BLITZ_1MIN = new GameMode("blitz_1min",true,60000,60000,2000,2000,0,0);
    private static final GameMode RAPID_5MIN = new GameMode("rapid_5min",true,300000,300000,0,0,0,0);
    private static final GameMode RAPID_10MIN = new GameMode("rapid_10min",true,600000,600000,0,0,0,0);

    static final GameMode[] gameModes = new GameMode[]{BLITZ_1MIN,BLITZ_3MIN,RAPID_5MIN,RAPID_10MIN};
}

