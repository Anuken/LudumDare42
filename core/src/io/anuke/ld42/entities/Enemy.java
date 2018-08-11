package io.anuke.ld42.entities;

import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ld42.Vars.player;

public class Enemy extends Spark{

    public Enemy(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 8, 16, 16);
    }

    @Override
    public String name(){
        return "not a spark really";
    }

    @Override
    public void update(){
        if(Mathf.chance(0.05)){
           bullet(BulletType.testType, angleTo(player));
        }
    }

    @Override
    public void draw(){
        Draw.grect("enemy", x, y);
    }

    @Override
    public float maxHealth(){
        return 100;
    }
}
