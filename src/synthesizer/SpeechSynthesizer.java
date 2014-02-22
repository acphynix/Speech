package synthesizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

import analysis.Analyzer;
import analysis.AudioPoint;
import analysis.PatternRecognition;

public class SpeechSynthesizer {
	public SpeechSynthesizer(float sampleRate, AudioFormat af) {
		this.sampleRate=sampleRate;
		selfFormat=af;
	}
	float sampleRate;
	AudioFormat selfFormat;
	byte[] rawAudio;
	short[] shortAudio;
	private byte[] convertTo(byte[] data, AudioFormat from, AudioFormat to){
		if(from.matches(new AudioFormat(
			selfFormat.getSampleRate(),
            16,  // sample size in bits
            1,  // channels
            true,  // signed
            false  // bigendian
            ))){
			byte[] newData = new byte[data.length];
			shortAudio = new short[data.length/2];
			for(int i=0;i<newData.length/2;i++){
				ByteBuffer bb = ByteBuffer.allocate(2);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				bb.put(data[i*2]);
				bb.put(data[i*2+1]);
				shortAudio[i]=bb.getShort(0);
				newData[i*2]=data[i*2];
				newData[i*2+1]=data[i*2+1];
			}
			return newData;
		}else throw new java.lang.UnsupportedOperationException("Ashwin: Unsupported audio format.");
	}
	private byte[] loadAudio(File file, AudioFormat format){
		
		try{
			Clip c= AudioSystem.getClip();
			c.open((AudioSystem.getAudioInputStream(file)));
			SpeechSynthesizer.diagPrint(c.getFormat());
			selfFormat = c.getFormat();
			SpeechSynthesizer.diagPrint(selfFormat);
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			//c.open(ais);
			//c.start();
			System.err.println(ais.available());
			ArrayList<byte[]> readIn = new ArrayList<byte[]>();
			byte[] buffer=new byte[4096];
			int res;
			int totalSize=0;
			while(true){
				res = ais.read(buffer);
				if(res==4096){
					readIn.add(buffer);
					buffer=new byte[4096];
					totalSize+=4096;
				}
				else if(res<1)break;
				else{
					byte[] q = new byte[res];
					System.arraycopy(buffer, 0, q, 0, res);
					readIn.add(q);
					totalSize+=res;
					break;
				}
			}
			byte[] allBytes = new byte[((readIn.size()-1)*4096)+res];
			int pos=0;
			for(byte[] i:readIn){
				System.arraycopy(i, 0, allBytes, pos, i.length);
				pos+=i.length;
			}
			return convertTo(allBytes, c.getFormat(), selfFormat);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public byte[] generateAudio(){
		String filename = "short e";
		byte[] in = loadAudio(new File("rsc/"+filename+".wav"),selfFormat);
        rawAudio=in;
        String suffix="A";
      //  double i=3;
        int index=0;
        lockDiag=true;
//        for(double i=0;i<33;i=(i==0)?1:(int)(i*2)){
        for(int i=0;i<99;i+=100){
        	index++;
        	suffix+="a";
        	AudioPoint p = Analyzer.simplify(shortAudio,32.0,i,i+1200);
        	Analyzer.writeWaveformToFile(p,new File("rsc/output/m"+filename+(String.format("%04.1f", (double)i))+".png"));
    		lockDiag=false;
            PatternRecognition pr = new PatternRecognition(p);
        }
        //analyze(rawAudio);
    		SpeechSynthesizer.diagPrint(selfFormat);
        return rawAudio;
	}
	static boolean lockDiag=true;
	static boolean diag=true;
	static void diagPrint(Object o){
		if(diag && lockDiag)System.out.println(o);
	}
}
