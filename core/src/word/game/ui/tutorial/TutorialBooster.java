package word.game.ui.tutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Pools;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;

public class TutorialBooster extends Tutorial{

    public TextButton gotit;
    private Color buttonDownColor;


    public TutorialBooster(BaseScreen screen) {
        super(screen);
        paddingX = 1.005f;
    }





    public void setGotIt(String text){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        String font = UIConfig.INTERACTIVE_TUTORIAL_GOT_IT_USE_SHADOW_FONT ? ResourceManager.fontSemiBold : ResourceManager.fontSemiBoldShadow;
        buttonStyle.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
        buttonStyle.fontColor = UIConfig.INTERACTIVE_TUTORIAL_GOT_IT_TEXT_COLOR;
        Color color = UIConfig.INTERACTIVE_TUTORIAL_GOT_IT_BACKGROUND_COLOR;
        NinePatch up = NinePatches.flat_btn;
        up.setColor(color);

        NinePatch down = new NinePatch(NinePatches.flat_btn);
        buttonDownColor = Pools.obtain(Color.class);
        buttonDownColor.set(color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, 1f);
        down.setColor(buttonDownColor);

        buttonStyle.up = new NinePatchDrawable(up);
        buttonStyle.down = new NinePatchDrawable(down);

        gotit = new TextButton(text, buttonStyle);
        float extra = gotit.getWidth() * 0.3f;
        gotit.setWidth(gotit.getWidth() + extra);
        gotit.setHeight(gotit.getHeight() + extra);

        gotit.setX((screen.stage.getWidth() - gotit.getWidth()) * 0.5f);
        gotit.setY(textContainer.getY() - gotit.getHeight() * 1.5f);

        addActor(gotit);
    }



    public void setGotItListener(ChangeListener listener){
        gotit.addListener(listener);
    }



    @Override
    public void showText(String text) {
        super.showText(text);
        textContainer.setX((screen.stage.getWidth() - textContainer.getWidth()) * 0.5f);
        textContainer.setY(actorPos.y - actor.getHeight() * actor.getScaleY() - textContainer.getHeight() * 1.1f);
    }




    @Override
    public boolean remove() {
        if(buttonDownColor != null) Pools.free(buttonDownColor);
        return super.remove();
    }
}
