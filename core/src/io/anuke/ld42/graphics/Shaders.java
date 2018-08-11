package io.anuke.ld42.graphics;

import com.badlogic.gdx.graphics.Color;
import io.anuke.ucore.graphics.Shader;

public class Shaders{
    public static final PlayerShader player = new PlayerShader();

    public static class PlayerShader extends Shader{
        public Color color = new Color();
        public Color light = new Color();
        public Color dark = new Color();
        public Color skin = new Color();
        public float hittime = 0f;

        public PlayerShader(){
            super("player", "default");
        }

        @Override
        public void apply(){
            shader.setUniformf("u_hitcolor", color);
            shader.setUniformf("u_hittime", hittime);
            shader.setUniformf("u_light", light);
            shader.setUniformf("u_dark", dark);
            shader.setUniformf("u_skin", skin);
        }
    }
}


