package analysis;

public class AudioPoint {

	public AudioPoint(int xx, int yy) {
		x=xx;
		y=yy;
	}
	int x;
	int y;
	private AudioPoint before;
	private AudioPoint after;
	public void setNext(AudioPoint next){
		if(next == after)return;
		if(after!=null)next.setNext(after);
		after=next;
		next.setBefore(this);
	}
	public void setBefore(AudioPoint bef){
		if(bef == before)return;
		//System.out.println(before);
		if(before!=null)bef.setBefore(before);
		before=bef;
		before.setNext(this);
	}
	public AudioPoint get(int next){
		if(next<0)throw new java.lang.IllegalArgumentException("Index < 0");
		AudioPoint temp=this;
		while(next>0){
			temp=temp.after;
			next--;
		}
		return temp;
	}
	public AudioPoint getNext(){
		return after;
	}
	public AudioPoint getPrev(){
		return before;
	}
	private static String toString(AudioPoint p){
		AudioPoint a=p;
		String t="";
		while((a=a.after)!=null){
			t+=a.simpleToString()+"; ";
		}
		return t;
	}
	public String simpleToString(){
		return "("+x+", "+y+")";
	}
	public String toString(){
		return AudioPoint.toString(this);
	}
}
