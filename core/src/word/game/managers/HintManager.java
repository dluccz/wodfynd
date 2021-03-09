package word.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import word.game.config.GameConfig;
import word.game.model.Constants;


public class HintManager {


    public static int getRemainingCoins(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(Constants.KEY_COIN_COUNT, GameConfig.DEFAULT_COIN_COUNT);
    }




    public static void setCoinCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_COIN_COUNT, count);
        preferences.flush();
    }




    public static int getRemainingSingleRandomRevealCount(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(Constants.KEY_SINGLE_RANDOM_REVEAL_COUNT, GameConfig.DEFAULT_SINGLE_RANDOM_REVEAL_COUNT);
    }



    public static void setSingleRandomRevealCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_SINGLE_RANDOM_REVEAL_COUNT, count);
        preferences.flush();
    }




    public static int getRemainingFingerRevealCount(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(Constants.KEY_FINGER_REVEAL_COUNT, GameConfig.DEFAULT_FINGER_REVEAL_COUNT);
    }




    public static void setFingerHintRevealCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_FINGER_REVEAL_COUNT, count);
        preferences.flush();
    }



    public static int getRemainingMultiRandomRevealCount(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(Constants.KEY_MULTI_RANDOM_REVEAL_COUNT, GameConfig.DEFAULT_MULTI_RANDOM_REVEAL_COUNT);
    }



    public static void setMultiRandomRevealCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_MULTI_RANDOM_REVEAL_COUNT, count);
        preferences.flush();
    }


    public static int getRemainingRocketRevealCount(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(Constants.KEY_ROCKET_REVEAL_COUNT, GameConfig.DEFAULT_ROCKET_REVEAL_COUNT);
    }



    public static void setRocketRevealCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_ROCKET_REVEAL_COUNT, count);
        preferences.flush();
    }
}
