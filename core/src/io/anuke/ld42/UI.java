package io.anuke.ld42;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import io.anuke.ld42.GameState.State;
import io.anuke.ld42.ui.*;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.Skin;
import io.anuke.ucore.scene.ui.KeybindDialog;

import static io.anuke.ld42.Vars.control;

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

        scene.table(menu -> {
            menu.defaults().width(200f);

            menu.addButton("Play", () -> {
               control.reset();
               GameState.set(State.playing);
            });

            menu.row();
            menu.addButton("Settings", settings::show);
            menu.row();
            menu.addButton("Controls", keybind::show);
            menu.row();
            menu.addButton("About", about::show);
            menu.row();

            if(Gdx.app.getType() == ApplicationType.Desktop){
                menu.addButton("Exit", Gdx.app::exit);
            }
        }).visible(() -> GameState.is(State.menu));

        scene.table(header -> header.top().add("LD42").get().setFontScale(2)).visible(() -> GameState.is(State.menu));
    }

}
