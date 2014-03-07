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
		//System.out.println(input.subList(2, 3));
		//getRepeatedWaveforms();
		//next, create a Pattern, and feed it all repetitions that are found in the input stream.
	}
	/**
	 * Combines two AudioPoints into one, preserving some of the elements of both.
	 * This function assumes that the two audiopoints are relatively similar.
	 */
	private AudioPoint synthesize(AudioPoint a, AudioPoint b){
		AudioPoint ret=new AudioPoint(a.getX(),a.getY());
		AudioPoint ret_temp=ret;
		AudioPoint anext;
		AudioPoint bnext;
		while(true){
			anext=a.getNext();
			bnext=b.getNext();
			DeltaPoint da = new DeltaPoint(a.getX(), a.getY(), anext.getX(), anext.getY());
			DeltaPoint db = new DeltaPoint(b.getX(), b.getY(), bnext.getX(), bnext.getY());
			double diff = da.difference(db);
			if(diff<1.3){	//nothing to be worried about. just append the average.
				ret_temp.setNext(new AudioPoint((anext.getX()+bnext.getX())/2.0,(anext.getY()+bnext.getY())/2.0));
				ret_temp=ret_temp.getNext();
				a=anext;
				b=bnext;
			}else{		//something's different between a and b.
				boolean fixed=false;
				if(!fixed){		//1. check if b has extra points.
					AudioPoint temp=b;
					iterate:
					for(int i=0;i<4;i++){
						temp=temp.getNext();
						DeltaPoint newb = new DeltaPoint(b.getX(), b.getY(), temp.getX(), temp.getY());
						double newdiff = da.difference(newb);
						if(newdiff<1.3){		//we fixed it, so figure out what we did...and do it again.
							fixed=true;
							AudioPoint temp2 = b;
							for(int k=0;k<i;k++){
								temp2=temp2.getNext();
								ret_temp.setNext(temp2.simpleCopy());
							}
							b=temp2;
							a=anext;
							break iterate;
						}
					}
				}
				if(!fixed){		//2. check if a has extra points.
					AudioPoint temp=a;
					iterate:
					for(int i=0;i<4;i++){
						temp=temp.getNext();
						DeltaPoint newa = new DeltaPoint(a.getX(), a.getY(), temp.getX(), temp.getY());
						double newdiff = db.difference(newa);
						if(newdiff<1.3){		//we fixed it, so figure out what we did...and do it again.
							fixed=true;
							AudioPoint temp2 = a;
							for(int k=0;k<i;k++){
								temp2=temp2.getNext();
								ret_temp.setNext(temp2);
							}
							break iterate;
						}
					}
				}
				//2. check if a has extra points.
				//3. if not...then find the next index where a and b are similar again.
				//	3a. average out the problematic area.
			}
			if(false)break;
		}
		return null;
	}
	public ArrayList<AudioPoint> getRepeatedWaveforms(){
		ArrayList<Integer> reps = searchForPatterns();	//repetitions
		ArrayList<AudioPoint> ret=new ArrayList<AudioPoint>();
		for(int ind=0;ind<reps.size()-1;ind++){
			int i=reps.get(ind);
			int j=reps.get(ind+1);
			AudioPoint seq = input.subList(i, j);
			seq.translate(-seq.getX(), 0);
			ret.add(seq);
		}
		return ret;
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
		DeltaPoint(double x1, double y1, double x2, double y2){
			rise = (int)(y2-y1);
			run = (int)(x2-x1);
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
