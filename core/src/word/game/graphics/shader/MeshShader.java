package word.game.graphics.shader;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public  class MeshShader extends Group implements Disposable {


    private float time;

    public ShaderProgram shaderProgram;
    protected Mesh mesh;

    protected int Idx = 0;
    protected float[] vertices;

    protected Map<String, Float> floatUniforms;
    protected Map<String, Vector2> vec2Uniforms;
    protected Map<String, Vector3> vec3Uniforms;

    protected boolean paused;




    private boolean matrixDirty = false;

    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();


    public MeshShader(ShaderProgram shaderProgram){
        if (!shaderProgram.isCompiled()) {
            System.err.println(shaderProgram.getLog());
        }
        this.shaderProgram = shaderProgram;
        createMesh();
        setProjectionMatrix();


    }


    public MeshShader(String vsh, String fsh){

        shaderProgram = new ShaderProgram(vsh, fsh);
        if (!shaderProgram.isCompiled()) {
            System.err.println(shaderProgram.getLog());
        }
        createMesh();
        setProjectionMatrix();



    }





    protected void createMesh(){
        vertices = new float[4 * 2];
        mesh = new Mesh(true,4,0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
        );
    }




    public void setProjectionMatrix(){
        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        matrixDirty = true;
    }



    public void setPaused(boolean paused){
        time = 0;
        this.paused = paused;
    }


    public boolean isPaused(){
        return paused;
    }




    public void setUniformFloat(String name, float value){
        if(floatUniforms == null)
            floatUniforms = new HashMap<>();

        floatUniforms.put(name, value);
    }




    public void setUniformVec2(String name, Vector2 value){
        if(vec2Uniforms == null)
            vec2Uniforms = new HashMap<>();

        vec2Uniforms.put(name, value);
    }



    public void setUniformVec3(String name, Vector3 value){
        if(vec3Uniforms == null)
            vec3Uniforms = new HashMap<>();

        vec3Uniforms.put(name, value);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(isVisible()) {
            ShaderProgram sp = batch.getShader();
            renderMesh(getX(), getY(), getWidth(), getHeight());
            batch.setShader(sp);
        }
    }






    protected void renderMesh(float x, float y, float width, float height){

        if (Idx == vertices.length && (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.Desktop)) {
            flush();
        }

        vertices[Idx++] = x;
        vertices[Idx++] = y;

        vertices[Idx++] = x + width;
        vertices[Idx++] = y;

        vertices[Idx++] = x + width;
        vertices[Idx++] = y + height;

        vertices[Idx++] = x;
        vertices[Idx++] = y + height;
    }






    protected void flush(){
        if (Idx ==0)
            return;



        mesh.setVertices(vertices);

        Gdx.gl.glDepthMask(false);
        int vertexCount = (Idx /2);

        shaderProgram.bind();
        setUniforms();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mesh.render(shaderProgram, GL20.GL_TRIANGLE_FAN, 0, vertexCount);


        Gdx.gl.glDepthMask(true);

        Idx = 0;
    }






    protected void setUniforms(){

        if (matrixDirty) {
            combinedMatrix.set(projectionMatrix);
            Matrix4.mul(combinedMatrix.val, transformMatrix.val);
            matrixDirty = false;
        }


        shaderProgram.setUniformMatrix("u_projTrans", combinedMatrix);
        shaderProgram.setUniformf("u_resolution", getWidth(), getHeight());

        if(!paused) {
            time += Gdx.graphics.getDeltaTime();
        }


        if(time > 30) time = 0;

        shaderProgram.setUniformf("u_time", time);

        if(floatUniforms != null){
            for(String name : floatUniforms.keySet()){
                shaderProgram.setUniformf(name, floatUniforms.get(name));
            }
        }



        if(vec2Uniforms != null){
            for(String name : vec2Uniforms.keySet()){
                Vector2 vec2 = vec2Uniforms.get(name);
                shaderProgram.setUniformf(name, vec2.x, vec2.y);
            }
        }



        if(vec3Uniforms != null){
            for(String name : vec3Uniforms.keySet()){
                Vector3 vec3 = vec3Uniforms.get(name);
                shaderProgram.setUniformf(name, vec3.x, vec3.y, vec3.z);
            }
        }


    }



    public void translate (float x, float y, float z) {
        transformMatrix.translate(x, y, z);
        matrixDirty = true;
    }





    @Override
    public void dispose() {
        setVisible(false);
        try {
            mesh.dispose();
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
