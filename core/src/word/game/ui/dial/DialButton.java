package word.game.ui.dial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import word.game.actions.CurveActionIn;
import word.game.actions.CurveActionOut;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.graphics.shader.LineShader;
import word.game.managers.ResourceManager;


public class DialButton extends Group implements Pool.Poolable {


    public static Label.LabelStyle labelStyleUp;
    public static Label.LabelStyle labelStyleDown;

    private Image background;

    public int id;
    public boolean isLineSnapped;
    public LineShader line;
    private char c;
    public Label label;
    private Image bgEffect;
    public float angle;
    private Dial dial;

    private ScaleToAction selectionScaleAction;
    private AlphaAction selectionAlphaAction;
    private ParallelAction selectionParallelAction;

    private Drawable bgDrawable;


    public DialButton(){

        if(bgDrawable == null) bgDrawable = new TextureRegionDrawable(AtlasRegions.dial_button);

        bgEffect = new Image(new TextureRegionDrawable(AtlasRegions.dial_button));
        bgEffect.setOrigin(Align.center);

        addActor(bgEffect);

        background = new Image(bgDrawable);

        setSize(background.getWidth(), background.getHeight());
        setOrigin(Align.center);
        addActor(background);

        label = createLabel(c, labelStyleUp);
        addActor(label);
    }



    private CurveActionIn curveActionIn;
    private CurveActionOut curveActionOut;
    private RotateToAction curveBackAction;
    private RotateByAction curveRotateBy;
    private RunnableAction shuffleEnd;
    private SequenceAction shuffleSequence;

    public void shuffle(float angle, Runnable callback){

        float speedIn = 0.25f;
        float speedOut = 0.25f;
        float speedBackIn = 0.075f;
        float speedBackOut = 0.17f;

        if(curveActionIn == null) curveActionIn = new CurveActionIn();
        else curveActionIn.reset();

        curveActionIn.init(this, dial);
        curveActionIn.setDuration(speedIn);
        curveActionIn.setInterpolation(Interpolation.sineOut);

        if(curveActionOut == null) curveActionOut = new CurveActionOut();
        else curveActionOut.reset();

        curveActionOut.init(this, dial);
        curveActionOut.setDuration(speedOut);
        curveActionOut.setInterpolation(Interpolation.sineIn);
        this.angle = angle;

        if(curveRotateBy == null) curveRotateBy = new RotateByAction();
        else curveRotateBy.reset();

        curveRotateBy.setAmount(-360);
        curveRotateBy.setDuration(speedBackIn);

        if(curveBackAction == null) curveBackAction = new RotateToAction();
        else curveBackAction.reset();

        curveBackAction.setRotation(-360f);
        curveBackAction.setDuration(speedBackOut);
        curveBackAction.setInterpolation(Interpolation.sineOut);

        if(shuffleSequence == null) shuffleSequence = new SequenceAction();
        else shuffleSequence.reset();

        shuffleSequence.addAction(curveActionIn);
        shuffleSequence.addAction(curveRotateBy);
        shuffleSequence.addAction(curveActionOut);
        shuffleSequence.addAction(curveBackAction);

        if(callback != null){
            if(shuffleEnd == null) shuffleEnd = new RunnableAction();
            else shuffleEnd.reset();

            shuffleEnd.setRunnable(callback);
            shuffleSequence.addAction(shuffleEnd);
        }

        addAction(shuffleSequence);
    }






    public void init(char c, int id, Dial dial){
        this.c = c;
        this.id = id;
        this.dial = dial;

        bgEffect.setVisible(false);

        label.setText(c+"");
        setSelected(false);
    }






    private void centerText(){
        GlyphLayout layout = Pools.obtain(GlyphLayout.class);
        layout.setText(label.getStyle().font, label.getText());
        label.setX((getWidth() - layout.width * label.getFontScaleX()) * 0.5f);
        label.setY((getHeight() - label.getHeight()) * 0.5f);
        Pools.free(layout);
    }





    private static Label createLabel(char c, Label.LabelStyle labelStyle){
        Label label = new Label(String.valueOf(c), labelStyle);
        label.setOrigin(Align.bottomLeft);
        return label;
    }





    private void resetAnimation(){
        if(selectionParallelAction == null){
            selectionParallelAction = new ParallelAction();
            selectionScaleAction = new ScaleToAction();
            selectionAlphaAction = new AlphaAction();
        }else{
            selectionParallelAction.reset();
            selectionScaleAction.reset();
            selectionAlphaAction.reset();
        }

        selectionScaleAction.setScale(1.3f);
        selectionScaleAction.setDuration(0.3f);

        selectionAlphaAction.setAlpha(0.0f);
        selectionAlphaAction.setDuration(0.3f);

        selectionParallelAction.addAction(selectionScaleAction);
        selectionParallelAction.addAction(selectionAlphaAction);
    }





    @Override
    public void setColor(Color color) {
        background.setColor(color);
        bgEffect.setColor(color);
    }




    @Override
    public boolean equals(Object o) {
        DialButton other = (DialButton)o;
        return this.id == other.id;
    }




    public char getChar(){
        return c;
    }




    public void setSelected(boolean selected){
        background.setVisible(selected);
        if(selected){
            label.setStyle(labelStyleDown);
            animateBgEffect();
        }else{
            removeLine();
            label.setStyle(labelStyleUp);
        }
    }





    private void animateBgEffect(){
        resetAnimation();
        bgEffect.setVisible(true);
        bgEffect.setScale(1.0f);
        Color color = bgEffect.getColor();
        bgEffect.setColor(color.r, color.g, color.b,1);
        bgEffect.addAction(selectionParallelAction);
    }





    public void removeLine(){
        isLineSnapped = false;

        if(line != null){
            line.setVisible(false);
        }
    }







    public void setScale(float scaleXY, int numButtons) {
        setScale(scaleXY);
        if(label != null){
            label.setFontScale((this.getHeight() * this.getScaleX() * (UIConfig.getDialButtonLetterFontScale(numButtons))) / label.getHeight());
            centerText();
        }
    }




    @Override
    public String toString() {
        return c+"";
    }




    @Override
    public void reset() {
        isLineSnapped = false;
        CurveActionIn.motionAngle = 0;
    }


}
