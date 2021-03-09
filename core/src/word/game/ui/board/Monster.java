package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

import word.game.actions.BezierToAction;
import word.game.graphics.AtlasRegions;

public class Monster extends Actor {


    public enum State{
        IDLE,
        JUMP_UP,
        JUMP_FALL
    }

    private Animation<TextureAtlas.AtlasRegion> idle;
    private State state = State.IDLE;
    private float stateTime;
    public CellView targetCell;
    public boolean hit;
    private MoveToAction up, down;
    private RunnableAction jumpFallRunnanbleAction, idleRunnableAction;
    private SequenceAction sequenceAction;
    private BezierToAction bezier;


    public Monster(){
        idle = new Animation<>(0.7f, AtlasRegions.monsterIdleAnimation);
        idle.setPlayMode(Animation.PlayMode.LOOP);

        TextureAtlas.AtlasRegion first = AtlasRegions.monsterIdleAnimation.get(0);
        setSize(first.getRegionWidth(), first.getRegionHeight());
    }




    private void setState(State state){
        this.state = state;
    }



    private Runnable jumpFallRunnable = new Runnable() {
        @Override
        public void run() {
            setState(State.JUMP_FALL);
        }
    };



    private Runnable idleRunnable = new Runnable() {
        @Override
        public void run() {
            setState(State.IDLE);
        }
    };



    public void jump(CellView randomCell, float srcX, float srcY, float dstX, float dstY){

        if(idleRunnableAction == null) idleRunnableAction = new RunnableAction();
        else idleRunnableAction.reset();
        idleRunnableAction.setRunnable(idleRunnable);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        float a = dstX - srcX;
        float b = dstY - srcY;
        double dst = Math.sqrt(a * a + b * b);


        float duration = 0.7f;

        if(dst > getStage().getWidth() * 0.5f)
            duration *= 1.5f;

        if(srcX == dstX){
            setState(State.JUMP_UP);

            if(jumpFallRunnanbleAction == null) jumpFallRunnanbleAction = new RunnableAction();
            jumpFallRunnanbleAction.reset();
            jumpFallRunnanbleAction.setRunnable(jumpFallRunnable);

            if(up == null) up = new MoveToAction();
            else up.reset();

            if(down == null) down = new MoveToAction();
            else down.reset();

            if(srcY > dstY) {
                up.setPosition(dstX, srcY + randomCell.getHeight() * 0.5f);
                up.setDuration(duration * 0.3f);

                down.setPosition(dstX, dstY);
                down.setDuration(duration * 0.7f);
            }else{
                up.setPosition(dstX, dstY + randomCell.getHeight() * 0.5f);
                up.setDuration(duration * 0.7f);

                down.setPosition(dstX, dstY);
                down.setDuration(duration * 0.3f);
            }
            up.setInterpolation(Interpolation.sineOut);
            down.setInterpolation(Interpolation.sineIn);

            if(sequenceAction == null) sequenceAction = new SequenceAction();
            else sequenceAction.reset();

            sequenceAction.addAction(up);
            sequenceAction.addAction(jumpFallRunnanbleAction);
            sequenceAction.addAction(down);
            sequenceAction.addAction(idleRunnableAction);
            addAction(sequenceAction);

            return;
        }


        if(bezier == null) {
            bezier = new BezierToAction() {
                @Override
                protected void update(float percent) {
                    super.update(percent);
                    if (percent <= 0.5f) setState(State.JUMP_UP);
                    else if (percent > 0.5f) setState(State.JUMP_FALL);

                }
            };
        }else{
            bezier.reset();
        }



        bezier.setStartPosition(srcX, srcY);

        float cell = randomCell.getHeight();

        if(dstX < srcX && dstY < srcY) {
            bezier.setPointA(dstX, srcY + cell * 2);
            bezier.setPointB(dstX, srcY + cell);
        }

        if(dstX < srcX && dstY > srcY){
            bezier.setPointA(dstX, dstY + cell * 2);
            bezier.setPointB(dstX, dstY + cell);
        }

        if(dstX > srcX && dstY < srcY){
            bezier.setPointA(dstX, srcY + cell * 2);
            bezier.setPointB(dstX, srcY + cell);
        }

        if(dstX > srcX && dstY > srcY){
            bezier.setPointA(dstX, dstY + cell * 2);
            bezier.setPointB(dstX, dstY + cell);
        }


        if(srcY == dstY){
            float x = Math.min(srcX, dstX) + Math.abs(dstX - srcX) * 0.5f;
            float y = dstY + randomCell.getHeight();
            bezier.setPointA(x, y);
            bezier.setPointB(x, y);
        }


        bezier.setEndPosition(dstX, dstY);
        bezier.setDuration(duration);
        bezier.setInterpolation(Interpolation.smooth);

        sequenceAction.addAction(bezier);
        sequenceAction.addAction(idleRunnableAction);

        addAction(sequenceAction);
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1f,1f,1f,parentAlpha);
        super.draw(batch, parentAlpha);

        TextureAtlas.AtlasRegion currentFrame = null;

        if(state == State.IDLE){
            stateTime += Gdx.graphics.getDeltaTime();
            currentFrame = idle.getKeyFrame(stateTime);
        }else if(state == State.JUMP_UP){
            currentFrame = AtlasRegions.jump_up;
        }else if(state == State.JUMP_FALL){
            currentFrame = AtlasRegions.jump_fall;
        }


        if(currentFrame != null) {
            batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight(),getScaleX(),getScaleY(), getRotation());
            setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }
    }




}
