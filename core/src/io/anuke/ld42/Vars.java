package io.anuke.ld42;

import io.anuke.ld42.entities.Aysa;
import io.anuke.ld42.entities.Player;
import io.anuke.ld42.entities.traits.EnemyTrait;

public class Vars{
	public static Control control;
	public static UI ui;

	public static Player player;
	public static Aysa aysa;
	public static EnemyTrait enemy;

	public static boolean debug = false;
	public static boolean drawLights = true;

	public static final int tileSize = 16;
	public static final int raynum = 15;
	public static final float shadowOpacity = 0.3f;
	
	public static String[] tutorialText = {
		"Tutorial line 1",
		"Tutorial line 2"
	};
	
	public static String[] aboutText = {
		"Made by [sky]Anuke[], [lime]Epowerj[] and [crimson]Tux[] for Ludum Dare 42.",
		"",
		"Resources used:",
		"- Prose font, made by Raus",
		"- LibGDX game framework",
		"- Jukedeck.com for music",
		"- Sound effects downloaded from some site I can't find anymore\n(whups)"
	};
}
