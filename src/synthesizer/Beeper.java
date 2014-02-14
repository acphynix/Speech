package synthesizer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.text.DecimalFormat;

import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Beeper extends JPanel{
    public static void main(String[] args) throws LineUnavailableException {
        Beeper b = new Beeper();
        b.trun();
    }
    
    Clip clip;
    final float sampleRate = 48000;
    DecimalFormat decimalFormat = new DecimalFormat("###00.00");

    void trun(){
    	try {
    		clip = AudioSystem.getClip();
			tgenerateAndPlay();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	while(clip.isActive());
    }
    void tgenerateAndPlay() throws LineUnavailableException, IOException, InterruptedException{
        clip.stop();
		clip.close();
		byte[] buf = new byte[(int)sampleRate*2];
		AudioFormat af = new AudioFormat(
		    sampleRate,
		    16,  // sample size in bits
		    1,  // channels
		    true,  // signed
		    false  // bigendian
		    );
		SpeechSynthesizer synth = new SpeechSynthesizer(sampleRate, af);
		buf = synth.generateAudio();
	    byte[] b = buf;
	    AudioInputStream ais = new AudioInputStream(
	        new ByteArrayInputStream(b),
	        synth.selfFormat,
	        buf.length );

	    clip.open( ais );
		clip.start();
		Thread.sleep(300);
        while(clip.isRunning()){
            Thread.sleep(10);
        }
    }

}