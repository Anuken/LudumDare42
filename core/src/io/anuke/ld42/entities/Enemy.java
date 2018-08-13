package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.Vars;
import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.lights.Light;
import io.anuke.ucore.lights.PointLight;

import static io.anuke.ld42.Vars.control;

public abstract class Enemy extends Spark implements EnemyTrait{
    public boolean active;
    public Light light;

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
