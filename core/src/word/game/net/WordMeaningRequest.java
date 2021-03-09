package word.game.net;


import word.game.ui.dialogs.DictionaryDialog;

public interface WordMeaningRequest {
    void request(String word, DictionaryDialog.DictionaryCallback callback);
}
