package io.anuke.ld42;

import io.anuke.ld42.entities.Player;
import io.anuke.ld42.entities.traits.EnemyTrait;

public class Vars{
	public static Control control;
	public static UI ui;

	public static Player player;
	public static EnemyTrait enemy;

	public static boolean debug = true;

	public static final int tileSize = 16;
	
	public static String[] tutorialText = {
		"Tutorial line 1",
		"Tutorial line 2"
	};
	
	public static String[] aboutText = {
		"Made by [crimson]Anuke[] for something.",
		"",
		"Tools used:",
		"- Some [lime]amazing[] tool #1",
		"- Another [royal]cool[] tool #2",
		"- Something [yellow]else[] #3"
	};
}
