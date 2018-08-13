package io.anuke.ld42.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ld42.graphics.Palette;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.graphics.Fill;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;

public class Fx{
	public static final Effect

	explosion = new Effect(10, e -> {
		Draw.color(Color.YELLOW);
		Fill.circle(e.x, e.y, 20 * e.fin());
		Draw.reset();
	}),

	smoke = new Effect(50, e -> {
		Draw.color(Color.MAROON, Color.BLACK, e.fin());
		Fill.circle(e.x, e.y, 4 * e.fslope());
		Draw.reset();
	}),

	msmoke = new Effect(50, e -> {
		Draw.color(Color.DARK_GRAY, Color.BLACK, e.fin());
		Fill.circle(e.x, e.y, 4 * e.fslope());
		Draw.reset();
	}),

	tentahit = new Effect(20, e -> {
		Draw.color(Color.MAROON, Color.BLACK, e.fout());
		Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*4f);
		});
		Draw.reset();
	}),

	artihit = new Effect(20, e -> {
		Draw.color(Palette.artifact);
		Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*4f);
		});
		Draw.reset();
	}),

	pillarup = new Effect(10, e -> {
		Draw.color(Color.WHITE);
		Lines.stroke( 2f * e.fout());
		Lines.circle(e.x, e.y, 30f * e.fin());
		Draw.reset();
	}),

	pillardown = new Effect(10, e -> {
		Draw.color(Color.WHITE);
		Lines.stroke( 2f * e.fin());
		Lines.circle(e.x, e.y, 20f * e.fout());
		Draw.reset();
	}),

	tentakill = new Effect(50, e -> {
		Draw.color(Color.MAROON, Color.BLACK, e.fout());
		Angles.randLenVectors(e.id, 20, e.fin() * 100f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*9f);
		});
		Draw.reset();
	}),

	playershoot = new Effect(10, e -> {
		Draw.color(Color.WHITE, Palette.eikan, e.fin());
		Lines.stroke(2f * e.fout());
		Lines.circle(e.x, e.y, 8 * e.fin());
		Draw.reset();
	}),

	playerhit = new Effect(20, e -> {
		Draw.color(Color.WHITE, Palette.eikan, e.fout());
		Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*4f);
		});
		Draw.reset();
	}),

	aysahit = new Effect(20, e -> {
		Draw.color(Color.WHITE, Palette.aysa, e.fout());
		Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
			Fill.circle(x + e.x, y + e.y, e.fout()*4f);
		});
		Draw.reset();
	}),

	hit = new Effect(10, e -> {
		Lines.stroke(3f);
		Draw.color(Color.WHITE, Color.ORANGE, e.fin());
		Lines.spikes(e.x, e.y, 5 + e.fin() * 40f, 10, 8);
		Draw.reset();
	});
	
}
