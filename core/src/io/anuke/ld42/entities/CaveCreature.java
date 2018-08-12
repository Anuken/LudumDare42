package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

import java.util.Arrays;

import static io.anuke.ld42.Vars.control;
import static io.anuke.ld42.Vars.player;

public class CaveCreature extends Spark implements EnemyTrait{
    private int segments = 10;
    private int tentacles = 6;
    private float len = 6f;

    boolean phase2;

    float lerpto;
    float[][] values;
    float silenceTime;

    public CaveCreature(){
        hitbox.setSize(30f);
        height = 0f;
        initTentacles();

        heal();
    }

    void initTentacles(){
        values = new float[tentacles][segments];

        for(int i = 0; i < tentacles; i++){
            Arrays.fill(values[i], (float)i / tentacles * 360f);
        }
    }

    @Override
    public void onDeath(){
        remove();

        for(int i = 0; i < 4; i++){
            Timers.run(i*10, () -> Effects.effect(Fx.tentakill, x + Mathf.range(20f), y + Mathf.range(20f)));
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
    public float drawSize(){
        return 100;
    }

    @Override
    public String name(){
        return "cave-creature";
    }

    @Override
    public void update(){
        if(!phase2 && Timers.get(this, "shoot2", 220)){
            for(int i = 0; i < 4; i++){
                bullet(BulletType.tentacid, angleTo(player) + Mathf.range(20));
            }
        }

        if(phase2 && Mathf.chance(0.1 * Timers.delta())){
            Rock rock = new Rock();
            rock.set(x + Mathf.range(300f), y + Mathf.range(300f));
            rock.add();
            Effects.shake(3f, 10f, rock);
        }

        if(silenceTime > 0){
            silenceTime -= Timers.delta();
        }

        if(Timers.get(this, "lines", 250) && health < maxHealth()/2){
            silenceTime = 110;
            for(int i = 0; i < 4; i++){
                float angle = angleTo(player)+Mathf.range(20f);
                float s = Mathf.range(20f);
                for(int j = 0; j < 10; j++){
                    int f = j;
                    Timers.run(j * 12, () -> bullet(BulletType.tenta, angle + s*f));
                }
            }
        }

        if(health < maxHealth()/4 && !phase2){
            phase2 = true;
            health += 70;
            control.flash(Color.MAROON);
            Timers.run(0f, () -> {
                len = 7f;
                tentacles = 20;
                segments += 6f;
                initTentacles();
            });
        }

        lerpto = Mathf.slerp(lerpto, angleTo(player)+ 360f, 0.1f);
    }

    @Override
    public void draw(){

        for(int i = 0; i < tentacles; i++){
            boolean shoot = silenceTime <= 0 && Timers.get(this, "shoot-" + i, health < 40 ? 30 : 50 + i);

            float lastx = x, lasty = y;
            float base = i / (float)tentacles * 360f;
            //float len = CaveCreature.len + Mathf.randomSeed(i + 5, 0, 20);
            //float bthick = Mathf.randomSeed(i + 4, 0, 10)/4f;

            for(int j = 0; j < segments; j++){
                base = Mathf.slerp(base, lerpto, 0.1f);
                values[i][j] = Mathf.slerpDelta(values[i][j], base, 0.01f);

                float ang = values[i][j] + Mathf.sin(Timers.time() + i *59 + j/2f, 20f + Mathf.randomSeed(i) * 6 - j, 25f);

                float fract = 1f-(float)j/segments;
                Draw.color(Color.MAROON, Color.BLACK, 1f-fract);
                Tmp.v1.set(len, 0).rotate(ang);
                float newx = lastx + Tmp.v1.x, newy = lasty + Tmp.v1.y;
                Lines.stroke(fract * (9f));
                Lines.line(lastx, lasty, newx, newy);
                lastx = newx;
                lasty = newy;

                if(Mathf.chance(0.08 * Timers.delta() / tentacles)){
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
        return 300;
    }
}
