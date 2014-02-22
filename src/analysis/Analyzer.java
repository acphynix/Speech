package analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import synthesizer.SpeechSynthesizer;

public class Analyzer {
	public Analyzer() {
		
	}
	public static AudioPoint simplify(short[] in, double threshold, int begin, int end){
		AudioPoint ret = new AudioPoint(-1, 0);
		final AudioPoint head = ret;
		//find a repeating pattern in the audio sample.
		int lastX=-1;
		int lastY=0;
		double sumOfAngles=0;
		Point beginPoint=new Point(-1,0);
		Point lastPoint=new Point(-1,0);
		double numAngles = 1;
		for(int i=1;i<in.length;i++){
			lastPoint = new Point(i+begin-1,in[i+begin-1]);
			int x=i+begin;
			int y=in[i+begin];
			if(x>end)break;
			if(beginPoint==null){
				beginPoint=new Point(x,y);
				continue;		//go to next point, so that angle is not undefined.
			}
			double newAngle = Math.toDegrees(Math.atan((double)(y-lastPoint.y)/(double)(x-lastPoint.x)));		//angle between start point and this point.
			//SpeechSynthesizer.diagPrint(newAngle);
			if(newAngle>180)newAngle-=360;
			if(newAngle<-180)newAngle+=360;
			double averageAngle = (sumOfAngles+newAngle)/(numAngles+1);		//get average angle, NOT including this point
			if(Math.abs(newAngle - averageAngle)>threshold){		//angle difference > threshold
				ret.setNext(new AudioPoint(x, y));
				ret=ret.getNext();
				sumOfAngles=newAngle;		//reset all counts.
				beginPoint = new Point(x,y);
				numAngles=1;
			}else{
				numAngles++;
				sumOfAngles+=newAngle;
			}
		}
		//SpeechSynthesizer.diagPrint("frames: "+keyFrames);
		//write out to file.
		return head;
	}

	public static void writeWaveformToFile(AudioPoint in, File out){
		System.out.print("\nExporting...");
		System.out.println(in);
		//display repeating waveform in image.
		BufferedImage output= new BufferedImage(1600,600,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = output.createGraphics();
		g.setColor(Color.darkGray);
		g.fillRect(0,0,1600,600);
		g.setColor(Color.black);
//		double step = Math.max(1,in.size()/1600);
		double xscale=1;
		double yscale=0.05;
		int xstart=in.x;
		g.setColor(new Color(20,80,140,90));
		//draw a vertical line every 25 frames.	
		for(int i=0;i<1600;i+=25*xscale){
			g.drawLine(i,0,i,600);
		}
		g.setColor(new Color(20,80,140));
		//draw a vertical line every 50 frames.	
		for(int i=0;i<1600;i+=50*xscale){
			g.drawLine(i,0,i,600);
		}
		//draw a dash every 5 frames.
		for(int i=0;i<1600;i+=5*xscale){
			g.drawLine(i,290,i,310);
		}
		g.setColor(new Color(100,160,220));
		for(int i=0;i<1600;i+=100*xscale){
			g.drawString(""+(int)(i/xscale),i+3,370);
		}
		g.setColor(new Color(20,80,140));
		g.drawLine(0,300,1600,300);
		AudioPoint t=in;
		while(t.getNext().getNext()!=null){
			Point a = new Point(t.x,t.y);
			Point b = new Point(t.getNext().x,t.getNext().y);
			if(a.x<xstart)continue;
			if(a.x>xstart+(1600.0/xscale))break;
			g.setColor(Color.black);
			g.drawLine((int)((a.x-xstart)*xscale), 300-(int)(a.y*yscale), (int)((b.x-xstart)*xscale), 300-(int)(b.y*yscale));
			g.setColor(Color.yellow);
			g.fillOval((int)((a.x-xstart)*xscale)-2,300-(int)(a.y*yscale)-2,4,4);
			t=t.getNext();
		}
		try {
			ImageIO.write(output, "png", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
