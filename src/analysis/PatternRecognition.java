package analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PatternRecognition {
	public PatternRecognition(AudioPoint stream){
		input=stream;
		pattern = new ArrayList<PatternRecognition.DeltaPoint>();
		random=new Random();
		init(input);
	}
	AudioPoint input;
	ArrayList<DeltaPoint> pattern;
	Random random;
	private void init(AudioPoint in){
		AudioPoint t=in;
		while((t=t.getNext())!=null){
			AudioPoint v=t.getPrev();
			pattern.add(new DeltaPoint(t.y-v.y, t.x-v.x));
		}
		searchForPatterns();
	}
	/**
	 * Search for recurring patterns within *pattern*.
	 */
	private void searchForPatterns(){
		System.out.println(pattern);
		ArrayList<Integer> results= new ArrayList<Integer>();
		//int beginIndex = random.nextInt(pattern.size());
		int beginIndex=3;
		double max_mag =0;
		for(int i=0;i<Math.min(100, pattern.size());i++){
			double newMag = pattern.get(i).magnitude();
			if(newMag>max_mag){
				beginIndex=i;
				max_mag=newMag;
			}
		}
		//first, search for a beginning index: namely, a region of high contrast in the graph.
		DeltaPoint begin = pattern.get(beginIndex);
		System.out.println("Begin Index: "+beginIndex+" "+input.get(beginIndex+1).simpleToString()+"- "+begin);
		for(int i=0;i<pattern.size();i++){
			DeltaPoint test =pattern.get(i);
			double diff = begin.difference(test);
			if(diff<5)System.out.println("Index "+i+", "+pattern.get(i)+", "+diff);
			if(diff<5 && i>3 && i<pattern.size()-23)results.add(i);
		}
		//the results array contains a list of the indices of the closest DeltaPoints to the search pattern (beginIndex)
		ArrayList<Double> scores = new ArrayList<Double>();
		for(Integer i:results){	//for each contending deltapoint.
			double score=0;
			double[] point_scores = new double[15];
			int iteration=0;
			for(int j=i;j<i+5;j++){	//check all values 5 indices into the future...
				DeltaPoint contender = pattern.get(beginIndex+(j-i));
				double pointScore=Double.MAX_VALUE;
				DeltaPoint[] tests=new DeltaPoint[5+iteration];
				for(int n=0;n<tests.length;n++){	
					tests[n]=pattern.get(j+n-3);
				}
				int bestIndex=0;
				for(int index=0;index<tests.length;index++){
					double newScore = tests[index].difference(contender);
					if(newScore<pointScore){
						bestIndex=index;
						pointScore=newScore;
					}
				}
				//j+=bestIndex;
				pointScore*=pointScore;
				point_scores[iteration]=pointScore;
				score+=pointScore;
				iteration++;
			}
			score/=15.0;
			//System.out.println(Arrays.toString(point_scores));
			Arrays.sort(point_scores);
			double median;
			if (point_scores.length % 2 == 0)
			    median = ((double)point_scores[point_scores.length/2 -1] + (double)point_scores[point_scores.length/2 - 1])/2;
			else
			    median = (double) point_scores[point_scores.length/2];
			//System.out.println(score+"\t"+median);
			//score = median;
			scores.add(score);
		}
		for(int i=0;i<results.size();i++){
			if(scores.get(i)<20)System.out.println(results.get(i)+"["+input.get(results.get(i)+1).simpleToString()+"]: "+pattern.get(results.get(i))+" ->\t"+scores.get(i));
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
			return Math.abs((magnitude()-p.magnitude())*(angle()-p.angle()));
//			double xf = rise-p.rise;
//			double yf = run-p.run;
//			return Math.sqrt(xf*xf+yf*yf);
		}
		double magnitude(){
			return Math.abs(Math.sqrt((double)rise*(double)rise + (double)run*(double)run));
		}
		double angle(){
			return Math.atan2((double)rise,(double)run);
		}
		double slope(){
			return (double)rise/(double)run;
		}
		public String toString(){
			return "("+run+", "+rise+")";
		}
	}
}
