package io.anuke.ld42.ui;

import io.anuke.ld42.Vars;
import io.anuke.ucore.scene.ui.Dialog;

public class GameOverDialog extends Dialog{

    public GameOverDialog(){
        super("Game Over", "dialog");
        content().add("You're dead.");
        buttons().addButton("Retry", () -> {
            Vars.control.reset();
            hide();
        }).size(200, 60);
    }
}
