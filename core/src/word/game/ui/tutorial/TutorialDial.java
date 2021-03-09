package word.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.model.Constants;
import word.game.model.GameData;

import word.game.model.Word;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;

import word.game.ui.dial.Dial;
import word.game.ui.dial.DialButton;

public class TutorialDial extends Tutorial{


    private int wordIndex;
    private int letterIndex;
    private Array<DialButton> dialButtons;
    private Array<Word> words;
    private Dial dial;
    private MoveToAction moveToAction;
    private RunnableAction runnableAction;
    private float animTime = 1f;
    private Array<DialButton> dialButtonsToAnimate = new Array<>();
    private AlphaAction alphaAction1;
    private AlphaAction alphaAction2;
    public boolean correctAnswerAnimating;
    protected Image help_hand;



    public TutorialDial(BaseScreen screen, Dial dial) {
        super(screen);
        this.dial = dial;
        step = Constants.TUTORIAL_DIAL;
        paddingX = 1.03f;
    }




    public void indicateLetters(Array<DialButton> dialButtons, Array<Word> words){
        this.dialButtons = dialButtons;
        this.words = words;
        help_hand = new Image(AtlasRegions.help_hand);
        help_hand.setRotation(20f);
        help_hand.setVisible(false);
        help_hand.setTouchable(Touchable.disabled);
        screen.stage.addActor(help_hand);

        wordIndex = GameData.getSolvedWords().size() - 1;
        nextWord();
    }



    public void nextWord(){
        wordIndex++;
        letterIndex = 0;
        showText(getText());
        findDialButtonsToAnimate();
        step_0();
    }



    private String getText(){
        String text = UIConfig.INTERACTIVE_TUTORIAL_TEXT_COLOR + LanguageManager.get(wordIndex == 0 ? "dial_tutorial_1" : "dial_tutorial_2") + "[]";
        StringBuilder sb = new StringBuilder(words.get(wordIndex).answer.length() + words.get(wordIndex).answer.length() - 1);

        String delim = "";
        for(int i = 0; i < words.get(wordIndex).answer.length(); i++){
            sb.append(delim);
            sb.append(words.get(wordIndex).answer.charAt(i));
            if(delim.isEmpty()) delim = "-";
        }

        text = text.replaceFirst("\\{0\\}", UIConfig.INTERACTIVE_TUTORIAL_DASHED_DIAL_WORD_COLOR + sb.toString() + "[]");
        return text;
    }


    private void step_0(){
        fadeIn(endOfStep0);
    }



    private Runnable endOfStep0 = new Runnable() {
        @Override
        public void run() {
            step_1();
        }
    };



    private void step_1(){
        help_hand.clearActions();
        help_hand.getColor().a = 0;
        help_hand.setVisible(true);

        DialButton currentButton = dialButtonsToAnimate.get(letterIndex);
        Vector2 currentPos = currentButton.localToActorCoordinates(this.getParent(), new Vector2());
        help_hand.setPosition(currentPos.x + currentButton.getWidth() * 0.5f, currentPos.y - currentButton.getHeight() * 0.5f);

        if(alphaAction1 == null)
            alphaAction1 = new AlphaAction();
        else
            alphaAction1.reset();

        alphaAction1.setAlpha(1f);
        alphaAction1.setDuration(0.5f);

        if(runnableAction == null)
            runnableAction = new RunnableAction();
        else
            runnableAction.reset();

        runnableAction.setRunnable(endOfStep1);

        help_hand.addAction(Actions.sequence(alphaAction1, runnableAction));
    }




    private Runnable endOfStep1 = new Runnable() {
        @Override
        public void run() {
            step_2();
        }
    };




    private void step_2(){
        DialButton nextButton = dialButtonsToAnimate.get(letterIndex + 1);
        Vector2 nextPos = nextButton.localToActorCoordinates(this.getParent(), new Vector2());

        if(moveToAction == null)
            moveToAction = new MoveToAction();
        else
            moveToAction.reset();

        moveToAction.setPosition(nextPos.x + nextButton.getWidth() * 0.5f, nextPos.y - nextButton.getHeight() * 0.5f);
        moveToAction.setDuration(0.7f);
        moveToAction.setInterpolation(Interpolation.sineOut);

        if(runnableAction == null)
            runnableAction = new RunnableAction();
        else
            runnableAction.reset();

        runnableAction.setRunnable(endOfStep2);

        help_hand.addAction(Actions.sequence(moveToAction, runnableAction));
    }





    private Runnable endOfStep2 = new Runnable() {

        @Override
        public void run() {
            if(letterIndex == dialButtonsToAnimate.size - 2) {
                step_3();
                return;
            }

            letterIndex++;
            step_2();
        }
    };





    private void step_3(){

        if(alphaAction1 == null)
            alphaAction1 = new AlphaAction();
        else
            alphaAction1.reset();

        alphaAction1.setAlpha(0);
        alphaAction1.setDuration(0.5f);

        runnableAction.reset();
        runnableAction.setRunnable(new Runnable() {

            @Override
            public void run() {
                dial.clearSelection();
                letterIndex = 0;
                step_1();
            }
        });


        help_hand.addAction(Actions.sequence(alphaAction1, runnableAction));
    }





    @Override
    public void showText(String text) {
        super.showText(text);
        textContainer.setX((screen.stage.getWidth() - textContainer.getWidth()) * 0.5f);
        textContainer.setY(dial.getY() + dial.getHeight() * 1.3f);
    }




    public Word getCurrentWord(){
        return words.get(wordIndex);
    }




    private void findDialButtonsToAnimate(){
        char[] chars = words.get(wordIndex).answer.toCharArray();

        dialButtonsToAnimate.clear();

        outer:for(int i = 0; i < chars.length; i++){
            for(int j = 0; j < dialButtons.size; j++){
                if(chars[i] == dialButtons.get(j).getChar() && !dialButtonsToAnimate.contains(dialButtons.get(j), false)){
                    dialButtonsToAnimate.add(dialButtons.get(j));
                    if(dialButtonsToAnimate.size == chars.length) break outer;
                    break;
                }
            }
        }
    }





    public void pauseAnimation(){
        help_hand.clearActions();

        if(alphaAction2 == null)
            alphaAction2 = new AlphaAction();
        else
            alphaAction2.reset();

        alphaAction2.setAlpha(0f);
        alphaAction2.setDuration(0.3f);
        help_hand.addAction(alphaAction2);

        fadeOut(null, false);
    }



    public void resumeAnimation(){
        letterIndex = 0;
        clearActions();
        fadeIn(null);
        step_1();
    }


    @Override
    public boolean remove() {
        help_hand.remove();
        help_hand = null;
        return super.remove();
    }



}
