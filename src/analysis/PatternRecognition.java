package analysis;

import java.util.ArrayList;

public class PatternRecognition {
	public PatternRecognition(AudioPoint stream){
		input=stream;
		pattern = new ArrayList<PatternRecognition.DeltaPoint>();
		init(input);
	}
	AudioPoint input;
	ArrayList<DeltaPoint> pattern;
	private void init(AudioPoint in){
		AudioPoint t=input;
		while((t=t.getNext())!=null){
			AudioPoint v=t.getPrev();
			pattern.add(new DeltaPoint(t.y-v.y, t.x-v.x));
		}
		searchForPatterns();
	}
	private void searchForPatterns(){
		for(int i=0;i<pattern.size();i++){
			DeltaPoint d=pattern.get(i);
			for(int j=0;j<i;j++){
				
			}
		}
	}
	private class DeltaPoint{
		DeltaPoint(int rise, int run){
			this.rise=rise;
			this.run=run;
		}
		int rise;
		int run;
		static final float similarityThreshold=1.f;
		double difference(DeltaPoint p){
			double xf = rise-p.rise;
			double yf = run-p.run;
			return Math.sqrt(xf*xf+yf*yf);
		}
		float slope(){
			return (float)rise/(float)run;
		}
	}
}
