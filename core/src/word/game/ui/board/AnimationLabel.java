package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;

import word.game.actions.BezierToAction;
import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.controllers.GameController;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;
import word.game.pool.Pools;
import word.game.util.MathUtil;

import static word.game.ui.board.CellView.solvedTileSizeCoef;

public class AnimationLabel extends Group implements Pool.Poolable {



    public static final float LETTER_ANIM_SPEED = 0.25f;


    private  Label.LabelStyle labelStyle = new Label.LabelStyle(CellView.labelStyleSolved);

    private BezierToAction bezierToAction;
    private DelayAction delayAction;
    private MoveToAction moveToAction;
    private RunnableAction runnableAction;
    private SequenceAction sequenceAction;

    public CellView cellView;
    private GameController gameController;
    private Runnable callback;
    private boolean lastIteration;
    public Label label;
    private Image bg;

    public AnimationLabel(){
        bg = new Image(NinePatches.board_cell_solved);
        addActor(bg);

        label = new Label("", labelStyle);
        addActor(label);
    }



    public void setText(CharSequence newText, Color color, float sourceHeight, ResourceManager resourceManager) {
        label.setText(newText);
        bg.setColor(color);
        float h = label.getPrefHeight();

        if(cellView != null) {
            GlyphLayout layout = com.badlogic.gdx.utils.Pools.obtain(GlyphLayout.class);
            layout.setText(resourceManager.get(ResourceManager.fontBoardAndDialFont, BitmapFont.class), newText);
            float fontScale = cellView.getHeight() * solvedTileSizeCoef * UIConfig.TILE_LETTER_FONT_SCALE / layout.height;
            label.setFontScale(fontScale);
        }

        float size;

        if(cellView != null) size = cellView.getWidth() * solvedTileSizeCoef;
        else size = label.getPrefHeight();

        setSize(size, size);
        bg.setSize(getWidth(), getHeight());
        bg.setOrigin(Align.center);

        label.setX((getWidth() - label.getPrefWidth()) * 0.5f);
        label.setY(label.getPrefHeight() * 0.5f);

        float scale = sourceHeight / h;
        setScale(scale);
    }




    public void animateCorrectAnswer(GameController gameController, float delay, float margin, Runnable callback){
        this.gameController = gameController;
        this.callback = callback;

        if(delayAction == null) delayAction = new DelayAction();
        else delayAction.reset();
        delayAction.setDuration(delay);

        final float startScale = getScaleX();
        final float endScale = (cellView.getHeight() * solvedTileSizeCoef) / getHeight();

        if(moveToAction == null){
            moveToAction = new MoveToAction(){
                @Override
                protected void update(float percent) {
                    super.update(percent);

                    float scale = MathUtil.scaleNumber(percent, 0, 1, startScale, endScale);
                    setScale(scale);
                }
            };
        } else {
            moveToAction.reset();
        }
        Vector2 targetVec2 = cellView.getStageCoords();

        targetVec2.x += margin;
        targetVec2.y += margin;
        moveToAction.setPosition(targetVec2.x, targetVec2.y);
        moveToAction.setDuration(LETTER_ANIM_SPEED);
        moveToAction.setInterpolation(word.game.actions.Interpolation.cubicInOut);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();
        runnableAction.setRunnable(correctAnimEnd);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(moveToAction);
        sequenceAction.addAction(runnableAction);
        addAction(sequenceAction);
    }





    private Runnable correctAnimEnd = new Runnable() {
        @Override
        public void run() {
            remove();
            cellView.starBurst();
            cellView.updateStateView();
            gameController.animateBoostersAfterLetterAnimation(cellView);
            cellView.growAndShrink(callback);
            Pools.animationLetterPool.free(AnimationLabel.this);
        }
    };






    public void animateExtraWord(GameController gameController, float delay, Vector2 sourceVec2, float targetX, float targetY, final float scaleTo, boolean last){
        this.gameController = gameController;
        lastIteration = last;

        float size = AnimationLabel.this.gameController.gameScreen.extraWordsButton.getWidth() * 0.5f;
        setSize(size, size);

        if(delayAction == null) delayAction = new DelayAction();
        else delayAction.reset();
        delayAction.setDuration(delay);

        if(bezierToAction == null) {
            bezierToAction = new BezierToAction(){
                @Override
                protected void update(float percent) {
                    super.update(percent);
                    setScale(MathUtil.scaleNumber(percent, 0, 1, 1, scaleTo));
                }
            };
        }else {
            bezierToAction.reset();
        }

        bezierToAction.setStartPosition(sourceVec2.x, sourceVec2.y);
        bezierToAction.setPointA(targetX + (sourceVec2.x - targetX) * 0.5f, sourceVec2.y * 1.5f);
        bezierToAction.setPointB(targetX, targetY * 1.5f);
        bezierToAction.setEndPosition(targetX, targetY);

        bezierToAction.setDuration(GameConfig.BONUS_WORD_LETTER_ANIM_SPEED);
        bezierToAction.setInterpolation(word.game.actions.Interpolation.cubicInOut);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();
        runnableAction.setRunnable(extraAnimEnd);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(bezierToAction);
        sequenceAction.addAction(runnableAction);
        addAction(sequenceAction);
    }




    private Runnable extraAnimEnd = new Runnable() {
        @Override
        public void run() {
            remove();
            Pools.animationLetterPool.free(AnimationLabel.this);
            if(lastIteration) gameController.gameScreen.extraWordsButton.growAndShrink(gameController.extraWordsGrowAndShrinkFinished());
        }
    };



    @Override
    public void reset() {
        cellView = null;
        setScale(1f);
    }
}
