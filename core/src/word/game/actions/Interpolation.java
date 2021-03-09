package word.game.actions;

import com.badlogic.gdx.math.MathUtils;

public class Interpolation {


    public static final com.badlogic.gdx.math.Interpolation cubicIn = new com.badlogic.gdx.math.Interpolation() {
        public float apply (float t) {
            return t * t * t;
        }
    };


    public static final com.badlogic.gdx.math.Interpolation cubicOut = new com.badlogic.gdx.math.Interpolation() {
        public float apply (float t) {
            float f = t - 1.0f;
            return f * f * f + 1.0f;
        }
    };


    public static final com.badlogic.gdx.math.Interpolation cubicInOut = new com.badlogic.gdx.math.Interpolation() {
        @Override
        public float apply(float t) {
            return t < 0.5f
                    ? 4.0f * t * t * t
                    : 0.5f * (float)Math.pow(2.0f * t - 2.0f, 3.0f) + 1.0f;
        }
    };



    public static final com.badlogic.gdx.math.Interpolation backOut = new com.badlogic.gdx.math.Interpolation(){
        @Override
        public float apply(float t) {
            float f = 1.0f - t;
            return 1.0f - (float)(Math.pow(f, 1.3f) - f * MathUtils.sin(f * MathUtils.PI));
        }
    };


    public static final com.badlogic.gdx.math.Interpolation backIn = new com.badlogic.gdx.math.Interpolation() {
        @Override
        public float apply(float t) {
            return (float)Math.pow(t, 1.3f) - t * MathUtils.sin(t * MathUtils.PI);
        }
    };

}
