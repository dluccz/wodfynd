package word.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.I18NBundle;


import java.util.Map;

import word.game.WordConnectGame;
import word.game.config.GameConfig;
import word.game.i18n.Locale;
import word.game.model.Constants;
import word.game.model.GameData;
import word.game.net.WordMeaningProvider;


public class LanguageManager {

    public static Locale locale;
    public static I18NBundle bundle;
    public static Map<String, WordMeaningProvider> wordMeaningProviderMap;



    public static String getSelectedLocaleCode(){
        if(locale != null)
            return locale.code;

        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getString(Constants.KEY_SELECTED_LANGUAGE, null);
    }



    public static void setLocale(String code, WordConnectGame wordConnectGame){
        Locale newLocale = GameConfig.availableLanguages.get(code);
        newLocale.code = code;
        LanguageManager.locale = newLocale;

        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(Constants.KEY_SELECTED_LANGUAGE, code);
        preferences.flush();
    }


    public static String get(String key){
        return bundle.get(key);
    }


    public static String format(String key, Object... args){
        return bundle.format(key, args);
    }


    public static void updateSelectedLanguage(){
        GameData.readWords();
        GameData.readVulgarWords();
    }


}
