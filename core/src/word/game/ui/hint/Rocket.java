package word.game.ui.hint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import word.game.actions.AngledBezierToAction;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.model.Constants;
import word.game.model.Direction;
import word.game.model.GameData;
import word.game.screens.GameScreen;
import word.game.ui.board.CellView;
import word.game.ui.dial.Particle;


public class Rocket extends Actor {

    public float defaultX, defaultY;

    private TextureRegion sparkleRegion;
    private Array<Particle> tail = new Array<>();


    private Array<CellView> cellViewsToAnimate;
    private GameScreen gameScreen;

    private Direction direction;
    private TextureRegion rocketRegion;
    private Array<CellView> cellsToComplete;


    public Rocket(GameScreen gameScreen){
        this.gameScreen = gameScreen;

        this.rocketRegion = AtlasRegions.rocket;

        setSize(rocketRegion.getRegionWidth(), rocketRegion.getRegionHeight());
        setOrigin(Align.center);
        setTouchable(Touchable.disabled);
        sparkleRegion = AtlasRegions.sparkle;
    }






    public void start(Array<CellView> cellViewsToAnimate, Direction direction){
        this.cellViewsToAnimate = cellViewsToAnimate;
        this.direction = direction;

        CellView firstTile = cellViewsToAnimate.get(0);
        Vector2 firstPos = firstTile.getStageCoords();

        AngledBezierToAction bezier = new AngledBezierToAction();
        bezier.setDuration(2);
        bezier.setInterpolation(Interpolation.sineIn);

        bezier.setStartPosition(getX(), getY());

        if(direction == Direction.ACROSS){
            float x = -firstTile.getWidth();
            float y = firstPos.y + (firstTile.getHeight() - getHeight()) * 0.5f;
            bezier.setPointA(x, y);
            bezier.setPointB(x, y);
            bezier.setEndPosition(getStage().getWidth() + getWidth(), y);
        }else{
            float x = firstPos.x + (firstTile.getWidth() - getWidth()) * 0.5f;
            bezier.setPointA(x, getY());
            bezier.setPointB(x, getY());
            bezier.setEndPosition(x, getStage().getHeight() + getHeight());
        }

        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(reset);
        addAction(new SequenceAction(bezier, runnableAction));
    }







    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(rocketRegion,
                getX(), getY(),
                getOriginX(), getOriginY(),
                rocketRegion.getRegionWidth(), rocketRegion.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation()
        );


        if(cellViewsToAnimate != null && cellViewsToAnimate.size > 0){
            for(int i = 0; i < cellViewsToAnimate.size; i++){
                CellView cellView = cellViewsToAnimate.get(i);
                Vector2 pos = cellView.getStageCoords();
                float left = pos.x;
                float bottom = pos.y;

                if((direction == Direction.ACROSS && getX() > left && getY() > bottom) || (direction == Direction.DOWN && getY() > bottom )){
                    int stage = cellView.cellData.getState();
                    if(stage == Constants.TILE_STATE_REVEALED || stage == Constants.TILE_STATE_SOLVED ||  stage == Constants.TILE_STATE_COINED){
                        continue;
                    }

                    reveal(i);
                    cellView.starBurst();
                }
            }


            if (tail.size < 30) {
                Particle sparkle = new Particle(sparkleRegion);

                tail.add(sparkle);
            }

            for (int i = 0; i < tail.size; i++) {
                Particle sparkle = tail.get(i);

                float x = getParticleX(sparkle);
                float y = getParticleY(sparkle);

                if (sparkle.getScaleX() == 1.0f)
                    sparkle.setPosition(x, y);

                Color c = UIConfig.ROCKET_FIRE_COLOR;
                c.a = color.a;
                sparkle.setColor(c);

                sparkle.draw(batch);
                sparkle.setScale(sparkle.getScaleX() - 0.05f);

                if (sparkle.getScaleX() <= 0) {
                    sparkle.setScale(1);
                }
            }
        }

        batch.setColor(color.r, color.g, color.b, 1f);
    }




    private float getParticleX(Particle sparkle){
        float centerX = (getWidth() - sparkle.getWidth()) * 0.5f;
        return getX() - MathUtils.cos((getRotation()) * MathUtils.degreesToRadians) * sparkle.getWidth() + centerX;
    }



    private float getParticleY(Particle sparkle){
        float centerY = (getHeight() - sparkle.getHeight()) * 0.5f;
        return getY() - MathUtils.sin((getRotation()) * MathUtils.degreesToRadians) * sparkle.getHeight() + centerY;
    }






    private void reveal(int cellViewIndex){

        if(cellViewIndex == 0) {
            CellView cellView = cellViewsToAnimate.get(0);
            if(cellView.cellData.getState() != Constants.TILE_STATE_DEFAULT) return;

            cellView.cellData.setState(Constants.TILE_STATE_REVEALED);
            cellView.updateStateView();
            GameData.saveTileState(cellView.cellData.getX(), cellView.cellData.getY(), Constants.TILE_STATE_REVEALED);
        }else if(cellViewIndex == cellViewsToAnimate.size - 1){
            CellView cellView = cellViewsToAnimate.get(cellViewsToAnimate.size - 1);
            if(cellView.cellData.getState() != Constants.TILE_STATE_DEFAULT) return;

            cellView.cellData.setState(Constants.TILE_STATE_REVEALED);
            cellView.updateStateView();
            GameData.saveTileState(cellView.cellData.getX(), cellView.cellData.getY(), Constants.TILE_STATE_REVEALED);

        }else {
            CellView cellView = cellViewsToAnimate.get(cellViewIndex);
            if(cellView.cellData.getState() == Constants.TILE_STATE_DEFAULT) {
                cellView.cellData.setState(Constants.TILE_STATE_COINED);
                cellView.updateStateView();
                GameData.saveTileState(cellView.cellData.getX(), cellView.cellData.getY(), Constants.TILE_STATE_COINED);
            }
        }
    }



    private Runnable reset = new Runnable() {

        @Override
        public void run() {
            setRotation(0);
            setPosition(0, defaultY);

            Action moveTo = Actions.moveTo(defaultX, defaultY, 1, Interpolation.fastSlow);
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    triggerCompletetingIncompleteTiles();
                    cellViewsToAnimate.clear();

                    for(Particle sparkle : tail){
                        sparkle.setX(getParticleX(sparkle));
                        sparkle.setY(getParticleY(sparkle));
                    }
                    getStage().getRoot().setTouchable(Touchable.enabled);
                    gameScreen.resumeIdleTimer();
                }
            });

            SequenceAction sequenceAction = new SequenceAction(moveTo, runnableAction);
            addAction(sequenceAction);
        }
    };






    private void triggerCompletetingIncompleteTiles(){
        if(cellsToComplete == null) cellsToComplete = new Array<>();
        else cellsToComplete.clear();

        for(CellView cellView : cellViewsToAnimate){
            if(cellView.cellData.getState() == Constants.TILE_STATE_REVEALED) {
                cellsToComplete.add(cellView);
            }
        }
        gameScreen.gameController.findAndCompleteInCompleteCellViewsAfterGivingHint(cellsToComplete);
    }

}
