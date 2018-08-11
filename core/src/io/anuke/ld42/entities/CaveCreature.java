package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.Vars;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class CaveCreature extends Spark{

    public CaveCreature(){
        hitboxTile.set(0, 3, 12, 6);
        hitbox.set(0, 8, 16, 16);
        height = 0f;
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
        if(Timers.get(this, "shoot", 10)){
            bullet(BulletType.testType, angleTo(Vars.player));
        }
    }

    @Override
    public void draw(){

        int segments = 10;
        int tentacles = 6;
        float len = 6f;

        for(int i = 0; i < tentacles; i++){
            float lastx = x, lasty = y;
            for(int j = 0; j < segments; j++){

                float ang = i/(float)tentacles * 360f + Mathf.sin(Timers.time() + i *59 + j/2f, 20f + Mathf.randomSeed(i) * 6 - j, 25f);
                float fract = 1f-(float)j/segments;
                Draw.color(Color.MAROON, Color.BLACK, 1f-fract);
                Tmp.v1.set(len, 0).rotate(ang);
                float newx = lastx + Tmp.v1.x, newy = lasty + Tmp.v1.y;
                Lines.stroke(fract * 9f);
                Lines.line(lastx, lasty, newx, newy);
                lastx = newx;
                lasty = newy;
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
