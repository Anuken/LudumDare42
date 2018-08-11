package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import io.anuke.ld42.ui.*;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.Skin;
import io.anuke.ucore.scene.ui.KeybindDialog;

public class UI extends SceneModule{
    public KeybindDialog keybind;
    public SettingsMenuDialog settings;
    public AboutDialog about;
    public TutorialDialog tutorial;
    public PausedDialog paused;
    public GameOverDialog gameover;
    public DialogBox dialog;

    protected void loadSkin(){
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), Core.atlas);
    }

    @Override
    public void init(){
        Core.font.setUseIntegerPositions(true);

        keybind = new KeybindDialog();
        settings = new SettingsMenuDialog();
        about = new AboutDialog();
        tutorial = new TutorialDialog();
        paused = new PausedDialog();
        gameover = new GameOverDialog();
        dialog = new DialogBox();
    }

}
