package word.game.ui.confetti;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;

import word.game.config.UIConfig;
import word.game.managers.ResourceManager;

public class Confetti{



    private static final Face[] faces = new Face[]{
            new Face(new Color(0xFF/255f, 0x00/255f, 0x00/255f,1), new Color(0xDC/255f, 0x14/255f, 0x3C/255f,1)),
            new Face(new Color(0x90/255f, 0xEE/255f, 0x90/255f,1), new Color(0x3C/255f, 0xB3/255f, 0x71/255f,1)),
            new Face(new Color(0x00/255f, 0x00/255f, 0xFF/255f,1), new Color(0x00/255f, 0x00/255f, 0x8B/255f,1)),
            new Face(new Color(0xFF/255f, 0xFF/255f, 0xE0/255f,1), new Color(0xFF/255f, 0xFF/255f, 0x00/255f,1)),
            new Face(new Color(0xFF/255f, 0xA5/255f, 0x00/255f,1), new Color(0xFF/255f, 0x8C/255f, 0x00/255f,1)),
            new Face(new Color(0xFF/255f, 0xC0/255f, 0xCB/255f,1), new Color(0xFF/255f, 0x69/255f, 0xB4/255f,1)),
            new Face(new Color(0xFF/255f, 0x00/255f, 0xFF/255f,1), new Color(0xDA/255f, 0x70/255f, 0xD6/255f,1)),
            new Face(new Color(0x40/255f, 0xE0/255f, 0xD0/255f,1), new Color(0x00/255f, 0xCE/255f, 0xD1/255f,1))
    };

    public boolean running;
    private Paper[] confettis;

    private float width, height;
    private float gravity = 0.5f;


    private float terminalVelocity;
    private float drag = 0.075f;

    public Confetti(float width, float height, int count){

        this.width = width;
        this.height = height;
        confettis = new Paper[count];

        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();

        terminalVelocity = UIConfig.WHEEL_DIALOG_CONFETTI_VELOCITY * Gdx.graphics.getDensity() / aspectRatio;

        for(int i = 0; i < count; i++){
            Paper paper = new Paper();
            resetPaper(paper);
            confettis[i] = paper;
        }


    }




    private void resetPaper(Paper paper){
        paper.face = faces[MathUtils.random(0, faces.length - 1)];
        paper.dimension_x = MathUtils.random(Gdx.graphics.getWidth() * 0.025f, Gdx.graphics.getWidth() * 0.05f);
        paper.dimension_y = MathUtils.random(Gdx.graphics.getHeight() * 0.025f, Gdx.graphics.getHeight() * 0.05f);
        paper.position_x = width * 0.5f;
        paper.position_y = height * 0.5f;
        paper.rotation = MathUtils.random() * 360f;
        paper.velocity_x = MathUtils.random(-25, 25);
        paper.velocity_y = MathUtils.random(Gdx.graphics.getHeight() * 0.05f, 0);
        paper.velocity_y *= -1;
        paper.dead = false;
    }



    public void reset(){
        for(Paper paper : confettis)
            resetPaper(paper);
    }



    public void render(Batch batch){
        if(running){

            for(int i = 0; i < confettis.length; i++){
                Paper paper = confettis[i];
                if(paper.dead)continue;

                paper.setX(paper.position_x);
                paper.setY(paper.position_y);
                paper.setRotation(paper.rotation);
                paper.setSize(paper.dimension_x, paper.dimension_y);
                paper.setScale(paper.getScaleX(), paper.scale_y);

                paper.velocity_x -= paper.velocity_x * drag;
                paper.velocity_y = Math.min(paper.velocity_y + gravity, terminalVelocity);
                paper.velocity_x += MathUtils.randomBoolean() ? MathUtils.random() : -MathUtils.random();

                paper.position_x += paper.velocity_x;
                paper.position_y -= paper.velocity_y;

                if(paper.position_y <= -paper.getHeight()){
                    paper.dead = true;
                    continue;
                }

                if(paper.position_x > width)
                    paper.position_x = 0;


                paper.scale_y = MathUtils.cos(paper.position_y * 0.05f / Gdx.graphics.getDensity());
                paper.setColor(paper.scale_y > 0 ? paper.face.front : paper.face.back);
                paper.setScale(1f, paper.getScaleY());
                paper.setColor(paper.getColor().r, paper.getColor().g, paper.getColor().b, batch.getColor().a);
                paper.draw(batch);

            }
        }
    }



}
