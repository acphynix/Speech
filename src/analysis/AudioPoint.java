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
		System.out.println(before);
		if(before!=null)bef.setBefore(before);
		before=bef;
		before.setNext(this);
	}
	public AudioPoint getNext(){
		return after;
	}
	public AudioPoint getPrev(){
		return before;
	}
	public static String toString(AudioPoint p){
		AudioPoint a=p;
		String t="";
		while((a=a.after)!=null){
			t+=a.dataToString()+"; ";
		}
		return t;
	}
	private String dataToString(){
		return "("+x+", "+y+")";
	}
	public String toString(){
		return AudioPoint.toString(this);
	}
}
