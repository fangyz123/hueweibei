package com.janborn.www;

public class Setting {
	public static boolean sounds = true;
	private static final String FILE_NAME = "AskMeWeather2.0.jar";
	private static final String ROOT = System.getProperty("user.dir") + "\\sounds\\send.wav";

	public Setting() {
		// TODO Auto-generated constructor stub
	}
	
	public void setSounds(boolean f) {
		sounds = f;
	}
	
	public boolean isSounds() {
		return sounds;
	}

}
