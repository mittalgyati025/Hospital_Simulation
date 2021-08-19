//import java.io.*;
import java.util.Vector;

public final class TechnicianQ {
// private static String _newline = System.getProperty("line.separator");
Vector<Technician> _list = new Vector<Technician>(); 

public TechnicianQ() {
  
}


public boolean isEmpty() {
	if (_list.size() >0)
        return false;
	else
        return true;
}

public void enqueue(Technician t) {
	  //System.out.println("schedule= "+event.toString());
  // int size = _list.size();
  _list.addElement(t);
}

public void dequeue(int i) {
	if (_list.size() >= 0) {
		_list.removeElementAt(i);
	}
}

public Technician getithserver(int i) {
if (_list.size()>0) {
	return (Technician) _list.elementAt(i);
}		
return null;
}

public int size() {
	return _list.size();
}

}
