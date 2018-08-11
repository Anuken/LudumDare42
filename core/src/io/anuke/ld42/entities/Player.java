package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.graphics.Shaders;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Translator;

import static io.anuke.ld42.entities.Direction.*;

public class Player extends Spark{
    private Translator movement = new Translator();
    private float speed = 3f;

    public Player(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 8, 16, 16);
        heal();
    }

    @Override
    public void onDeath(){
        //ui.gameover.show();
        //control.reset();
    }

    @Override
    public float maxHealth(){
        return 5;
    }

    @Override
    public void draw(){
        Shaders.player.hittime = 0f;
        Shaders.player.color.set(Color.valueOf("ff4c4c"));
        Shaders.player.light.set(Color.valueOf("5f8689"));
        Shaders.player.dark.set(Color.valueOf("46596b"));
        Shaders.player.skin.set(Color.valueOf("fff0c6"));

        Graphics.shader(Shaders.player);

        Draw.grect("player-" + direction.texture +
        (walktime > 0 ? "-walk" + (1 + (int)(walktime / 10f) % 2) : ""), x, y, direction.flipped);

        Graphics.shader();
    }

    @Override
    public void update(){
        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed);
        if(!movement.isZero()){
            float angle = movement.angle();
            direction = (angle < 45 || angle > 315 ? right : angle >= 45 && angle < 135 ? back : angle >= 135 && angle < 225 ? left : front);;
            walktime += Timers.delta();
        }else{
            walktime = 0;
        }
        move(movement.x, movement.y);

        if(Inputs.keyDown("shoot") && Timers.get(this, "shoot", 10)){
            bullet(BulletType.testType, Angles.mouseAngle(x, y + height));
        }
    }
}
