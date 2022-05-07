package word.game.ui.dial;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Disposable;

import word.game.config.UIConfig;
import word.game.graphics.shader.MeshShader;
import word.game.managers.ResourceManager;
import word.game.screens.GameScreen;

public class DialAnimationContainer extends Group implements Disposable {

    private ResourceManager resourceManager;
    private Actor dial;
    private boolean running;
    public MeshShader dialAnimation;

    private boolean autoIncrease;
    private float autoIncreaseTime;
    private float autoIncreaseInterval;
    private int autoIncreaseIndex;
    private int targetState;
    private boolean fadeInShader;

    private ParticleEmitter particleEmitter;
    private ParticleSettings particleSettings = new ParticleSettings();
    private ShaderProgram shaderProgram;





    public DialAnimationContainer(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        getColor().a = 0f;
    }





    public void setDial(Actor dial){
        this.dial = dial;
    }





    public void init(){
        if(particleEmitter == null) {
            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            setParticleEmitter();
        }

        if(shaderProgram == null)
            shaderProgram = resourceManager.get(ResourceManager.SHADER_DIAL, ShaderProgram.class);

        if(dialAnimation == null) {
            dialAnimation = new MeshShader(shaderProgram);
            dialAnimation.setPaused(true);
            dialAnimation.setWidth(Gdx.graphics.getWidth() );
            dialAnimation.setHeight(Gdx.graphics.getHeight() );
            dialAnimation.setUniformFloat("u_dial_diameter", dial.getHeight() * (Gdx.graphics.getWidth() / getStage().getWidth()));
            dialAnimation.setUniformVec2("u_center", new Vector2(0.5f, (dial.getY() + dial.getHeight() * 0.5f) / getStage().getHeight()));
            addActor(dialAnimation);
        }
    }





    public void stop(){
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                running = false;
                dialAnimation.setPaused(true);
                dialAnimation.setVisible(false);
            }
        });

        addAction(new SequenceAction(Actions.fadeOut(0.1f), runnableAction));
    }





    public void setStage(int stage){
        
        switch (stage){
            case 0:
                stop();
                return;
            case 1:
                particleSettings.count = 0;
                dialAnimation.setPaused(false);
                if(!autoIncrease)dialAnimation.setVisible(true);
                break;
            case 2:
                particleSettings.count = 2;
                break;
            case 3:
                particleSettings.count = 4;
                particleSettings.speedMin   = getStage().getWidth() * 0.004f;
                particleSettings.speedMax   = getStage().getWidth() * 0.007f;
                break;
            case 4:
                particleSettings.count = 6;
                break;
            case 5:
                particleSettings.count = 8;
                particleSettings.speedMin   = getStage().getWidth() * 0.005f;
                particleSettings.speedMax   = getStage().getWidth() * 0.008f;
                break;
            case 6:
                particleSettings.count = 10;
                break;
            case 7:
                particleSettings.count = 12;
                particleSettings.speedMin   = getStage().getWidth() * 0.006f;
                particleSettings.speedMax   = getStage().getWidth() * 0.009f;
                break;
            case 8:
                particleSettings.count = 14;
                break;
            case 9:

                particleSettings.count = 16;
                break;
            case 10:
                particleSettings.speedMin   = getStage().getWidth() * 0.007f;
                particleSettings.speedMax   = getStage().getWidth() * 0.01f;
                particleSettings.count = 18;
                break;
            case 11:
                particleSettings.count = 20;
                break;
            case 12:
                particleSettings.count = 22;
                break;
            case 13:
                particleSettings.count = 24;
                break;
            case 14:
                particleSettings.count = 26;
                break;
            case 15:
                particleSettings.count = 27;
                break;
            case 16:
                particleSettings.count = 28;
                break;
            default:
                particleSettings.count = 28;
        }

        running = true;
        particleEmitter.setStage(stage);
        dialAnimation.setPaused(false);
        setVisible(true);

        if(getColor().a < 1f)
            fadeInShader = true;
    }





    private void setParticleEmitter(){
        particleSettings.count      = 0;
        particleSettings.gravity    = 0.8f;
        particleSettings.speedMin   = getStage().getWidth() * 0.003f;
        particleSettings.speedMax   = getStage().getWidth() * 0.006f;
        particleSettings.friction   = 0.98f;
        particleEmitter = new ParticleEmitter(dial, particleSettings, 1);
    }





    public void setAutoIncrease(int state){
        autoIncreaseTime = 0;
        autoIncreaseInterval = 1f / (float)state;
        targetState = state;
        setVisible(true);
        getColor().a = 0;
        autoIncrease = true;
        fadeInShader = true;
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if(running && particleEmitter != null){
            particleEmitter.update(batch);
        }

        if(dialAnimation != null){
            dialAnimation.setUniformFloat("u_alpha", color.a);
        }

        batch.setColor(color.r, color.g, color.b, 1);

        if(autoIncrease){
            if(autoIncreaseTime >= autoIncreaseInterval){
                autoIncreaseIndex++;
                setStage(autoIncreaseIndex);
                autoIncreaseTime = 0;

                if(autoIncreaseIndex >= targetState){
                    autoIncrease = false;
                    fadeInShader = true;
                }
            }
            autoIncreaseTime += Gdx.graphics.getDeltaTime();
        }

        if(fadeInShader){
            getColor().a += 0.05f;
            if(getColor().a >= 1f){
                fadeInShader = false;
            }
        }

    }




    @Override
    public void dispose() {
        if(dialAnimation != null) dialAnimation.dispose();
    }
}
