package word.game.model;

public class Constants {

    public static final String PREFS_NAME                           = "wordconnect";

    public static final String KEY_TUTORIAL_STEP                    = "KEY_TUTORIAL_STEP";
    public static final String KEY_LAST_INCOMPLETE_LEVEL            = "KEY_LAST_INCOMPLETE_LEVEL";
    public static final String KEY_SAVED_SOLVED_WORDS               = "KEY_SAVED_SOLVED_WORDS";
    public static final String KEY_TILE_STATE                       = "KEY_TILE_STATE";
    public static final String KEY_ROCKET_WORDS                     = "KEY_ROCKET_WORDS";
    public static final String KEY_EXTRA_WORDS                      = "KEY_EXTRA_WORDS";
    public static final String KEY_EXTRA_WORD_COUNT                 = "KEY_EXTRA_WORD_COUNT";
    public static final String KEY_COMBO_COUNT                      = "KEY_COMBO_COUNT";
    public static final String KEY_COMBO_REWARD                     = "KEY_COMBO_REWARD";

    public static final String KEY_NUMBER_OF_BOMB_MOVES             = "KEY_NUMBER_OF_BOMB_MOVES";
    public static final String KEY_NUMBER_OF_GOLD_PACK_MOVES        = "KEY_NUMBER_OF_GOLD_PACK";

    public static final String KEY_UFO_CONSUMED                     = "KEY_UFO_CONSUMED";
    public static final String KEY_MONSTER_CONSUMED                 = "KEY_MONSTER_CONSUMED";
    public static final String KEY_BOMB_CONSUMED                    = "KEY_BOMB_CONSUMED";
    public static final String KEY_GOLD_PACK_CONSUMED               = "KEY_GOLD_PACK_CONSUMED";

    public static final String KEY_SELECTED_LANGUAGE                = "KEY_SELECTED_LANGUAGE";
    public static final String KEY_LAST_WHEEL_SPIN_TIME             = "KEY_LAST_WHEEL_SPIN_TIME";

    public static final String KEY_COIN_COUNT                       = "KEY_COIN_COUNT";
    public static final String KEY_SINGLE_RANDOM_REVEAL_COUNT       = "KEY_SINGLE_RANDOM_HINT_COUNT";
    public static final String KEY_FINGER_REVEAL_COUNT              = "KEY_FINGER_REVEAL_COUNT";
    public static final String KEY_MULTI_RANDOM_REVEAL_COUNT        = "KEY_MULTI_RANDOM_REVEAL_COUNT";
    public static final String KEY_ROCKET_REVEAL_COUNT              = "KEY_ROCKET_REVEAL_COUNT";

    public static final String KEY_DONT_SHOW_AGAIN                  = "KEY_DONT_SHOW_AGAIN";
    public static final String KEY_APP_LAUNCH_COUNT                 = "KEY_APP_LAUNCH_COUNT";
    public static final String KEY_APP_FIRST_LAUNCH_DATE            = "KEY_APP_FIRST_LAUNCH_DATE";

    public static final int MAX_LETTERS                             = 8;
    public static final int MIN_LETTERS                             = 3;

    public static final int TILE_STATE_DEFAULT                      = 0;
    public static final int TILE_STATE_SOLVED                       = 1;
    public static final int TILE_STATE_REVEALED                     = 2;
    public static final int TILE_STATE_COINED                       = 4;
    public static final int TILE_STATE_BOMBED                       = 5;
    public static final int TILE_STATE_GOLD_PACKED                  = 6;
    public static final int TILE_STATE_UFO                          = 7;
    public static final int TILE_STATE_MONSTER                      = 8;

    public static final String KEY_BOOSTER_TYPE                     = "KEY_BOOSTER_TYPE";


    public static final float RENDER_DELAY_FINAL_WORD_ANIM          = 0.5f;


    public static final String KEY_MUTED                            = "KEY_MUTED";


    public static final int TUTORIAL_PLAY_BUTTON                    = 1;
    public static final int TUTORIAL_DIAL                           = 2;
    public static final int TUTORIAL_SHUFFLE                        = 3;
    public static final int TUTORIAL_SINGLE_RANDOM_HINT             = 4;
    public static final int TUTORIAL_FINGER_HINT                    = 5;
    public static final int TUTORIAL_MULTI_RANDOM                   = 6;
    public static final int TUTORIAL_ROCKET                         = 7;



    public static final int TUTORIAL_DIAL_LEVEL                     = 0;
    public static final int TUTORIAL_SHUFFLE_LEVEL                  = 1;
    public static final int TUTORIAL_SINGLE_RANDOM_LEVEL            = 2;
    public static final int TUTORIAL_FINGER_LEVEL                   = 3;
    public static final int TUTORIAL_MULTI_RANDOM_LEVEL             = 4;
    //rocket tutorial must be the last UI tutorial for efficiency
    public static final int TUTORIAL_ROCKET_LEVEL                   = 10;

    public static final String KEY_UFO_TUTORIAL_SHOWN               = "KEY_UFO_TUTORIAL_SHOWN";
    public static final String KEY_BOMB_TUTORIAL_SHOWN              = "KEY_BOMB_TUTORIAL_SHOWN";
    public static final String KEY_GOLD_PACK_TUTORIAL_SHOWN         = "KEY_GOLD_PACK_TUTORIAL_SHOWN";
    public static final String KEY_MONSTER_TUTORIAL_SHOWN           = "KEY_MONSTER_TUTORIAL_SHOWN";
    public static final String KEY_BONUS_WORDS_TUTORIAL_SHOWN1      = "KEY_BONUS_WORDS_TUTORIAL_SHOWN1";
    public static final String KEY_BONUS_WORDS_TUTORIAL_SHOWN2      = "KEY_BONUS_WORDS_TUTORIAL_SHOWN2";

    public static final String KEY_LAST_REWARDED_AD_TIME            = "KEY_LAST_REWARDED_AD_TIME";



    public static final int ALERT_DIALOG_IAP_ERROR1                 = 1;
    public static final int ALERT_DIALOG_IAP_ERROR2                 = 2;
    public static final int ALERT_DIALOG_NOTIFICATION               = 3;
    public static final int ALERT_DIALOG_BOMB_BLAST                 = 4;
    public static final int CONFIRM_DIALOG_BACK_PRESS               = 5;
    public static final int CONFIRM_DIALOG_RATE_US                  = 6;
    public static final int WHEEL_DIALOG                            = 7;
}
