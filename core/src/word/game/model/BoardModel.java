package word.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import word.game.pool.Pools;
import word.game.ui.board.BoardView;
import word.game.ui.board.CellView;

public class BoardModel {

    public int width, height;

    private Array<Word> acrossWords;
    private Array<Word> downWords;

    private CellModel[][] solvedState;

    public Array<Word> getAcrossWords() {
        return acrossWords;
    }

    public void setAcrossWords(Array<Word> acrossWords) {
        this.acrossWords = acrossWords;
    }

    public Array<Word> getDownWords() {
        return downWords;
    }

    public void setDownWords(Array<Word> downWords) {
        this.downWords = downWords;
    }


    public CellModel[][] getSolvedState(){

        if(solvedState != null)
            return solvedState;

        solvedState = new CellModel[width][height];

        Map<Integer, Integer> tileMap = GameData.readTileStates();

        for(int x = 0; x < acrossWords.size; x++){
            Word word = acrossWords.get(x);

            for(int i = 0; i < word.answer.length(); i++){
                int mergedPosition = ((word.x + i) << 8) | word.y;
                int state = Constants.TILE_STATE_DEFAULT;
                if(tileMap.containsKey(mergedPosition)){
                    state = tileMap.get(mergedPosition);
                }

                CellModel cellModel = Pools.cellModelPool.obtain();
                cellModel.init(word.answer.charAt(i), word.x + i, word.y, null, word, state);

                solvedState[word.x + i][word.y] = cellModel;

            }
        }


        for(int y = 0; y < downWords.size; y++){
            Word word = downWords.get(y);

            for(int i = 0; i < word.answer.length(); i++){
                int mergedPosition = ((word.x) << 8) | (word.y - i);

                int state = Constants.TILE_STATE_DEFAULT;
                if(tileMap.containsKey(mergedPosition)){
                    state = tileMap.get(mergedPosition);
                }

                if(solvedState[word.x][word.y - i] != null) {
                    solvedState[word.x][word.y - i].setDownWord(word);
                }else {
                    CellModel cellModel = Pools.cellModelPool.obtain();
                    cellModel.init(word.answer.charAt(i), word.x, word.y - i, word, null, state);
                    solvedState[word.x][word.y - i] = cellModel;
                }
            }
        }


        return solvedState;
    }




    public Array<Word> getAllWords(boolean includeSolved){
        Array<Word> acrossWords = getAcrossWords();
        Array<Word> downWords = getDownWords();

        Array<Word> words = new Array<>();

        for(Word word : acrossWords){
            if(includeSolved) {
                words.add(word);
            }else {
                if (!word.isSolved) {
                    words.add(word);
                }
            }
        }

        for(Word word : downWords){
            if(includeSolved) {
                words.add(word);
            }else {
                if (!word.isSolved) {
                    words.add(word);
                }
            }
        }

        return words;

    }






    public Word getRandomWordForRocket(BoardView boardView){
        Array<Word> words = getAllWords(false);
        words.shuffle();

        for(Word word : words) {
            if (!word.hasRocket && isWordAvailableForRocket(word, boardView))
                return word;
        }

        return null;
    }



    private boolean isWordAvailableForRocket(Word word, BoardView boardView){
        Array<CellView> cellViewsToAnimate = boardView.findWordCellViews(word);



        for(CellView cellView : cellViewsToAnimate){
            if(     cellView.cellData.getState() == Constants.TILE_STATE_COINED ||
                    cellView.cellData.getState() == Constants.TILE_STATE_MONSTER ||
                    cellView.cellData.getState() == Constants.TILE_STATE_GOLD_PACKED ||
                    cellView.cellData.getState() == Constants.TILE_STATE_UFO ||
                    cellView.cellData.getState() == Constants.TILE_STATE_BOMBED)
                return false;
        }
        return true;
    }




    public void reset() {
        solvedState = null;
    }
}
