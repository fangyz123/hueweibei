package com.janborn.www.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	
	public static final String file = System.getProperty("user.dir")+"\\sounds\\send.wav";
	private AudioFormat audioFormat = null; 
	private SourceDataLine sourceDataLine = null; 
	private DataLine.Info dataLine_info = null; 
	private AudioInputStream audioInputStream = null; 

	public SoundPlayer() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(file));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		audioFormat = audioInputStream.getFormat(); 
		dataLine_info = new DataLine.Info(SourceDataLine.class,audioFormat); 
		try {
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine_info);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play() { 
		byte[] b = new byte[1024]; 
		int len = 0; 
		try {
			sourceDataLine.open(audioFormat, 1024);
			sourceDataLine.start(); 
			while ((len = audioInputStream.read(b)) > 0){ 
			sourceDataLine.write(b, 0, len); 
			} 
			audioInputStream.close(); 
			sourceDataLine.drain(); 
			sourceDataLine.close(); 
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	} 

}
