package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import io.anuke.ld42.graphics.Palette;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ld42.Vars.control;
import static io.anuke.ld42.Vars.tileSize;

public class Artifact extends Enemy{
    static float shootdur = 15f;
    static int pillarid = 16;

    float shootTime;
    float ang = 0f;
    boolean diag;

    {
        height = 38;
        hitbox.set(0, 20, 20, 40);
    }

    @Override
    public void onDeath(){
        super.onDeath();

        Effects.sound("blobdie", this);
    }

    @Override
    public void behavior(){
        if(shootTime > 0){
            shootTime -= Timers.delta();
        }

        if(shootTime < 0) shootTime = 0;

        if(healthf() > 0.8f){
            if(Timers.get(this, "shotgun", 35)){
                ang += 60f;
                Angles.circle(3, f -> Angles.shotgun(6, 10f, f + ang, a -> bullet(BulletType.artifact, a)));
            }
        }else if(healthf() > 0.6){
            if(Timers.get(this, "spiral", 6)){
                ang += 9f;
                Angles.circle(3, f -> bullet(BulletType.artifact, f + ang));
            }

            if(Timers.get(this, "spiral", 20)){
                ang += 9f;
                Angles.circle(3, f -> bullet(BulletType.artifact, f + ang));
            }
        }else if(healthf() > 0.4){
            if(Timers.get(this, "blade", 100)){
                for(int i = 0; i < 30; i++){
                    ang += 15f;
                    float fa = ang;
                    Timers.run(i * 3, () -> bullet(BulletType.artifact, fa));
                }
            }

            if(Timers.get(this, "spiral", 60)){
                for(int i = 0; i < 6; i++){
                    int fi = i;
                    Timers.run(i * 10, () -> Angles.circle(3, f -> bullet(BulletType.artifact, f + fi*60)));
                }
            }
        }else if(healthf() > 0.25){
            ang += Mathf.sin(Timers.time(), 13f, 3f);
            if(Timers.get(this, "spiral", 5)){
                Angles.circle(5, f -> bullet(BulletType.artifact, f + ang));
            }
        }else{
            if(Timers.get(this, "rotate", 210)){
                diag = !diag;
            }

            if(Timers.getTime(this, "rotate") < 185){
                setPillars(pillarid);
            }else{
                setPillars(0);
            }

            if(Timers.getTime(this, "rotate") < 100 && Timers.get(this, "shotgun", 8)){
                ang += 29f;
                Angles.circle(3, f -> Angles.shotgun(6, 10f, f + ang, a -> bullet(BulletType.artifact, a)));
            }
        }
    }

    void setPillars(int id){
        setPillars(id, true);
    }

    void setPillars(int id, boolean flip){
        int px = (int)(x / tileSize);
        int py = (int)(y / tileSize);
        int rad = 6;
        for(int i = 0; i < 4; i++){

            GridPoint2 p = Geometry.d8edge[i];
            int cx = px + p.x * rad, cy = py + p.y * rad;
            control.wall(cx, cy, ((i % 2) == 0 != diag && flip ? 0 : id));
        }
    }

    @Override
    public void removed(){
        super.removed();
        setPillars(pillarid, false);
    }

    @Override
    public int shadowSize(){
        return 24;
    }

    @Override
    public int shadowOffsetX(){
        return -1;
    }

    @Override
    public Color lightColor(){
        return Palette.artifact;
    }

    @Override
    public float lightRadius(){
        return 300f + shootTime/shootdur*100f;
    }

    @Override
    public void draw(){
        Draw.grect("artifact", x, y);

        float f = Mathf.absin(Timers.time(), 8f, 4f);
        float rad = shootTime/shootdur*7;

        Draw.color(Palette.artifact, Color.WHITE, shootTime/shootdur);
        Draw.alpha(0.5f);
        Fill.circle(x-1, y + height + f, 6f + rad);
        Fill.circle(x-1, y + height + f, 4f + rad);
        Draw.color(Color.WHITE);
        Fill.circle(x-1, y + height + f, 2f + rad);
    }

    @Override
    public float maxHealth(){
        return 300;
    }

    @Override
    public void bullet(BulletType type, float angle){
        super.bullet(type, angle);
        shootTime = shootdur;
    }
}
