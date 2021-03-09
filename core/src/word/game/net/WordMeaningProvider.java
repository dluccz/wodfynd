package word.game.net;

public interface WordMeaningProvider{
    WordMeaningRequest get(String langCode);
}
