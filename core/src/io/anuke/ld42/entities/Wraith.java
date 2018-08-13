package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.graphics.Palette;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ld42.Vars.control;
import static io.anuke.ld42.Vars.player;

public class Wraith extends Enemy{
    boolean phase2 = false;

    {
        hitbox.set(0, 8, 16, 16);
    }

    @Override
    public Color lightColor(){
        return Palette.wraith;
    }

    @Override
    public void behavior(){

        if(phase2){
            if(Mathf.chance(0.15 * Timers.delta())){
                Effects.effect(Fx.msmoke, x + Mathf.range(8f), y + 10f + Mathf.range(10f));
            }

            if(Timers.get(this, "phase2shoot", 70)){
                float ang = angleTo(player);
                for(int i : Mathf.signs){
                    for(int j = 0; j < 7; j++){
                        int fj = j;
                        Timers.run(j*4, () -> bullet(BulletType.wraith2, ang + i*140f - i*fj*20));
                    }
                }
            }
        }else if(health < 5){
            phase2 = true;
            health = maxHealth();
            control.flash(Palette.wraith);
        }else if(healthf() < 0.5f){
            if(Timers.get(this, "circle", 200)){
                Angles.circle(10, f -> {
                    for(int i = 0; i < 4; i++){
                        Timers.run(i*6, () -> bullet(BulletType.wraith, f));
                    }
                });
            }

            if(Timers.get(this, "blast", 130)){
                float ang = angleTo(player);
                for(int i : Mathf.signs){
                    for(int j = 0; j < 5; j++){
                        int fj = j;
                        Timers.run(j*5, () -> bullet(BulletType.wraith, ang + i*50f - i*fj*10));
                    }
                }
            }
        }else{
            if(Timers.get(this, "circle", 200)){
                Angles.circle(10, f -> bullet(BulletType.wraith, f));
            }
        }

        float speed = phase2 ? 1f : (healthf() < 0.5f ? 0.3f : 0.6f);

        movement.set(player.x - x, player.y - y).setLength(speed).scl(Timers.delta());
        direction = movement.x < 0;
        move(movement.x, movement.y);
    }

    @Override
    public void draw(){
        float offset = Mathf.absin(Timers.time(), 10f, 4f);
        if(phase2){
            Draw.colorl(0.1f);
            Draw.alpha(0.4f);
            Fill.circle(x, y + offset + 10, 10f);
            Lines.stroke(2f);
            Angles.circleVectors(10, 16f, Timers.time()*2f, (nx, ny) -> {
                Fill.circle(x + nx, y + ny + 10, 4f);
            });
            Draw.reset();
        }
        Draw.grect(phase2 ? "wraith2" : "wraith", x, y + offset, direction);
    }

    @Override
    public float maxHealth(){
        return phase2 ? 60 : 90;
    }
}
