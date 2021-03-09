package word.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.pool.Pools;
import word.game.util.Text;


import static word.game.model.Constants.KEY_ROCKET_WORDS;
import static word.game.model.Constants.KEY_SAVED_SOLVED_WORDS;
import static word.game.model.Constants.KEY_TILE_STATE;

public class GameData {


    public static ResourceManager resourceManager;

    public static HashMap<Integer, String> wordMap = new HashMap<>();
    public static HashSet<String> vulgarWords;
    private static Set<Integer> integerSet = new HashSet<>();

    private static Array<String> extraWords = new Array<>();
    private static JsonReader jsonReader = new JsonReader();

    private static BoardModel boardModel;
    private static Array<Word> acrossWords = new Array<>();
    private static Array<Word> downWords = new Array<>();
    private static Level level;


    public static int getTutorialStep(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(Constants.KEY_TUTORIAL_STEP, 0);
    }

    public static void saveTutorialStep(int step){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(Constants.KEY_TUTORIAL_STEP, step);
        preferences.flush();
    }



    public static int findFirstIncompleteLevel() {
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(getLocaleAwareKey(Constants.KEY_LAST_INCOMPLETE_LEVEL), 0);
    }



    public static Level getLevelByIndex(int index) {
        String fileName = "data/" + LanguageManager.locale.code + "/levels/" + index;
        Level level = readLevelFile(fileName);
        level.index = index;
        return level;
    }







    private static Level readLevelFile(String file){

        JsonValue doc = jsonReader.parse(Gdx.files.internal(file));

        if(boardModel == null) boardModel = new BoardModel();
        else boardModel.reset();

        String o = doc.getString("o");
        String[] split = o.split(",");

        boardModel.width = Integer.parseInt(split[0]);
        boardModel.height = Integer.parseInt(split[1]);

        Set<Integer> solvedWords = getSolvedWords();
        Set<Integer> rocketWords = getWordsWithRocket();

        if(acrossWords.size > 0) acrossWords.clear();
        if(downWords.size > 0) downWords.clear();



        if(GameConfig.DEBUG_LEVEL_ANSWERS) Gdx.app.log("game.log", "--- LEVEL " + file + " (" + boardModel.height + "x" + boardModel.width + ")");

        jsonToWords(acrossWords, doc.get("a"), Direction.ACROSS, solvedWords, rocketWords);
        jsonToWords(downWords, doc.get("d"), Direction.DOWN, solvedWords, rocketWords);

        boardModel.setAcrossWords(acrossWords);
        boardModel.setDownWords(downWords);

        if(level == null){
            level = new Level();
            level.setBoardModel(boardModel);
        }else{
            level.reset();
        }


        String letters = split[2];
        char[] chars = letters.toCharArray();
        shuffleArray(chars);
        level.setLetters(chars);

        return level;
    }




    public static Set<Integer> getSolvedWords(){

        JsonValue doc = readJsonArrayFromPreferences(getLocaleAwareKey(KEY_SAVED_SOLVED_WORDS));


        Set<Integer> set = new HashSet<>();

        for(int i = 0; i < doc.size; i++)
            set.add(doc.get(i).asInt());

        return set;
    }


    public static void saveSolvedWord(int id){
        String key = getLocaleAwareKey(KEY_SAVED_SOLVED_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);

        doc.addChild(new JsonValue(id));

        saveJsonDocument(doc, key);

    }




    public static void saveWordWithRocket(int id){
        String key = getLocaleAwareKey(KEY_ROCKET_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);
        doc.addChild(new JsonValue(id));
        saveJsonDocument(doc, key);
    }



    public static Set<Integer> getWordsWithRocket(){
        String key = getLocaleAwareKey(KEY_ROCKET_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);


        integerSet.clear();

        for(int i = 0; i < doc.size; i++)
            integerSet.add(doc.get(i).asInt());

        return integerSet;
    }




    public static void deleteWordWithRocket(int id){
        Set<Integer> set = getWordsWithRocket();
        set.remove(id);

        clearWordsWithRocket();

        for(Integer i : set){
            saveWordWithRocket(i);
        }

    }





    public static void clearWordsWithRocket(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(getLocaleAwareKey(KEY_ROCKET_WORDS),"[]");
        preferences.flush();
    }





    private static JsonValue readJsonArrayFromPreferences(String key){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String json = preferences.getString(key, "[]");
        return jsonReader.parse(json);
    }



    private static void jsonToWords(Array<Word> words, JsonValue json, Direction direction, Set<Integer> solvedWords, Set<Integer> rocketWords){

        for (int i = 0; i < json.size; i++){
            Word word = Pools.wordPool.obtain();

            JsonValue value = json.get(i);
            String s = value.asString();
            String[] split = s.split(",");

            word.id = Integer.parseInt(split[0]);

            word.answer = GameData.wordMap.get(word.id);

            if(GameConfig.DEBUG_LEVEL_ANSWERS){
                Gdx.app.log("game.log", word.toString());
            }

            word.x = Integer.parseInt(split[1]);
            word.y = Integer.parseInt(split[2]);
            word.direction = direction;
            word.isSolved = solvedWords.contains(word.id);
            word.hasRocket = rocketWords.contains(word.id);
            words.add(word);
        }
    }




    private static void shuffleArray(char[] array){
        int index;

        for (int i = array.length - 1; i > 0; i--){
            index = MathUtils.random(i);
            if (index != i){
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }





    public static String getLocaleAwareKey(String key){
        if(LanguageManager.locale == null) {
            for (String k : GameConfig.availableLanguages.keySet()) {
                return key + "_" + k;
            }
        }

        return key + "_" + LanguageManager.locale.code;
    }





    public static void readWords() {
        String fileName = "data/" + LanguageManager.locale.code + "/words.txt";

        Text text = resourceManager.get(fileName, Text.class);
        try {
            String str = new String(text.getString().getBytes(), "UTF-8");
            String[] split = str.split(":");

            wordMap.clear();
            for(int i = 0; i < split.length; i += 2){
                wordMap.put(Integer.parseInt(split[i]), split[i + 1]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }



    public static void readVulgarWords() {

        String fileName = "data/" + LanguageManager.locale.code + "/vulgar.txt";
        Text text = null;

        try {
            text = resourceManager.get(fileName, Text.class);
        }catch (GdxRuntimeException e){
            text = new Text(fileName);
        }

        if(text == null) throw new GdxRuntimeException("Could not read: "+fileName);

        try {
            String str = new String(text.getString().getBytes(), "UTF-8");
            String[] split = str.split("\\,");
            vulgarWords = new HashSet<>(Arrays.asList(split));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }



    public static void saveTileState(int x, int y, int type){
        Map<Integer, Integer> map = readTileStates();

        int key = (x << 8) | y;

        map.put(key, type);
        saveJsonDocument(mapToJsonValue(map), getLocaleAwareKey(KEY_TILE_STATE));
    }




    public static void removeTileState(int x, int y){
        Map<Integer, Integer> map = readTileStates();
        int key = (x << 8) | y;

        map.remove(key);
        saveJsonDocument(mapToJsonValue(map), getLocaleAwareKey(KEY_TILE_STATE));
    }



    public static Map<Integer, Integer> readTileStates(){
        String name = getLocaleAwareKey(KEY_TILE_STATE);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        String json = preferences.getString(name, "{}");
        JsonValue doc = jsonReader.parse(json);
        return jsonToMap(doc);
    }



    private static Map<Integer, Integer> jsonToMap(JsonValue doc){

        Map<Integer, Integer> map = new HashMap<>();

        for(int i = 0; i < doc.size; i++){
            JsonValue jv = doc.get(i);
            map.put(Integer.parseInt(jv.name), jv.asInt());
        }

        return map;
    }



    private static JsonValue mapToJsonValue(Map<Integer, Integer> map){
        JsonValue doc = new JsonValue(JsonValue.ValueType.object);

        for(Integer key : map.keySet()){
            doc.addChild(Integer.toString(key), new JsonValue(map.get(key)));
        }
        return doc;
    }






    public static void clearTileStates() {
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(getLocaleAwareKey(KEY_TILE_STATE), "{}");
        preferences.flush();
    }





    private static void saveJsonDocument(JsonValue doc, String key){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(key, doc.toJson(JsonWriter.OutputType.json));
        preferences.flush();
    }




    
    public static void clearSavedSolvedWordsJson() {
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(getLocaleAwareKey(KEY_SAVED_SOLVED_WORDS), "[]");
        preferences.flush();
    }




    public static void updateFirstIncompleteLevelIndex(int index) {
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_LAST_INCOMPLETE_LEVEL), index);
        preferences.flush();
    }





    public static int insertWordToExtraJson(String word){

        int a = 0;
        int b = 0;

        int wordId = isExtraWord(word);

        if(wordId > 0){
            a = 1;
            boolean exists = doesWordExistInExtraJson(wordId);

            if(!exists){
                b = 1;
                addWordToExtraJson(wordId);
            }else{
                b = 0;
            }


        }

        return (a << 8) | b;
    }



    public static Array<String> getExtraWords(){
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);

        extraWords.clear();

        for(int i = 0; i < doc.size; i++){
            extraWords.add(wordMap.get(doc.get(i).asInt()));
        }

        return extraWords;
    }




    private static void addWordToExtraJson(int wordId) {
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);
        doc.addChild(new JsonValue(wordId));
        saveJsonDocument(doc, key);
    }





    private static boolean doesWordExistInExtraJson(int wordId) {
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORDS);
        JsonValue doc = readJsonArrayFromPreferences(key);

        for(int i = 0; i < doc.size; i++){
            if(wordId == doc.get(i).asInt())
                return true;
        }

        return false;
    }



    public static boolean isVulgarWord(String word){
        return vulgarWords.contains(word);
    }



    public static int isExtraWord(String word) {
        for(Map.Entry<Integer, String> entry : wordMap.entrySet()){
            if(word.equals(entry.getValue()))
                return entry.getKey();
        }
        return 0;
    }



    public static void clearExtraWords(){
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORDS);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putString(key, "[]");
        preferences.flush();
    }




    public static void incrementFoundBonusWordCount(){
        int count = getExtraWordsCount();
        count++;

        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(key, count);
        preferences.flush();
    }


    public static void saveExtraWordsCount(int n){
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(key, n);
        preferences.flush();
    }


    public static int getExtraWordsCount(){
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(key, 0);
    }




    public static void resetExtraWordCount(){
        String key = getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT);
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(key, 0);
        preferences.flush();
    }




    public static boolean hasUfoBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getBoolean(getLocaleAwareKey(Constants.KEY_UFO_CONSUMED), false);
    }



    public static void setUfoHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(getLocaleAwareKey(Constants.KEY_UFO_CONSUMED), true);
        preferences.flush();
    }



    public static void removeUfoHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_UFO_CONSUMED));
        preferences.flush();
    }




    public static boolean hasBombBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getBoolean(getLocaleAwareKey(Constants.KEY_BOMB_CONSUMED), false);
    }



    public static void setBombHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(getLocaleAwareKey(Constants.KEY_BOMB_CONSUMED), true);
        preferences.flush();
    }



    public static void removeBombHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_BOMB_CONSUMED));
        preferences.flush();
    }



    public static void setNumberOfBombMoves(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_NUMBER_OF_BOMB_MOVES), Math.max(0, count));
        preferences.flush();
    }




    public static int getNumberOfBombMoves(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(getLocaleAwareKey(Constants.KEY_NUMBER_OF_BOMB_MOVES), 0);
    }




    public static void removeNumberOfBombMoves(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_NUMBER_OF_BOMB_MOVES));
        preferences.flush();
    }





    public static boolean hasGoldPackBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getBoolean(getLocaleAwareKey(Constants.KEY_GOLD_PACK_CONSUMED), false);
    }



    public static void setGoldPackHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(getLocaleAwareKey(Constants.KEY_GOLD_PACK_CONSUMED), true);
        preferences.flush();
    }



    public static void removeGoldPackHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_GOLD_PACK_CONSUMED));
        preferences.flush();
    }



    public static void setNumberOfGoldPackMoves(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_NUMBER_OF_GOLD_PACK_MOVES), count);
        preferences.flush();
    }



    public static int getNumberOfGoldPackMoves(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        return preferences.getInteger(getLocaleAwareKey(Constants.KEY_NUMBER_OF_GOLD_PACK_MOVES), 0);
    }



    public static void removeNumberOfGoldPackMoves(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_NUMBER_OF_GOLD_PACK_MOVES));
        preferences.flush();
    }






    public static void setMonsterHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(getLocaleAwareKey(Constants.KEY_MONSTER_CONSUMED), true);
        preferences.flush();
    }



    public static boolean hasMonsterBeenConsumedInThisLevel(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(getLocaleAwareKey(Constants.KEY_MONSTER_CONSUMED), false);
    }


    public static void removeMonsterHasBeenConsumedInThisLevel(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.remove(getLocaleAwareKey(Constants.KEY_MONSTER_CONSUMED));
        preferences.flush();
    }



    public static void saveLastBoosterType(int boosterType){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_BOOSTER_TYPE), boosterType);
        preferences.flush();
    }




    public static int getLastBoosterType(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(getLocaleAwareKey(Constants.KEY_BOOSTER_TYPE), -1);
    }





    public static void saveComboCount(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_COMBO_COUNT), count);
        preferences.flush();
    }



    public static int getComboCount(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(getLocaleAwareKey(Constants.KEY_COMBO_COUNT), 0);
    }




    public static void saveComboReward(int count){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putInteger(getLocaleAwareKey(Constants.KEY_COMBO_REWARD), count);
        preferences.flush();
    }



    public static int getComboReward(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(getLocaleAwareKey(Constants.KEY_COMBO_REWARD), 0);
    }



    public static boolean isUfoTutorialDisplayed(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_UFO_TUTORIAL_SHOWN, false);
    }


    public static void setUfoTutorialComplete(){
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putBoolean(Constants.KEY_UFO_TUTORIAL_SHOWN, true);
        preferences.flush();
    }




    public static boolean isBombTutorialDisplayed(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_BOMB_TUTORIAL_SHOWN, false);
    }


    public static void setBombTutorialComplete(){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_BOMB_TUTORIAL_SHOWN, true).flush();
    }


    public static boolean isGoldPackTutorialDisplayed(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_GOLD_PACK_TUTORIAL_SHOWN, false);
    }

    public static void setGoldPackTutorialComplete(){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_GOLD_PACK_TUTORIAL_SHOWN, true).flush();
    }

    public static boolean isMonsterTutorialDisplayed(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_MONSTER_TUTORIAL_SHOWN, false);
    }

    public static void setMonsterTutorialComplete(){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_MONSTER_TUTORIAL_SHOWN, true).flush();
    }

    public static boolean isExtraWordsTutorialDisplayed1(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_BONUS_WORDS_TUTORIAL_SHOWN1, false);
    }

    public static void setExtraWordsTutorialComplete1(){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_BONUS_WORDS_TUTORIAL_SHOWN1, true).flush();
    }


    public static boolean isExtraWordsTutorialDisplayed2(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_BONUS_WORDS_TUTORIAL_SHOWN2, false);
    }

    public static void setExtraWordsTutorialComplete2(){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_BONUS_WORDS_TUTORIAL_SHOWN2, true).flush();
    }






    public static boolean isGameMuted(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getBoolean(Constants.KEY_MUTED, false);
    }


    public static void setGameMute(boolean muted){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putBoolean(Constants.KEY_MUTED, muted).flush();
        ConfigProcessor.muted = muted;
    }



    public static void saveLastRewardedAdTime(long time){
        Gdx.app.getPreferences(Constants.PREFS_NAME).putLong(Constants.KEY_LAST_REWARDED_AD_TIME, time).flush();
    }


    public static long getLastRewardedAdTime(){
        return Gdx.app.getPreferences(Constants.PREFS_NAME).getLong(Constants.KEY_LAST_REWARDED_AD_TIME, 0);
    }
}
