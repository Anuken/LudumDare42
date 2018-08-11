package io.anuke.ld42.entities;

import io.anuke.ucore.entities.impl.BaseBulletType;
import io.anuke.ucore.graphics.Fill;

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
            Fill.square(b.x, b.y, 4);
        }
    };
}
