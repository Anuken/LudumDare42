package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.entities.traits.LayerTrait.Layer;
import io.anuke.ld42.graphics.Palette;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.entities.impl.BaseBulletType;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Mathf;

public class BulletType extends BaseBulletType<Bullet>{
    public static final BulletType

    playerBullet = new BulletType(){
        {
            damage = 1;
            speed = 9f;
            hiteffect = Fx.playerhit;
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Palette.eikan);
            Draw.alpha(0.5f);
            Fill.circle(b.x, b.y, 6f);
            Draw.alpha(1f);

            Fill.circle(b.x, b.y, 4);
            Draw.reset();
            Fill.circle(b.x, b.y, 2);
        }
    },

    aysaBullet = new BulletType(){
        {
            damage = 1;
            speed = 9f;
            hiteffect = Fx.aysahit;
        }

        @Override
        public void draw(Bullet b){
            Lines.stroke(3f);
            Draw.color(Palette.aysa);
            Draw.alpha(0.5f);
            Fill.circle(b.x, b.y, 6f);
            Draw.alpha(1f);

            Fill.circle(b.x, b.y, 4);
            Draw.reset();
            Fill.circle(b.x, b.y, 2);
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
            speed = 4f;
            lifetime = 100f;
            drag = 0.02f;
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

            Bullet a = new Bullet(acid, b.getOwner(), 0f);
            a.set(b.x + Mathf.range(5f), b.y + Mathf.range(5f));
            a.add();

            for(int i = 0; i < 3; i++){
                Effects.effect(Fx.tentahit, b.x + Mathf.range(4f), b.y + Mathf.range(4f));
            }
        }

        @Override
        public void hit(Bullet b){
            super.hit(b);
            despawned(b);
        }

        @Override
        public void draw(Bullet b){
            float s = 0.5f + b.fslope()*0.5f;

            Draw.color(Color.MAROON);
            Draw.alpha(0.5f);
            Fill.circle(b.x, b.y, 12f*s);
            Draw.alpha(1f);

            Fill.circle(b.x, b.y, 9*s);
            Draw.reset();
            Fill.circle(b.x, b.y, 4*s);
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
