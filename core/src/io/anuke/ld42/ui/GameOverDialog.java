package io.anuke.ld42.ui;

import com.badlogic.gdx.Gdx;
import io.anuke.ucore.scene.ui.Dialog;

public class GameOverDialog extends Dialog{

    public GameOverDialog(){
        super("[ORANGE]Congratulations!", "dialog");
        content().add("You've beaten the game!").pad(10);
        buttons().addButton("Exit", () -> {
            hide();
            Gdx.app.exit();
        }).size(200, 60).pad(10);
    }
}
