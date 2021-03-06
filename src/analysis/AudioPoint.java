package analysis;

public class AudioPoint {

	public AudioPoint(int xx, int yy) {
		x=xx;
		y=yy;
	}
	public AudioPoint(double xx, double yy) {
		x=xx;
		y=yy;
	}
	private double x;
	double Sx;
	private double y;
	double Sy;
	double dX;
	double dX2;
	private AudioPoint before;
	private AudioPoint after;
	/**
	 * Segments and clones this AudioPoint. Returns the beginning point.
	 * @param prev number of steps left, inclusive
	 * @param next number of steps right, inclusive
	 * getSegment(0,0) is identical to simpleCopy().
	 * @return
	 */
	public AudioPoint getSegment(int prev, int next){
		AudioPoint pivot = simpleCopy();
		//take care of {next} first.
		AudioPoint tempReal=this;
		AudioPoint tempCloned=pivot;
		for(int i=0;i<=next;i++){
			tempCloned.setNext(tempReal.getNext().simpleCopy());
			tempReal=tempReal.after;
			tempCloned=tempCloned.after;
		}
		tempReal=this;
		tempCloned=pivot;
		for(int i=0;i<prev;i++){
			tempCloned.setBefore(tempReal.getPrev().simpleCopy());
			tempReal=tempReal.before;
			tempCloned=tempCloned.before;
		}
		return tempCloned;
	}
	public AudioPoint subList(int begin, int end){
		if(begin<0 || end<0)throw new java.lang.UnsupportedOperationException("Ashwin: Cannot pass negative numbers");
		AudioPoint ret = null;
		AudioPoint temp=this;
		for(int i=0;i<begin;i++)temp=temp.after;
		ret=temp.simpleCopy();
		AudioPoint tempCloned=ret;
		for(int i=begin;i<end;i++){
			tempCloned.setNext(temp.getNext().simpleCopy());
			temp=temp.getNext();
			tempCloned=tempCloned.after;
		}
		return ret;
	}
	public AudioPoint simpleCopy(){
		AudioPoint k=new AudioPoint(x,y);
		k.x=x;
		k.y=y;
		k.Sx=Sx;
		k.Sy=Sy;
		k.dX=dX;
		k.dX2=dX2;
		return k;
	}
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
	public AudioPoint getLast(){
		AudioPoint temp=this;
		while(temp.after!=null)temp=temp.after;
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
		return "("+getX()+", "+getY()+")";
	}
	public String toString(){
		return AudioPoint.toString(this);
	}
	public void addXY(double x, double y){
		
	}
	public int getX() {
		return (int)x;
	}
	public int getY() {
		return (int)y;
	}
	/**
	 * scales the x and y components of this audiopoint as well as all parents and children.
	 * @param parameters to multiply x and y by.
	 */
	public void scale(double sx, double sy){
		AudioPoint temp=this;
		while((temp=temp.after)!=null){		//all points after this.
			temp.x*=sx;
			temp.y*=sy;
		}
		temp=this;
		while((temp=temp.before)!=null){	//all points before this.
			temp.x*=sx;
			temp.y*=sy;
		}
		x*=sx;							//this.
		y*=sy;
	}
	/**
	 * translates the x and y components of this audiopoint as well as all parents and children.
	 * @param parameters to multiply x and y by.
	 */
	public void translate(double tx, double ty){
		AudioPoint temp=this;
		while((temp=temp.after)!=null){		//all points after this.
			temp.x+=tx;
			temp.y+=ty;
		}
		temp=this;
		while((temp=temp.before)!=null){	//all points before this.
			temp.x+=tx;
			temp.y+=ty;
		}
		x+=tx;							//this.
		y+=ty;
	}
}
