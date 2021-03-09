package word.game.ui.dial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;


public class ParticleEmitter {


    private ParticleSettings settings;
    private Array<Particle> particles = new Array<>();
    private Actor dial;
    private float maxDst;
    private int currentStage = 1;



    public ParticleEmitter(Actor dial, ParticleSettings particleSettings, int currentStage){
        settings = particleSettings;
        this.dial = dial;
        this.currentStage = currentStage;
        maxDst = dial.getOriginX() * 0.7f;
    }



    public void setStage(int stage){
        currentStage = stage;
    }



    private void createParticle(){
        TextureRegion region = null;
        Particle.ParticleType type = Particle.ParticleType.DISC;

        float originX = 0.0f;
        float originY = 0.5f;

        if(currentStage == 2 || currentStage == 5 || currentStage == 8 || currentStage == 11 || currentStage == 14) {
            region = AtlasRegions.discParticle;
        }else if(currentStage == 3 || currentStage == 6 || currentStage == 9 || currentStage == 12 || currentStage == 15) {
            region = AtlasRegions.star_particle;
            type = Particle.ParticleType.STAR;
        }else if(currentStage == 4 || currentStage == 7 || currentStage == 10 || currentStage == 13 || currentStage == 16) {
            region = AtlasRegions.stripe_particle;
            type = Particle.ParticleType.STRIPE;
        }else{
            region = AtlasRegions.discParticle;
        }

        Particle particle = new Particle(region);
        particle.setOrigin(particle.getWidth() * originX, particle.getHeight() * originY);

        particle.type = type;
        particle.stage = currentStage;
        reset(particle);
        particles.add(particle);
    }




    private void reset(Particle particle){
        float TWO_PI = 6.283185f;
        float angle = MathUtils.random() * TWO_PI;
        float cos = MathUtils.cos(angle);
        float sin = MathUtils.sin(angle);

        particle.angle = angle * MathUtils.radiansToDegrees;
        particle.x = particle.startX = (dial.getX() + dial.getOriginX()) + (cos * dial.getOriginX());
        particle.y = particle.startY = (dial.getY() + dial.getOriginY()) + (sin * dial.getOriginY());
        particle.radius = MathUtils.random(0.4f, 1.0f);
        particle.rotation = MathUtils.random() * 360.0f;
        particle.friction = settings.friction;
        particle.dst = 0;

        float speed = MathUtils.random(settings.speedMin, settings.speedMax);
        particle.vx = cos * speed;
        particle.vy = sin * speed;

        if(particle.type == Particle.ParticleType.DISC) {
            if (MathUtils.randomBoolean())
                particle.setColor(UIConfig.DIAL_ROUND_PARTICLE_COLOR);
            else
                particle.setColor(UIConfig.DIAL_PARTICLE_ALTERNATE_COLOR);
        }

        if(particle.type == Particle.ParticleType.STAR){
            if(MathUtils.randomBoolean())
                particle.setColor(UIConfig.DIAL_STAR_PARTICLE_COLOR);
            else
                particle.setColor(UIConfig.DIAL_PARTICLE_ALTERNATE_COLOR);
        }

        if(particle.type == Particle.ParticleType.STRIPE){
            if(MathUtils.randomBoolean())
                particle.setColor(UIConfig.DIAL_STRIP_PARTICLE_COLOR);
            else
                particle.setColor(Color.WHITE);
        }
    }





    public void update(Batch batch){
        if(currentStage == 1)
            return;

        if(particles.size < settings.count){
            createParticle();
        }

        for(int i = 0; i < particles.size; i++){
            Particle p = particles.get(i);

            if(p.stage > currentStage)
                continue;

            p.x += p.vx;
            p.y += p.vy;
            p.dst = (float)Math.sqrt((p.x - p.startX) * (p.x - p.startX) + (p.y - p.startY) * (p.y - p.startY));
            p.speed *= p.friction;
            p.radius *= 0.99f;
            p.setPosition(p.x, p.y);
            p.setScale(p.radius);
            p.setRotation(p.angle);
            p.setAlpha(batch.getColor().a);
            p.draw(batch);

            if(p.dst > maxDst){
                reset(p);
            }
        }
    }


}
