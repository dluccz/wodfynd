package word.game.desktop;


import word.game.net.WordMeaningProvider;
import word.game.net.WordMeaningRequest;

public class WordMeaningProviderDesktop implements WordMeaningProvider {


    public WordMeaningRequest get(String langCode){
        if(langCode.equals("en"))
            return new WordMeaningRequest_en();

        return null;

    }

}
