import java.io.Serializable;
import java.util.ArrayList;
public class ChunkWrapper implements Serializable{

    public ArrayList<Integer> leftArrList;
    public ArrayList<Integer> midArrList;
    public ArrayList<Integer> rightArrList;

    public ChunkWrapper(ArrayList<Integer> _left, ArrayList<Integer> _mid, ArrayList<Integer> _right ){
        leftArrList  = _left;
        midArrList   = _mid;
        rightArrList = _right;
    }

    public ArrayList<Integer> getLeftPart(){
        return rightArrList;
    }

    public ArrayList<Integer> getMidPart(){
        return midArrList;
    }

    public ArrayList<Integer> getRightPart(){
        return rightArrList;
    }

}
