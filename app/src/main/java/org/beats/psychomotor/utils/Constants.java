package org.beats.psychomotor.utils;

import android.graphics.drawable.Drawable;

import org.beats.psychomotor.R;
import org.beats.psychomotor.model.Assessment;
import org.beats.psychomotor.model.Client;

public abstract class Constants {
    public static final int MOVE_FOLD = 0;
    public static final int UPDATE_GAME_NAME = 1;
    public static final int GAME_PLAY = 2;
    public static final int PLAYER_LIST_UPDATE = 3;
    public static final int NEW_GAME = 4;
    public static final int SEND_BLOCK = 5;
    public static final int FINISH = 99;
    public static final String ACTION_KEY = "action";
    public static final String DATA_KEY = "data";

    public static boolean isPlayerActive(String userName, Assessment gameObject) {
        for (int i = 0; i < gameObject.getClients().size(); i++) {
            Client play = gameObject.getClients().get(i);
            if (play.username.equals(userName) && play.isActive) {
                return true;
            }
        }
        return false;
    }

    public static class Color {
        public static int RED = R.drawable.rectangle_r;
        public static int BLUE = R.drawable.rectangle_b;
        public static int YELLOW = R.drawable.rectangle_y;
        public static int WHITE = R.drawable.rectangle;

    }

    public static class Url {
        public static String block_url = "http://beats.sid-indonesia.org/psychomotor/saveblock";
        public static String assessment_url = "http://beats.sid-indonesia.org/psychomotor/saveassessment";
    }
}
