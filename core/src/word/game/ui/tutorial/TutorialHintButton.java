package word.game.ui.tutorial;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;

import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;

public class TutorialHintButton extends Tutorial{


    public TutorialHintButton(BaseScreen screen) {
        super(screen);
        getColor().a = 0;
    }




    @Override
    public void showText(String text) {
        super.showText(text);
        textContainer.setX((screen.stage.getWidth() - textContainer.getWidth()) * 0.5f);
        textContainer.setY(screen.stage.getHeight() * 0.5f);
        fadeIn(null);
    }

}
