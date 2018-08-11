package io.anuke.ld42.entities;

import io.anuke.ucore.core.Timers;
import io.anuke.ucore.util.Mathf;

public class Aysa extends Spark{
    float dir;

    @Override
    public void update(){
        dir += Mathf.range(20f);
        movement.set(10f, 1f).setLength(0.5f).setAngle(dir);
        direction = movement.x > 0;
        walktime += Timers.delta();
        move(movement.x, movement.y);
    }

    @Override
    public String name(){
        return "aysa";
    }

    @Override
    public float maxHealth(){
        return 10;
    }
}
