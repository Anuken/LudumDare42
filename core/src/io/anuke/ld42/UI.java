package io.anuke.ld42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import io.anuke.ld42.ui.*;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.Skin;
import io.anuke.ucore.scene.ui.KeybindDialog;

import static io.anuke.ld42.Vars.player;

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

        scene.table(hud -> {
            float wh = 40, hh = 30;
            float wspace = 20;
            hud.top().left().table("button", t -> {
                t.margin(10);
                t.top().left().addRect((x, y, w, h) -> {
                    int health = (int)player.health;
                    for(int i = 0; i < player.maxHealth(); i++){
                        Draw.color(health <= i ? Color.BLACK : Color.SCARLET);
                        Fill.crect(x + (i*(wh + wspace)), y, wh, hh);
                    }
                    Draw.color();
                }).size((wh + wspace)*5 - wspace, hh);
            });
        });
    }

}
