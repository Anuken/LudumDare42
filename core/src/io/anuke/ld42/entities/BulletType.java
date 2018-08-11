package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.entities.impl.BaseBulletType;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Lines;

public class BulletType extends BaseBulletType<Bullet>{
    public static final BulletType

    testType = new BulletType(){
        {
            damage = 1;
            speed = 9f;
            hiteffect = Fx.explosion;
        }

        @Override
        public void draw(Bullet b){
            Draw.color(Color.PURPLE);
            Lines.poly(b.x, b.y, 3, 10);
            Draw.color();
        }
    },

    tenta = new BulletType(){
        {
            damage = 1;
            speed = 3f;
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Color.MAROON);
            Lines.circle(b.x, b.y, 3);
            Draw.reset();
        }
    };
}
