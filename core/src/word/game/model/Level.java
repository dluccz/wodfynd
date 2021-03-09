package word.game.model;

import com.badlogic.gdx.utils.Array;

public class Level {


    public int index = 0;
    private char[] letters;
    private BoardModel boardModel;
    public int comboCount;
    private int wordCount = 0;
    public boolean isSolved = false;


    public char[] getLetters(){
        return letters;
    }


    public void setLetters(char[] letters){
        this.letters = letters;
    }

    public BoardModel getBoardModel() {
        return boardModel;
    }

    public void setBoardModel(BoardModel boardModel) {
        this.boardModel = boardModel;
    }




    public String[] getWordsAsString(){

        Array<Word> acrossWords = boardModel.getAcrossWords();
        Array<Word> downWords = boardModel.getDownWords();

        String[] words = new String[acrossWords.size + downWords.size];

        int index = 0;

        for(int i = 0; i < acrossWords.size; i++){
            words[index] = acrossWords.get(i).answer;
            index++;
        }


        for(int i = 0; i < downWords.size; i++){
            words[index] = downWords.get(i).answer;
            index++;
        }

        return words;
    }




    public int getWordCount(){
        if(wordCount == 0) wordCount = getBoardModel().getAllWords(true).size;
        return wordCount;
    }



    public void reset(){
        comboCount = 0;
        wordCount = 0;
        isSolved = false;
    }

}
