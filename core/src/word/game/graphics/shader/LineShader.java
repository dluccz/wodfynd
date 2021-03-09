package word.game.graphics.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;

public class LineShader extends MeshShader{


    public float pointAx;
    public float pointAy;

    public float pointBx;
    public float pointBy;


    private Color themeColor;
    public int letterCount;


    public LineShader(ShaderProgram shaderProgram) {
        super(shaderProgram);
    }


    @Override
    public void setUniforms(){
        super.setUniforms();

        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        shaderProgram.setUniformf("thickness",     UIConfig.getLengthOfLinesBetweenDialLetters(letterCount) * Gdx.graphics.getDensity() / aspectRatio);
        shaderProgram.setUniformf("r", themeColor.r);
        shaderProgram.setUniformf("g", themeColor.g);
        shaderProgram.setUniformf("b", themeColor.b);
        shaderProgram.setUniformf("pointA", pointAx, pointAy);
        shaderProgram.setUniformf("pointB", pointBx, pointBy);

    }


    @Override
    public void setColor(Color color) {
        themeColor = color;
    }
}
