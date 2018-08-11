package io.anuke.ld42.entities;

import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Translator;

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
        Draw.grect("char" +
        (walktime > 0 ? "-walk-" + ( (int)(walktime / 10f) % 2) : ""), x, y, !direction);
    }

    @Override
    public void update(){
        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed);
        if(!movement.isZero()){
            direction = (Math.abs(movement.x) > 0 ? movement.x > 0 : movement.y > 0);
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
