package io.anuke.ld42.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.GameState;
import io.anuke.ld42.GameState.State;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Musics;
import io.anuke.ucore.input.Input;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ucore.core.Core.scene;

public class Intro{
    private static float fadeOut = 250f;
    private static float fadeIn = 1000f;
    private static String[] text = {"Wisdom through insight", "Strength through clarity", "Life through magic"};

    public float fadeInTime = -1f;
    public float particleFadeTime;
    public float time;
    public boolean begin;
    public float btime = 1f;

    public Intro(){

        scene.table(t -> {
            t.setColor(Color.BLACK);
            t.getColor().a = 1f;

            t.update(() -> {
                if(!begin){
                    if(Inputs.keyTap(Input.SPACE)){
                        Musics.playTracks("intro");
                        begin = true;
                    }
                    return;
                }

                if(Inputs.keyTap(Input.SPACE)){
                    fadeInTime = 1f;
                    particleFadeTime = 0f;
                }

                btime = Mathf.lerp(btime, 0f, 0.04f);

                t.getColor().a = Mathf.clamp(1f-fadeInTime);
                if(fadeInTime > 0){
                    particleFadeTime = Mathf.lerp(particleFadeTime, 0f, 0.01f);
                    fadeInTime += 1f/fadeOut;
                }else{
                    particleFadeTime = Mathf.lerp(particleFadeTime, 1f, 0.005f);
                    fadeInTime += 1f/fadeIn;
                }

                time += Gdx.graphics.getDeltaTime()*60f;

                if(fadeInTime > 1f){
                    GameState.set(State.playing);
                    //Musics.fadeOut();
                }
            });

            t.table(nest -> {
                for(int i = 0; i < text.length; i++){
                    float f = (float)i/text.length + 0.1f;
                    nest.add(text[i]).update(l -> {
                        l.getColor().a = Mathf.clamp(Mathf.clamp(1f-(-fadeInTime) - f)*10f);
                    });
                    nest.row();
                }
            });
        }).visible(() -> GameState.is(State.intro));

        scene.table(t -> {
            t.add("[space] to begin.");
            t.update(() -> t.getColor().a = btime);
        }).visible(() -> GameState.is(State.intro));
    }
}
