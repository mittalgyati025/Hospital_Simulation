//import java.io.*;
import java.util.Vector;

public final class NurseQ {
 // private static String _newline = System.getProperty("line.separator");
  Vector<Nurse> _list = new Vector<Nurse>(); 

public NurseQ() {
    
}


public boolean isEmpty() {
	if (_list.size() >0)
          return false;
	else
          return true;
}
 
public void enqueue(Nurse s) {
	  //System.out.println("schedule= "+event.toString());
    // int size = _list.size();
    _list.addElement(s);
}

public void dequeue(int i) {
	if (_list.size() >= 0) {
		_list.removeElementAt(i);
	}
}
  
public Nurse getithserver(int i) {
if (_list.size()>0) {
  	return (Nurse) _list.elementAt(i);
  }		
  return null;
}
  
public int size() {
	return _list.size();
}
  
}