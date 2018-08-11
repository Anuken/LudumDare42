package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.entities.traits.LayerTrait.Layer;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.impl.BaseBulletType;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Mathf;

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
            speed = 2f;
            lifetime = 200f;
            hiteffect = Fx.tentahit;
            hiteffect = Fx.tentahit;
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Color.MAROON);
            Draw.alpha(0.5f);
            Fill.circle(b.x, b.y, 9f);
            Draw.alpha(1f);

            Fill.circle(b.x, b.y, 6);
            Draw.reset();
            Fill.circle(b.x, b.y, 3);
        }
    },

    tentacid = new BulletType(){
        {
            damage = 1f;
            speed = 1.9f;
            lifetime = 100f;
            hiteffect = Fx.tentahit;
            hiteffect = Fx.tentahit;
        }

        @Override
        public void init(Bullet b){
            super.init(b);
            b.getVelocity().scl(Mathf.random(0.5f, 1f));
            Effects.shake(4f, 4f, b);
        }

        @Override
        public void update(Bullet b){
            super.update(b);
            if(Mathf.chance(0.1 * Timers.delta())){
                Effects.effect(Fx.smoke, b);
            }
        }

        @Override
        public void despawned(Bullet b){
            super.despawned(b);

            for(int i = 0; i < 1; i++){
                Bullet a = new Bullet(acid, b.getOwner(), 0f);
                a.set(b.x + Mathf.range(5f), b.y + Mathf.range(5f));
                a.add();
            }
        }

        @Override
        public void hit(Bullet b){
            super.hit(b);
            despawned(b);
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Color.MAROON);
            Draw.alpha(0.5f);
            Fill.circle(b.x, b.y, 12f);
            Draw.alpha(1f);

            Fill.circle(b.x, b.y, 9);
            Draw.reset();
            Fill.circle(b.x, b.y, 4);
        }
    },

    acid = new BulletType(){
        {
            damage = 1;
            speed = 2f;
            lifetime = 999999999999999f;
            pierce = true;
            speed = 0f;
            layer = Layer.floor;
        }

        @Override
        public void update(Bullet b){
            super.update(b);
            if(Mathf.chance(0.01 * Timers.delta())){
                Effects.effect(Fx.smoke, b.x + Mathf.range(10f), b.y + Mathf.range(10f));
            }
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Color.MAROON);
            Fill.circle(b.x, b.y, 9f);
            Draw.alpha(1f);
            Draw.reset();
        }
    };

    public Layer layer = Layer.wall;
}
