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
		DeltaPoint begin = pattern.get(beginIndex);
		System.out.println(beginIndex+": "+begin);
		for(int i=0;i<pattern.size();i++){
			DeltaPoint test =pattern.get(i);
			double diff = begin.difference(test);
			//if(diff<1000)System.out.println("Index "+i+", "+pattern.get(i)+", "+diff);
			if(diff<1000 && i<pattern.size()-17)results.add(i);
		}
		//the results array contains a list of the indices of the closest DeltaPoints to the search pattern (beginIndex)
		ArrayList<Double> scores = new ArrayList<Double>();
		for(Integer i:results){	//for each contending deltapoint.
			double score=0;
			double[] point_scores = new double[15];
			int iteration=0;
			for(int j=i;j<i+15;j++){	//check all values 5 indices into the future...
				DeltaPoint contender = pattern.get(beginIndex+(j-i));
				double pointScore=Double.MAX_VALUE;
				DeltaPoint[] tests=new DeltaPoint[1+(iteration/2)];
				for(int n=0;n<tests.length;n++)tests[n]=pattern.get(j+n);
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
			System.out.println(results.get(i)+"["+input.get(results.get(i)+1).simpleToString()+"]: "+pattern.get(results.get(i))+" ->\t"+scores.get(i));
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
		public String toString(){
			return "("+run+", "+rise+")";
		}
	}
}
