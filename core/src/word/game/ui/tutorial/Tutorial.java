package word.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.graphics.shader.MeshShader;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.screens.BaseScreen;

import word.game.screens.GameScreen;
import word.game.screens.IntroScreen;
import word.game.ui.hint.IdleTimer;
import word.game.util.BackNavigator;

public class Tutorial extends Group implements Disposable, BackNavigator {




    public enum Shape{
        RECT,
        DISC
    }

    private AlphaAction alphaAction;
    public float paddingX = 1.2f;
    public float paddingY = 1.2f;
    protected BaseScreen screen;
    private ShaderProgram shaderProgram;
    private MeshShader meshShader;
    protected Actor actor;
    protected Label label;
    public Group textContainer;
    private Image textBg;
    public int step;
    protected Image arrow;
    protected Vector2 actorPos;
    public TutorialSaver tutorialSaver;


    public Tutorial(BaseScreen screen){
        this.screen = screen;
        notifyNavigationController(screen);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        getColor().a = 0;

        Image bg = new Image(AtlasRegions.rect);
        bg.setSize(screen.stage.getWidth(), screen.stage.getHeight());
        bg.getColor().a = 0f;
        addActor(bg);
    }





    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }




    @Override
    public boolean navigateBack() {
        if(step > 0) GameData.saveTutorialStep(step);
        if(tutorialSaver != null) tutorialSaver.save();

        if(screen instanceof IntroScreen){
            fadeOut(screen.nullifyTutorial, true);
        }else{
            GameScreen gameScreen = (GameScreen)screen;
            fadeOut(gameScreen.tutorialRemover, true);
        }

        return true;
    }





    public void highlightActor(Actor actor, Shape shape){
        IdleTimer.setPaused(true);
        this.actor = actor;
        if(shaderProgram == null){
            shaderProgram = screen.wordConnectGame.resourceManager.get(ResourceManager.SHADER_OVERLAY, ShaderProgram.class);
            meshShader = new MeshShader(shaderProgram);
            meshShader.setWidth(getWidth());
            meshShader.setHeight(getHeight());
            meshShader.setPaused(true);
            addActor(meshShader);
        }

        float actorWidth = actor.getWidth() * actor.getScaleX() * ResourceManager.scaleFactor * paddingX;
        float actorHeight = actor.getHeight() * actor.getScaleY() * ResourceManager.scaleFactor * paddingY;

        meshShader.setUniformVec2("u_size", new Vector2(actorWidth, actorHeight));

        Vector3 pos = new Vector3();

        boolean inRoot = actor.getParent().equals(screen.stage.getRoot());

        if(inRoot) {
            pos.set(actor.getX(), actor.getY(), 0);
        }else{
            Vector2 vector2 = actor.localToActorCoordinates(screen.stage.getRoot(), new Vector2());
            pos.set(vector2.x, vector2.y, 0);
        }

        actorPos = new Vector2(pos.x, pos.y);
        getParent().getStage().getCamera().project(pos);
        meshShader.setUniformVec2("u_position",
                inRoot ? new Vector2(
                        pos.x  + actor.getOriginX()  * ResourceManager.scaleFactor ,
                        pos.y  + actor.getOriginY()  * ResourceManager.scaleFactor
                ) :
                new Vector2(
                        pos.x + actor.getWidth() * actor.getScaleX() * 0.5f * ResourceManager.scaleFactor,
                        pos.y + actor.getHeight() * actor.getScaleY() * 0.5f * ResourceManager.scaleFactor
                )
        );
        meshShader.setUniformFloat("u_shape", (float)shape.ordinal());

        actor.setZIndex(this.getZIndex() + 1);
    }




    public void showText(String text){
        if(label == null) {
            textContainer = new Group();
            addActor(textContainer);

            textBg = new Image(NinePatches.round_rect_shadow);
            textContainer.addActor(textBg);

            Label.LabelStyle style = new Label.LabelStyle();
            String font = UIConfig.INTERACTIVE_TUTORIAL_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            style.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
            style.font.getData().markupEnabled = true;

            label = new Label(text, style);
            label.setAlignment(Align.center);
            label.setOrigin(Align.bottomLeft);
            label.setWrap(true);
            label.setWidth(screen.stage.getWidth() * 0.7f);
            textContainer.addActor(label);
        }else{
            label.setText(text);
        }

        textBg.setColor(UIConfig.INTERACTIVE_TUTORIAL_TEXT_BG_COLOR);

        float padding = getWidth() * 0.06f;

        textBg.setSize(Math.min(label.getWidth() * 1.1f, screen.stage.getWidth()), label.getPrefHeight() + padding);
        textContainer.setSize(textBg.getWidth(), textBg.getHeight());
        label.setX((textContainer.getWidth() - label.getWidth()) * 0.5f);
        label.setY((textContainer.getHeight() - label.getHeight()) * 0.5f);
    }





    public void indicateActor(float angle){
        arrow = new Image(AtlasRegions.arrow);
        arrow.setOrigin(Align.center);
        arrow.setRotation(angle);
        arrow.setTouchable(Touchable.disabled);
        arrow.getColor().a = 0f;
        addActor(arrow);
        arrow.addAction(Actions.fadeIn(0.2f));

        Vector2 p = actor.localToActorCoordinates(this, new Vector2());

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;

        if(angle == 90f){
            x1 = p.x + (actor.getWidth() * actor.getScaleX() - arrow.getWidth()) * 0.5f;
            y1 = p.y - arrow.getHeight() * 2f;
        }else if(angle == 180f){
            x1 = p.x + actor.getWidth() + arrow.getWidth() * 0.5f;
            y1 = p.y + actor.getHeight() * 0.5f - arrow.getHeight() * 0.5f;
        }else if(angle == 0f){
            x1 = p.x - arrow.getWidth() * 1.5f;
            y1 = p.y + actor.getHeight() * 0.5f - arrow.getHeight() * 0.5f;
        }else if(angle == -90f){
            x1 = p.x + (actor.getWidth() - arrow.getWidth()) * 0.5f;
            y1 = p.y + actor.getHeight() + arrow.getHeight();
        }

        arrow.setX(x1);
        arrow.setY(y1);
        float radian = MathUtils.degreesToRadians * angle;
        float halfHeight = arrow.getHeight() * 0.5f;
        x2 = arrow.getX() + MathUtils.cos(radian) * halfHeight;
        y2 = arrow.getY() + MathUtils.sin(radian) * halfHeight;

        arrow.addAction(Actions.forever(Actions.sequence(Actions.moveTo(x2, y2, 0.5f, Interpolation.sineOut), Actions.moveTo(x1, y1, 0.5f, Interpolation.sineOut))));
    }





    private void setAlphaAction(){
        if(alphaAction == null)
            alphaAction = new AlphaAction();
        else
            alphaAction.reset();
    }





    public void fadeIn(Runnable callback){
        setAlphaAction();
        alphaAction.setAlpha(1f);
        alphaAction.setDuration(0.3f);

        if(callback == null) addAction(alphaAction);
        else addAction(Actions.sequence(alphaAction, Actions.run(callback)));
    }








    public void fadeOut(Runnable callback, boolean pop){
        if(pop) screen.backNavQueue.pop();
        setAlphaAction();
        alphaAction.setAlpha(0f);
        alphaAction.setDuration(0.3f);

        if(callback == null) addAction(alphaAction);
        else addAction(Actions.sequence(alphaAction, Actions.run(callback)));
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {
        meshShader.setUniformFloat("u_alpha", getColor().a);
        super.draw(batch, parentAlpha);
    }




    @Override
    public boolean remove() {
        dispose();
        return super.remove();
    }





    @Override
    public void dispose() {
        if(meshShader != null) {
            meshShader.dispose();
        }
    }



    public interface TutorialSaver{
        void save();
    }
}
