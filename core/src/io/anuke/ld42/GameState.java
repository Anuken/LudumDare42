package io.anuke.ld42;

public class GameState{
	private static State state = State.intro;
	
	public static boolean is(State other){
		return state == other;
	}
	
	public static void set(State other){
		state = other;
	}
	
	public enum State{
		playing, paused, intro;
	}
}
