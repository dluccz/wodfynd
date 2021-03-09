package word.game.ui.preview;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;
import word.game.pool.Pools;
import word.game.screens.GameScreen;
import word.game.util.UiUtil;

public class Preview extends Group {


    private Label.LabelStyle previewStyle;
    private float padding;
    private Image bg;
    public Array<Letter> letters = new Array<>();
    private boolean shaking;
    private AlphaAction alphaAction;
    private SizeToAction sizeToAction;
    private float time = 0.2f;
    private boolean dirty;
    private Color bgColor;
    BitmapFont bmfont;




    public Preview(GameScreen screen){
        getColor().a = 0;

        NinePatch ninePatch = NinePatches.preview;
        padding = ninePatch.getLeftWidth();

        bg = new Image(ninePatch);
        bg.setWidth(padding * 2);
        addActor(bg);

        bmfont = screen.wordConnectGame.resourceManager.get(ResourceManager.fontBoardAndDialFont, BitmapFont.class);

        previewStyle = new Label.LabelStyle(bmfont, UIConfig.PREVIEW_TEXT_COLOR);

        setHeight(bg.getHeight());
    }






    public void setAnimatedText(String text){
        if(dirty) reset();

        bg.setColor(bgColor);

        alpha(1f);

        if(text.length() > letters.size)
            addNewLetter(text);
        else
            removeLetter();

        bg.setX((getWidth() - bg.getWidth()) * 0.5f);
        setWidth(bg.getWidth());
        setX((getStage().getWidth() - getWidth()) * 0.5f);
    }






    private float findKerning(String text, Letter letter){
        GlyphLayout glyphLayout = com.badlogic.gdx.utils.Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(bmfont, text);

        float x = glyphLayout.width * UIConfig.PREVIEW_FONT_SCALE - (letter.getWidth());
        com.badlogic.gdx.utils.Pools.free(glyphLayout);

        return x;
    }



    private void addNewLetter(String text){
        char last = text.charAt(text.length() - 1);
        Letter letter = Pools.letterPool.obtain();
        letter.setLetter(last, previewStyle);

        shaking = false;

        if(sizeToAction == null) sizeToAction = new SizeToAction();
        else sizeToAction.reset();

        float x = padding + findKerning(text, letter);

        float targetWidth = x + letter.getWidth() + padding;

        sizeToAction.setWidth(targetWidth);
        sizeToAction.setHeight(bg.getHeight());
        sizeToAction.setDuration(time);
        bg.addAction(sizeToAction);

        addActor(letter);
        letters.add(letter);

        letter.setX(x);
        letter.setY((bg.getHeight() - letter.getHeight()) * 0.6f);

        if(letter.scaleToAction == null)
            letter.scaleToAction = new ScaleToAction();
        else
            letter.scaleToAction.reset();

        letter.scaleToAction.setScale(1f);
        letter.scaleToAction.setDuration(time);
        letter.addAction(letter.scaleToAction);
    }





    private void removeLetter(){
        Letter lastLetter = letters.pop();
        if(letters.isEmpty()) return;

        lastLetter.scaleToAction.reset();
        lastLetter.scaleToAction.setScale(0);
        lastLetter.scaleToAction.setDuration(time * 0.5f);
        lastLetter.addAction(lastLetter.scaleToAction);

        Letter futureLastLetter = letters.get(letters.size - 1);
        float targetWidth = futureLastLetter.getX() + futureLastLetter.getWidth() + padding;

        sizeToAction.reset();
        sizeToAction.setWidth(targetWidth);
        sizeToAction.setHeight(bg.getHeight());
        sizeToAction.setDuration(time);
        bg.addAction(sizeToAction);
    }



    public void setDirty(){
        dirty = true;
    }



    public void reset(){
        for(Letter letter : letters){
            letter.clearActions();
            letter.remove();
            Pools.letterPool.free(letter);
        }

        letters.clear();
        bg.clearActions();
        clearActions();
        bg.setWidth(padding * 2);
        dirty = false;
    }




    public void shake(){
        dirty = true;
        shaking = true;
        UiUtil.shake(this, true,padding * 2, shakerCallback);
    }



    private Runnable shakerCallback = new Runnable() {
        @Override
        public void run() {
            alpha(0);
        }
    };



    private void alpha(float value){
        if(alphaAction == null) alphaAction = new AlphaAction();
        else alphaAction.reset();
        alphaAction.setAlpha(value);
        alphaAction.setDuration(0.2f);
        addAction(alphaAction);
    }



    public void fadeOut(){
        dirty = true;
        alpha(0);
    }




    @Override
    public void setColor(Color color) {
        bgColor = color;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(!shaking) {
            setWidth(bg.getWidth());

            bg.setX(letters.size <= 1 ? 0 : (getWidth() - bg.getWidth()) * 0.5f);
            setX((getStage().getWidth() - getWidth()) * 0.5f);
        }
    }












}
