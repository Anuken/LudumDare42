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
    }

    @Override
    public float maxHealth(){
        return 5;
    }

    @Override
    public void draw(){
        Draw.grect("char", x, y);
    }

    @Override
    public void update(){
        movement.set(Inputs.getAxis("move_x") * speed, Inputs.getAxis("move_y") * speed).limit(speed);
        move(movement.x, movement.y);

        if(Inputs.keyDown("shoot") && Timers.get(this, "shoot", 10)){
            Bullet bullet = new Bullet(BulletType.testType, this, Angles.mouseAngle(x, y));
            bullet.set(x, y);
            bullet.add();
        }
    }
}
