package io.anuke.ld42.dialogs;

import io.anuke.ucore.scene.ui.SettingsDialog;

public class SettingsMenuDialog extends SettingsDialog{

    public SettingsMenuDialog(){
        //TODO auto-generated settings menu

        main.volumePrefs();
        main.screenshakePref();

        addCloseButton();
    }
}
