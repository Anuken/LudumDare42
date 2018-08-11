package io.anuke.ld42.dialogs;

import io.anuke.ucore.scene.ui.Dialog;
import io.anuke.ld42.GameState;
import io.anuke.ld42.GameState.State;
import static io.anuke.ld42.Vars.*;

public class PausedDialog extends Dialog{

    public PausedDialog(){
        super("Paused");

        content().defaults().width(200);

        content().addButton("Resume", () -> {
            GameState.set(State.playing);
            ui.paused.hide();
        });
        content().row();

        content().addButton("Settings", ui.settings::show);
        content().row();

        content().addButton("Controls", ui.keybind::show);
        content().row();

        content().addButton("Back to Menu", () -> {
            hide();
            GameState.set(State.menu);
        });
    }
}
