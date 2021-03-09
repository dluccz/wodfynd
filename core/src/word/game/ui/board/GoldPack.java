package word.game.ui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.ui.Glitter;


public class GoldPack extends Group {


    private Label label;
    private int count;
    public boolean hit;


    public GoldPack(ResourceManager resourceManager){
        setTouchable(Touchable.disabled);
        Image gold_pack = new Image(AtlasRegions.gold_pack);
        addActor(gold_pack);
        setSize(gold_pack.getWidth(), gold_pack.getHeight());

        Label.LabelStyle wordTitlelabelStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.BLACK);;

        label = new Label("", wordTitlelabelStyle);
        label.setOrigin(Align.bottomLeft);
        label.setAlignment(Align.bottomLeft);
        addActor(label);

        Glitter glitter = new Glitter(0, 0, getWidth(), getHeight());
        addActor(glitter);
        glitter.running = true;
    }



    public void setCount(int count){
        this.count = count;
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, Integer.toString(count));
        label.setText(count);
        label.setX((getWidth() - glyphLayout.width) * 0.5f);
        label.setY(getHeight() * 0.25f);
        Pools.free(glyphLayout);
        GameData.setNumberOfGoldPackMoves(count);
    }



    public int getCount(){
        return count;
    }



    public void remove(Runnable callback) {
        Action grow = Actions.scaleBy(0.1f, 0.1f, 0.1f);
        ParallelAction parallelAction = new ParallelAction();
        parallelAction.addAction(Actions.scaleTo(0, 0, 0.3f));
        parallelAction.addAction(Actions.fadeOut(0.3f));

        Action run = Actions.run(callback);
        addAction(new SequenceAction(grow, parallelAction, run));
    }
}
