package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.Vars;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

import java.util.Arrays;

public class CaveCreature extends Spark{
    private static final int segments = 10;
    private static final int tentacles = 6;
    private static final float len = 6f;

    float lerpto;
    float[][] values;

    public CaveCreature(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 8, 16, 16);
        height = 0f;

        values = new float[tentacles][segments];

        for(int i = 0; i < tentacles; i++){
            Arrays.fill(values[i], i / tentacles * 360f);
        }
    }

    @Override
    public Layer getLayer(){
        return Layer.wall;
    }

    @Override
    public int shadowSize(){
        return 20;
    }

    @Override
    public String name(){
        return "cave-creature";
    }

    @Override
    public void update(){
        if(Timers.get(this, "shoot2", 240)){
            bullet(BulletType.tentacid, angleTo(Vars.player));
        }

        lerpto = Mathf.slerp(lerpto, angleTo(Vars.player)+ 360f, 0.1f);
    }

    @Override
    public void draw(){
        boolean shoot = Timers.get(this, "shoot", 30);

        for(int i = 0; i < tentacles; i++){

            float lastx = x, lasty = y;
            float base = i / (float)tentacles * 360f;
            for(int j = 0; j < segments; j++){
                base = Mathf.slerp(base, lerpto, 0.1f);
                values[i][j] = Mathf.slerpDelta(values[i][j], base, 0.01f);

                float ang = values[i][j] + Mathf.sin(Timers.time() + i *59 + j/2f, 20f + Mathf.randomSeed(i) * 6 - j, 25f);


                float fract = 1f-(float)j/segments;
                Draw.color(Color.MAROON, Color.BLACK, 1f-fract);
                Tmp.v1.set(len, 0).rotate(ang);
                float newx = lastx + Tmp.v1.x, newy = lasty + Tmp.v1.y;
                Lines.stroke(fract * 9f);
                Lines.line(lastx, lasty, newx, newy);
                lastx = newx;
                lasty = newy;

                if(Mathf.chance(0.01 * Timers.delta())){
                    Effects.effect(Fx.smoke, newx + Mathf.range(len), newy + Mathf.range(len));
                }

                if(shoot && j == segments - 1){
                    bullet(BulletType.tenta, x, y, values[i][segments-1]);
                }
            }
        }

        Draw.reset();

        float sz = 15f - Mathf.absin(Timers.time(), 5f, 5f);

        Draw.color(Color.DARK_GRAY);
        Fill.circle(x, y, sz/2f);
        Draw.color(Color.MAROON);
        Fill.circle(x, y, sz/4f);
        Draw.color();
        Draw.rect("cave-creature-teeth", x, y, sz, sz, Mathf.absin(Timers.time(), 40f, 100f));
    }

    @Override
    public float maxHealth(){
        return 100;
    }
}
