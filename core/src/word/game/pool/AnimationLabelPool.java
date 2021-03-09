package word.game.pool;

import com.badlogic.gdx.utils.Pool;

import word.game.ui.board.AnimationLabel;

public class AnimationLabelPool extends Pool<AnimationLabel> {


    public AnimationLabelPool(){
        super(6, 50);
    }



    @Override
    protected AnimationLabel newObject() {
        return new AnimationLabel();
    }


}
