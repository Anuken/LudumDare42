package io.anuke.ld42.ui;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.GameState;
import io.anuke.ld42.GameState.State;
import io.anuke.ucore.core.Musics;
import io.anuke.ucore.util.Mathf;

import static io.anuke.ucore.core.Core.scene;

public class Intro{
    private static float fadeOut = 250f;
    private static float fadeIn = 900f;
    private static String[] text = {"Wisdom through insight", "Strength through clarity", "Life through magic"};

    public float fadeInTime = -1f;

    public Intro(){
        if(GameState.is(State.intro)){
            Musics.playTracks("intro");
        }

        scene.table("white", t -> {
            t.setColor(Color.BLACK);
            t.getColor().a = 1f;

            t.update(() -> {
                t.getColor().a = Mathf.clamp(1f-fadeInTime);
                if(fadeInTime > 0){
                    fadeInTime += 1f/fadeOut;
                }else{
                    fadeInTime += 1f/fadeIn;
                }

                if(fadeInTime > 1f){
                    GameState.set(State.playing);
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
        });
    }
}
