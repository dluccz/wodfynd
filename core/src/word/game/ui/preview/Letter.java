package word.game.ui.preview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;

import word.game.config.UIConfig;
import word.game.managers.ResourceManager;

public class Letter extends Group implements Pool.Poolable {

    ScaleToAction scaleToAction;
    public Label label;
    public char letter;
    public Vector2 vec2Zero;


    public void setLetter(char c, Label.LabelStyle style){

        if(label == null){
            label = createLabel(c, style);
            label.setAlignment(Align.bottomLeft);
            label.setFontScale(UIConfig.PREVIEW_FONT_SCALE);
            addActor(label);
        }else{
            label.setText(c + "");
        }

        letter = c;

        setSize(label.getPrefWidth(), label.getPrefHeight());
        setOrigin(Align.right);
        setScale(0);
    }


    public float getRawHeight(){
        return label.getHeight();
    }


    public Vector2 getVec2Zero(){
        if(vec2Zero == null) vec2Zero = new Vector2();
        vec2Zero.x = 0;
        vec2Zero.y = 0;
        return vec2Zero;
    }





    public static Label createLabel(char c, Label.LabelStyle labelStyle){
        return new Label(c + "", labelStyle);
    }




    @Override
    public void reset() {
        setScale(0);
        setPosition(0, 0);
        scaleToAction.reset();
    }



}
