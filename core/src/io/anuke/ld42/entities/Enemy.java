package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.Vars;
import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.impl.EffectEntity;
import io.anuke.ucore.entities.trait.Entity;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.lights.PointLight;

import static io.anuke.ld42.Vars.control;
import static io.anuke.ld42.Vars.player;

public abstract class Enemy extends Spark implements EnemyTrait{
    public boolean active;
    public PointLight light;

    {
        heal();
    }

    public Color lightColor(){
        return Color.WHITE;
    }

    public float lightRadius(){
        return 140f;
    }

    public abstract void behavior();

    @Override
    public void update(){
        light.setPosition(x, y);
        if(light.getDistance() != lightRadius())light.setDistance(lightRadius());
        if(active) behavior();
    }

    @Override
    public void added(){
        light = new PointLight(control.rays, Vars.raynum, lightColor(), lightRadius(), x, y);
    }

    @Override
    public String name(){
        return "invalid";
    }

    @Override
    public void onDeath(){
        remove();
        control.flash(Color.WHITE);
        player.heal();
        control.displayDeath(this);

        Timers.run(1f, () -> {
            for(Entity entity : Entities.all()){
                if(entity instanceof Bullet || entity instanceof EffectEntity){
                    entity.remove();
                }
            }
        });
    }

    @Override
    public boolean isActive(){
        return active;
    }

    @Override
    public boolean collides(SolidTrait other){
        return active && super.collides(other);
    }

    @Override
    public void setActive(boolean active){
        this.active = active;
    }
}
