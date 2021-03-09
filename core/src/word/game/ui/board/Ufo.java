package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import word.game.actions.AngledBezierToAction;
import word.game.actions.BezierToAction;
import word.game.config.GameConfig;
import word.game.graphics.AtlasRegions;
import word.game.model.Constants;
import word.game.model.GameData;
import word.game.screens.GameScreen;
import word.game.ui.board.CellView;

import static word.game.actions.Interpolation.cubicIn;
import static word.game.actions.Interpolation.cubicOut;


public class Ufo extends Group {


    private Image thrust;
    private float time;
    private boolean enableChrono;
    private UfoCallback ufoCallback;
    private GameScreen gameScreen;
    public CellView targetCell;
    private RunnableAction runnableAction;



    public Ufo(GameScreen gameScreen){

        this.gameScreen = gameScreen;

        Image ufo = new Image(AtlasRegions.ufo);
        addActor(ufo);

        setSize(ufo.getWidth(), ufo.getHeight());
        setOrigin(Align.center);
        thrust = new Image(AtlasRegions.ufo_thrust);
        thrust.setX((getWidth() - thrust.getWidth()) * 0.5f);
        thrust.setY(-thrust.getHeight());
        thrust.setOriginX(thrust.getWidth() * 0.5f);
        thrust.setOriginY(thrust.getHeight());
        addActor(thrust);
    }



    public void flyIn(CellView targetCell, UfoCallback ufoCallback){
        this.targetCell = targetCell;
        this.ufoCallback = ufoCallback;
        flyInStep1();
    }




    private void flyInStep1(){
        float startX = 0;
        float startY = gameScreen.stage.getHeight();

        setX(startX);
        setY(startY);
        setScale(targetCell.getWidth() / getWidth(), targetCell.getWidth() / getWidth());

        gameScreen.stage.addActor(this);

        final Vector2 pos = targetCell.getStageCoords();
        pos.x += (targetCell.getWidth() - getWidth()) * 0.5f ;
        pos.y += (targetCell.getHeight() - getHeight()) * 0.5f;

        BezierToAction bezier = new BezierToAction();
        bezier.setStartPosition(startX, startY);
        bezier.setPointA(startX, pos.y + (startY - pos.y) * 0.5f);
        bezier.setPointB(startX + (pos.x  - startX) * 0.5f, pos.y);
        bezier.setEndPosition(pos.x, pos.y);
        bezier.setDuration(1.5f);
        bezier.setInterpolation(Interpolation.sineOut);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();
        runnableAction.setRunnable(flyInFinished);

        addAction(Actions.sequence(bezier, runnableAction));
    }



    private Runnable flyInFinished = new Runnable() {

        @Override
        public void run() {
            ufoCallback.flyInFinished();
            thrust.addAction(Actions.fadeOut(0.3f));
        }
    };




    private void flyOutStep1(){
        thrust.addAction(Actions.fadeIn(0.2f));
        Action moveByBack = Actions.moveBy(-getWidth() * 0.3f, getHeight() * 0.2f, 0.2f, cubicOut);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(flyOutStep2);
        addAction(new SequenceAction(moveByBack, runnableAction));
        addAction(Actions.rotateBy(-10, 0.2f, cubicOut));
    }




    private Runnable flyOutStep2 = new Runnable() {
        @Override
        public void run() {
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(flyOutStep3);
            addAction(new SequenceAction(Actions.rotateBy(10, 0.05f), runnableAction));
        }
    };




    private Runnable flyOutStep3 = new Runnable() {
        @Override
        public void run() {

            float right = gameScreen.stage.getWidth();
            float top = gameScreen.stage.getHeight() - getHeight();

            float a = right - getX();
            float b = top - getY();

            float dst = (float)Math.sqrt(a * a + b * b);
            float time = dst * 0.0007f;

            AngledBezierToAction bezier = new AngledBezierToAction();
            bezier.setStartPosition(getX(), getY());
            bezier.setPointA(getX() + (right - getX()) * 0.5f, getY());
            bezier.setPointB(right, getY() + (top - getY()) * 0.5f);
            bezier.setEndPosition(gameScreen.stage.getWidth() * 1.3f, top);
            bezier.setDuration(time);
            bezier.setInterpolation(cubicIn);

            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(ufoRemover);

            addAction(new SequenceAction(bezier, runnableAction));
            addAction(Actions.scaleTo(1, 1, time, cubicIn));
        }
    };



    private Runnable ufoRemover = new Runnable() {
        @Override
        public void run() {
            ufoCallback.deleteUfo();
        }
    };



    public void startChrono(){enableChrono = true;}
    public void stopChrono(){
        enableChrono = false;
    }




    public void hide(){
        Action scale = Actions.scaleTo(0, 0, 0.5f, Interpolation.sineIn);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(ufoRemover);
        addAction(new SequenceAction(scale, runnableAction));
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(enableChrono){
            time += Gdx.graphics.getDeltaTime();

            if(time >= GameConfig.SECONDS_BEFORE_UFO_DISAPPEARS){
                targetCell.cellData.setState(Constants.TILE_STATE_DEFAULT);
                GameData.setUfoHasBeenConsumedInThisLevel();
                enableChrono = false;
                flyOutStep1();
                ufoCallback.timeIsUp(targetCell);
            }
        }
    }





    public interface UfoCallback{
        void timeIsUp(CellView cellView);
        void flyInFinished();
        void deleteUfo();
    }

}
