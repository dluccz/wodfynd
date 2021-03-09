package word.game;


import word.game.net.WordMeaningProvider;
import word.game.net.WordMeaningRequest;

public class WordMeaningProviderAndroid implements WordMeaningProvider {


    public WordMeaningRequest get(String langCode){
        if(langCode.equals("en")) return new WordMeaningRequest_en();

        return null;
    }

}
