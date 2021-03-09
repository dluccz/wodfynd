package word.game.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.Calendar;

import word.game.WordConnectGame;
import word.game.ui.calendar.Date;

public class ConfigProcessor {


    /**
     * There is no configuration option in this file. It serves to make calculations for GameConfig.java and UIConfig.java.
     */


    public static boolean muted;



    public static Color getLevelColor(int levelIndex){
        int index = levelIndex % UIConfig.levelColors.length;

        if(index >= 0){
            return UIConfig.levelColors[index];
        }

        return Color.WHITE;
    }





    public static boolean isMenuEnabled(boolean gdpr, boolean hasManyLocale){
        return findTotalEnabledMenuRows(gdpr, hasManyLocale) > 0;
    }



    public static int findTotalEnabledMenuRows(boolean gdpr, boolean hasManyLocale){
        int enabledItems = 0;

        if(UIConfig.MENU_ITEM_GDPR_ENABLED && gdpr) enabledItems++;
        if(UIConfig.MENU_ITEM_RATE_US_ENABLED) enabledItems++;
        if(UIConfig.MENU_ITEM_CONTACT_US_ENABLED) enabledItems++;
        if(UIConfig.MENU_ITEM_LANGUAGE_ENABLED && hasManyLocale) enabledItems++;
        if(UIConfig.MENU_ITEM_SOUND_ENABLED) enabledItems++;

        return enabledItems;
    }



    public static boolean isHallowenDay(Date date){
        return date.getMonth() == 9 && date.getDate() == 31;
    }



    public static boolean isThanksGivingDay(WordConnectGame wordConnectGame, Date date){
        Date thanksGiving = wordConnectGame.dateUtil.newDate(date.getYear(), 10, 1);
        int dayOfWeek = thanksGiving.getDay() - 1;//java.util.Calendar is 1-based
        int thanksGivingDate = 22 + (11 - dayOfWeek) % 7;
        return date.getMonth() == 10 && date.getDate() == thanksGivingDate;
    }




    public static boolean isChristmasHoliday(WordConnectGame wordConnectGame, Date date){
        Date start = null;
        Date end = null;

        if(date.getMonth() == 11){
            start = wordConnectGame.dateUtil.newDate(date.getYear(), 11, 24);
            end   = wordConnectGame.dateUtil.newDate(date.getYear() + 1, 0, 6);
        }else if(date.getMonth() == 0){
            start = wordConnectGame.dateUtil.newDate(date.getYear() - 1, 11, 24);
            end   = wordConnectGame.dateUtil.newDate(date.getYear(), 0, 5);
        }

        if(start == null || end == null)return false;

        return date.after(start) && date.before(end);
    }

}
