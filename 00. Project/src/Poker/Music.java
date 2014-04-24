package Poker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


public abstract class Music {
	
	public static void oneChipSound(){
		AudioStream cardSound=null;
		try {
			cardSound=new AudioStream(new FileInputStream(new File("OneChip.wav")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		AudioPlayer.player.start(cardSound);
	}
	
	public static void manyChipSound(){
		AudioStream cardSound=null;
		try {
			cardSound=new AudioStream(new FileInputStream(new File("ManyChips.wav")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		AudioPlayer.player.start(cardSound);
	}
	
	public static void twoCardsSound(){
		AudioStream cardSound=null;
		try {
			cardSound=new AudioStream(new FileInputStream(new File("2Cards.wav")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		AudioPlayer.player.start(cardSound);
	}
	
	public static void manyCardsSound(){
		AudioStream cardSound=null;
		try {
			cardSound=new AudioStream(new FileInputStream(new File("manyCards.wav")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		AudioPlayer.player.start(cardSound);
	}
}
