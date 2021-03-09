package word.game.ui.dial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;

import word.game.actions.CurveActionIn;
import word.game.config.ConfigProcessor;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.controllers.GameController;
import word.game.graphics.AtlasRegions;
import word.game.graphics.shader.LineShader;
import word.game.managers.ResourceManager;
import word.game.model.Constants;
import word.game.pool.Pools;
import word.game.screens.GameScreen;
import word.game.ui.tutorial.TutorialDial;


public class Dial extends Group implements Disposable {



    public Array<DialButton> dialButtons = new Array<>(true, Constants.MAX_LETTERS);
    public Array<DialButton> selectedButtons;
    private float rotationStartAngle = 0.0f;
    private float rotationIncrement = 0;
    private char[] letters;
    private Color themeColor;
    private Group lineContainer;
    private Vector3 pointA, pointB;
    private GameScreen gameScreen;
    private GameController gameController;
    private StringBuilder stringBuilder;
    private Vector2 temp = new Vector2();
    private Array<Float> angles;
    private Image background;


    public Dial(){
        background = new Image(AtlasRegions.dial);
        addActor(background);
        setSize(background.getWidth(), background.getHeight());
        setOrigin(Align.center);
        setTouchListeners();
    }




    public void setDialBackgroundColor(Color color){
        background.setColor(color);
    }



    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }




    @Override
    public void setColor(Color color) {
        themeColor = color;

        for(DialButton dialButton : dialButtons)
            dialButton.setColor(color);
    }




    @Override
    public Color getColor() {
        return themeColor;
    }




    public void setLetters(char[] letters){
        this.letters = letters;

        calculateOffsetAngleToMakeSymmetric();
        float buttonScale = UIConfig.calculateDialButtonScale(letters.length);

        for(int i = 0; i < letters.length; i++){
            DialButton dialButton = Pools.dialButtonPool.obtain();
            dialButton.init(letters[i], i, this);
            dialButton.setScale(buttonScale, letters.length);
            dialButtons.add(dialButton);
            dialButton.angle = rotationStartAngle * MathUtils.degreesToRadians;

            Vector2 position = calculateDialLetterPosition(dialButton.getChar());
            dialButton.setPosition(position.x - dialButton.getOriginX(), position.y - dialButton.getOriginY());
            addActor(dialButton);
        }
    }





    private void calculateOffsetAngleToMakeSymmetric(){

        switch(letters.length){
            case 3:
                rotationStartAngle = -30.0f;
                break;
            case 4:
                rotationStartAngle = 90.0f;
                break;
            case 5:
                rotationStartAngle = -55.0f;
                break;
            case 6:
                rotationStartAngle = -30.0f;
                break;
            case 7:
                rotationStartAngle = -65.0f;
                break;
            case 8:
                rotationStartAngle = -90.0f;
                break;
            case 9:
                rotationStartAngle = -70.0f;
                break;
            case 10:
                rotationStartAngle = -90.0f;
        }

        rotationIncrement = -1.0f / letters.length * 360.0f;
    }






    private Vector2 calculateDialLetterPosition(char c){
        final float halfWidth = getOriginX();
        final float radius = calculateRadius();
        float x = radius * (float)Math.cos(MathUtils.degreesToRadians * rotationStartAngle) + halfWidth;
        float y = radius * (float)Math.sin(MathUtils.degreesToRadians * rotationStartAngle) + halfWidth;

        rotationStartAngle += rotationIncrement;

        temp.x = x;
        temp.y = y;

        return temp;
    }




    public float calculateRadius(){
        return getOriginX() - dialButtons.get(0).getWidth() * dialButtons.get(0).getScaleX() * 0.5f - getWidth() * UIConfig.DIAL_BUTTON_MARGIN;
    }





    private void addDialButtonToVector(DialButton dialButton){
        if(selectedButtons == null) selectedButtons = new Array<>(true, Constants.MAX_LETTERS);

        if(selectedButtons.contains(dialButton, true))
            return;

        dialButton.setSelected(true);
        selectedButtons.add(dialButton);

        gameController.selectingLetters(selectedButtonsToString());

        if(!ConfigProcessor.muted) sfx();
    }





    private void sfx(){
        String file = null;
        int size = selectedButtons.size;

        if(size == 1) file = ResourceManager.SFX_SELECT_1;
        else if(size == 2) file = ResourceManager.SFX_SELECT_2;
        else if(size == 3) file = ResourceManager.SFX_SELECT_3;
        else if(size == 4) file = ResourceManager.SFX_SELECT_4;
        else if(size == 5) file = ResourceManager.SFX_SELECT_5;
        else if(size == 6) file = ResourceManager.SFX_SELECT_6;
        else if(size == 7) file = ResourceManager.SFX_SELECT_7;
        else if(size == 8) file = ResourceManager.SFX_SELECT_8;

        if(file != null){
            Sound sound = gameScreen.wordConnectGame.resourceManager.get(file, Sound.class);
            sound.play(SoundConfig.SFX_LETTER_SWIPE_VOLUME);
        }
    }





    public void dialButtonMouseDown(DialButton dialButton){
        addDialButtonToVector(dialButton);
    }



    public void dialButtonHovered(DialButton dialButton){
        if (!dialButton.isLineSnapped){
            addDialButtonToVector(dialButton);
        }else{
            if (selectedButtons.size >= 2){
                if (dialButton.id == selectedButtons.get(selectedButtons.size - 2).id){
                    selectedButtons.get(selectedButtons.size - 1).removeLine();
                    selectedButtons.get(selectedButtons.size - 2).isLineSnapped = false;
                    selectedButtons.get(selectedButtons.size - 1).setSelected(false);
                    selectedButtons.pop();
                    gameController.selectingLetters(selectedButtonsToString());
                }
            }
        }
    }






    private void dialButtonMouseUp(){
        if(selectedButtons == null) return;

        String answer = null;

        if(selectedButtons.notEmpty())
            answer = selectedButtonsToString();

        if(answer != null && !answer.isEmpty()) {
            gameController.selectingLettersFinished(answer);
        }
    }





    private String selectedButtonsToString(){
        if(stringBuilder == null) stringBuilder = new StringBuilder(Constants.MAX_LETTERS);
        else stringBuilder.clear();

        for (int i = 0; i < selectedButtons.size; i++)
            stringBuilder.append(selectedButtons.get(i).getChar());

        return stringBuilder.toString();
    }




    private void drawConnections(float x, float y){
        if(selectedButtons == null) return;

        for(int i = 0; i < selectedButtons.size; i++){
            DialButton dialButtonToDrawLine = selectedButtons.get(i);

            if (dialButtonToDrawLine.line == null || !dialButtonToDrawLine.line.isVisible()){

                createLineForDialButton(dialButtonToDrawLine);
                dialButtonToDrawLine.line.setVisible(true);
                if(i > 0 && !selectedButtons.get(i - 1).isLineSnapped){
                    selectedButtons.get(i - 1).isLineSnapped = true;

                    drawLineBetweenPoints(
                            selectedButtons.get(i - 1).line,
                            dialButtonToDrawLine.getX() + dialButtonToDrawLine.getOriginX(),
                            dialButtonToDrawLine.getY() + dialButtonToDrawLine.getOriginY(),
                            selectedButtons.get(i - 1).getX() + dialButtonToDrawLine.getOriginX(),
                            selectedButtons.get(i - 1).getY() + dialButtonToDrawLine.getOriginY()
                    );

                }
            } else {

                if (!dialButtonToDrawLine.isLineSnapped){

                    drawLineBetweenPoints(
                            dialButtonToDrawLine.line,
                            x,
                            y,
                            dialButtonToDrawLine.getX() + dialButtonToDrawLine.getOriginX(),
                            dialButtonToDrawLine.getY() + dialButtonToDrawLine.getOriginY()
                    );
                }

            }
        }
    }





    private void drawLineBetweenPoints(LineShader line, float ax, float ay, float bx, float by) {

        if(pointA == null) pointA = new Vector3();
        pointA.x = getX() + ax;
        pointA.y = getY() + ay;
        getParent().getStage().getCamera().project(pointA);

        if(pointB == null) pointB = new Vector3();
        pointB.x = getX() + bx;
        pointB.y = getY() + by;
        getParent().getStage().getCamera().project(pointB);

        line.pointAx = pointA.x;
        line.pointAy = pointA.y;
        line.pointBx = pointB.x;
        line.pointBy = pointB.y;
    }





    private void createLineContainer(){
        lineContainer = new Group();
        lineContainer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lineContainer.setTouchable(Touchable.disabled);
        lineContainer.setPosition(-getX(), -getY());
        addActorAt(1, lineContainer);
    }





    private void createLineForDialButton(DialButton dialButtonToDrawLine) {
        if(dialButtonToDrawLine.line == null) {
            LineShader line = new LineShader(gameScreen.wordConnectGame.resourceManager.get(ResourceManager.SHADER_LINE, ShaderProgram.class));
            line.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            dialButtonToDrawLine.line = line;
            if(lineContainer == null) createLineContainer();
            lineContainer.addActor(line);
        }
        dialButtonToDrawLine.line.letterCount = dialButtons.size;
        dialButtonToDrawLine.line.setColor(themeColor);
    }





    public InputListener inputListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

            for(DialButton dialButton : dialButtons){
                if(isTouchInBounds(dialButton, x, y)) {
                    pauseTutorial();
                    dialButtonMouseDown(dialButton);
                }
            }

            return true;
        }


        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            drag(x, y);
        }


        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            dialButtonMouseUp();
        }
    };




    private void pauseTutorial(){
        if(gameScreen.tutorial != null){
            if(gameScreen.tutorial instanceof TutorialDial){
                TutorialDial tutorialDial = (TutorialDial)gameScreen.tutorial;
                tutorialDial.pauseAnimation();
            }
        }
    }





    public void drag(float x, float y){

        for(DialButton dialButton : dialButtons){
            if(isTouchInBounds(dialButton, x, y)) {
                dialButtonHovered(dialButton);
            }
            drawConnections(x, y);
        }
    }




    private void setTouchListeners(){
        addListener(inputListener);
    }





    private static boolean isTouchInBounds(DialButton actor, float x, float y){

        float radius = actor.getOriginX() * actor.getScaleX();

        float centerX = actor.getX() + actor.getOriginX();
        float centerY = actor.getY() + actor.getOriginY();

        float diffX = x - centerX;
        float diffY = y - centerY;

        return radius >= Math.sqrt(diffX * diffX + diffY * diffY);
    }






    public void shuffle(){
        getStage().getRoot().setTouchable(Touchable.disabled);

        if(CurveActionIn.motionAngle == 0)
            CurveActionIn.motionAngle = Math.abs(dialButtons.get(1).angle - dialButtons.get(0).angle);

        if(angles == null) angles = new Array<>();
        else angles.clear();

        for(int i = 0; i < dialButtons.size; i++) angles.add(dialButtons.get(i).angle);

        for(int i = 0; i < dialButtons.size; i++){
            while(dialButtons.get(i).angle == angles.get(i)) angles.shuffle();
        }


        for(int i = 0; i < angles.size; i++){
            dialButtons.get(i).shuffle(angles.get(i), i == angles.size - 1 ? shuffleComplete : null);
        }

    }





    private Runnable shuffleComplete = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
            gameScreen.resumeIdleTimer();
        }
    };






    public void clearSelection(){
        if(selectedButtons != null) {
            for (DialButton dialButton : selectedButtons) {
                dialButton.setSelected(false);
                if (dialButton.line != null)
                    drawLineBetweenPoints(dialButton.line, dialButton.getX(), dialButton.getY(), dialButton.getX(), dialButton.getY());
            }
            selectedButtons.clear();
        }
    }






    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }






    public void clearContent(){
        for(DialButton dialButton : dialButtons){
            dialButton.remove();
            Pools.dialButtonPool.free(dialButton);
        }
        dialButtons.clear();
        if(selectedButtons != null) selectedButtons.clear();
    }






    @Override
    public void dispose() {
        for(DialButton dialButton : dialButtons){
            if(dialButton.line != null){
                dialButton.line.dispose();
            }
        }
        dialButtons.clear();
    }
}
