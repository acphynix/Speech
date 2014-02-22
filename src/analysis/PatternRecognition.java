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
			pattern.add(new DeltaPoint(t.getY()-v.getY(), t.getX()-v.getX()));
		}
		ArrayList<Integer> reps = searchForPatterns();	//repetitions
		//next, create a Pattern, and feed it all repetitions that are found in the input stream.
	}
	/**
	 * Search for a single recurring pattern within *pattern*.
	 * @return An arraylist containing the beginning indices of each repeating pattern.
	 */
	private ArrayList<Integer> searchForPatterns(){
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
		//beginIndex=3;
		//first, search for a beginning index: a region of high contrast in the graph.
		DeltaPoint begin = pattern.get(beginIndex);
		System.out.println("Begin Index: "+beginIndex+" "+input.get(beginIndex+1).simpleToString()+"- "+begin);
		for(int i=0;i<pattern.size();i++){
			DeltaPoint test =pattern.get(i);
			double diff = begin.difference(test);
			if(diff<5)System.out.println("Index "+i+", "+pattern.get(i)+", "+diff);
			if(diff<5 && i>3 && i<pattern.size()-32)results.add(i);
		}
		//the results array contains a list of the indices of the closest DeltaPoints to the search pattern (beginIndex)
		ArrayList<Double> scores = new ArrayList<Double>();
		for(Integer i:results){	//for each contending deltapoint.
			double score=0;
			double[] point_scores = new double[15];
			int iteration=0;
			for(int j=i;j<i+15;j++){	//check all values 15 indices into the future...
				DeltaPoint contender = pattern.get(beginIndex+(j-i));	//the actual template  	DeltaPoint
				double pointScore=Double.MAX_VALUE;
				DeltaPoint[] tests=new DeltaPoint[3*(5+iteration)];
				DeltaPoint cumulPoint = new DeltaPoint(0,0);
				for(int n=0;n<tests.length;n+=3){
					int curr_index = j+(n/2)-3;
					tests[n]=pattern.get(curr_index);
					//cumulPoint = new DeltaPoint(0,0);
					//DeltaPoint oldPoint = new DeltaPoint(pattern.get(j-3).rise, pattern.get(j-3).run);
					DeltaPoint newPoint = new DeltaPoint(pattern.get(curr_index).rise, pattern.get(curr_index).run);
					tests[n+1]=new DeltaPoint(newPoint.rise+pattern.get(curr_index+1).rise, newPoint.run+pattern.get(curr_index+1).run);
					tests[n+2]=new DeltaPoint(newPoint.rise+pattern.get(curr_index-1).rise, newPoint.run+pattern.get(curr_index-1).run);
					//tests[n]=tests[n+1];
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
				pointScore*=pointScore;			//square pointScore so that larger values are given more weight.
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
		ArrayList<Integer> finalResults=new ArrayList<Integer>();
		for(int i=0;i<results.size();i++){
			if(scores.get(i)<2){
				finalResults.add(results.get(i));
				System.out.println(input.get(results.get(i)+1).simpleToString());
			}
		}
		System.out.println(finalResults);
		return finalResults;
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
