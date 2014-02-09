package synthesizer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.text.DecimalFormat;

import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;

public class Beeper extends JPanel{
    public static void main(String[] args) throws LineUnavailableException {
        Beeper b = new Beeper();
        b.tinit();
        b.trun();
    }
    
    Clip clip;
    final float sampleRate = 48000;
    DecimalFormat decimalFormat = new DecimalFormat("###00.00");

    void trun(){
    	tgenerateAndPlay();
    	while(clip.isActive());
    }
    void tinit() throws LineUnavailableException{
    	clip = AudioSystem.getClip();
    }
    void tgenerateAndPlay(){
        try {
			generateTone();
			//clip.start();
		} catch (LineUnavailableException e){
			e.printStackTrace();
		}
        long begin = System.currentTimeMillis();
        try {
			Thread.sleep(300);
	        long end = begin;
	        while(clip.isRunning()){
	        	end = (System.currentTimeMillis());
	            Thread.sleep(10);
	        }
        }
        catch(InterruptedException e){}
    }
    int samplesPerWave(float frequency){
    	return (int) (sampleRate/frequency);
    }

    /** Generates a tone, and assigns it to the Clip.
     * @throws LineUnavailableException  */
    public void generateTone() throws LineUnavailableException {    	 clip.stop();    	 clip.close();

        int intFPW = 25;
        int wavelengths = 20;
        byte[] buf = new byte[(int)sampleRate*2];
        AudioFormat af = new AudioFormat(
            sampleRate,
            8,  // sample size in bits
            1,  // channels
            true,  // signed
            false  // bigendian
            );        SpeechSynthesizer synth = new SpeechSynthesizer(sampleRate, af);
        buf = synth.generateAudio();
        
        try {
            byte[] b = buf;
            AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(b),
                synth.selfFormat,
                buf.length );

            clip.open( ais );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** Provides the byte value for this point in the sinusoidal wave. */
    private static byte getByteValue(double angle) {
        int maxVol = 127;
        return (new Integer(
            (int)Math.round(
            Math.sin(angle)*maxVol))).
            byteValue();
    }
}