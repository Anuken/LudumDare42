package io.anuke.ld42.entities;

import io.anuke.ld42.Vars;
import io.anuke.ld42.entities.traits.ShadowTrait;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.impl.BaseEntity;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.graphics.Draw;

public class Rock extends BaseEntity implements DrawTrait, ShadowTrait{
    static float maxHeight = 200f;
    float height = maxHeight;

    @Override
    public void update(){
        height -= Timers.delta() * 3;
        if(height < 0){
            Bullet b = new Bullet(BulletType.rock, Vars.enemy, 0f);
            b.set(x, y);
            b.add();
            remove();
        }
    }

    @Override
    public void draw(){
        Draw.alpha(1f-height/maxHeight);
        Draw.rect("rock", x, y + height);
        Draw.color();
    }

    @Override
    public int shadowSize(){
        return (2+(int)((1f - height/maxHeight)*7))*2;
    }

    @Override
    public int shadowOffsetX(){
        return 0;
    }

    @Override
    public int shadowOffsetY(){
        return 0;
    }

    @Override
    public float drawSize(){
        return 100;
    }
}
