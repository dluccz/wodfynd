package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.managers.LanguageManager;
import word.game.screens.BaseScreen;
import word.game.ui.Modal;
import word.game.ui.Toast;
import word.game.util.BackNavigator;


public class BoardOverlay extends Modal implements BackNavigator {

    private Action fadeInAction = Actions.fadeIn(0.3f);
    private RunnableAction openEnd = new RunnableAction();
    private SequenceAction openSequence = new SequenceAction();
    private Toast toast;

    private Runnable openRunnable = new Runnable() {
        @Override
        public void run() {
            if(getStage() != null) getStage().getRoot().setTouchable(Touchable.enabled);
            toast = boardView.gameScreen.showToast(LanguageManager.get("finger_hint_msg"));
            if(toast != null) toast.setY(boardView.getY() - toast.getHeight() * 1.2f);
        }
    };

    private Action fadeOutAction = Actions.fadeOut(0.3f);
    private RunnableAction closeEnd = new RunnableAction();
    private SequenceAction closeSequence = new SequenceAction();
    private Runnable closeCallback;
    private BoardView boardView;
    private BaseScreen screen;

    private Runnable endRunnable = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
            remove();
            if(closeCallback != null)closeCallback.run();
        }
    };

    public BoardOverlay(float width, float height, TextureRegion closeRegion, Actor button, BoardView boardView, BaseScreen screen){
        super(width, height);

        this.boardView = boardView;
        this.screen = screen;

        ImageButton.ImageButtonStyle closeStyle = new ImageButton.ImageButtonStyle();
        closeStyle.up = new TextureRegionDrawable(closeRegion);

        Button close = new Button(closeStyle);
        close.setPosition(button.getX(), button.getY());
        addActor(close);

        Color c = getColor();
        c.a = 0;
        setColor(c);


        close.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide(null);
            }
        });

    }



    public void hide(Runnable callback){
        if(toast != null){
            toast.remove();
            toast = null;
        }

        boardView.setFingerHintSelectionModeActive(false);
        fadeOutAction.reset();
        closeEnd.reset();
        closeSequence.reset();
        closeEnd.setRunnable(endRunnable);
        closeSequence.addAction(fadeOutAction);
        closeSequence.addAction(closeEnd);
        closeCallback = callback;
        addAction(closeSequence);
    }




    public void show(){
        notifyNavigationController(screen);
        fadeInAction.reset();
        openEnd.reset();
        openSequence.reset();
        openEnd.setRunnable(openRunnable);
        openSequence.addAction(fadeInAction);
        openSequence.addAction(openEnd);
        addAction(openSequence);
    }





    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }




    @Override
    public boolean navigateBack() {
        hide(null);
        return true;
    }
}
