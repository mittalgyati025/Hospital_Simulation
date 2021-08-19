//import java.io.*;
import java.util.Vector;

public final class PatientQ {
 // private static String _newline = System.getProperty("line.separator");
  Vector<Event> _list = new Vector<Event>(); 

public PatientQ() {
    
}

public boolean isEmpty() {
	if (_list.size() >0)
          return false;
    else
          return true;
}
  
public Event dequeue() {
	if (_list.size() >= 0) {
       Event ev = (Event)_list.elementAt(0);
      _list.removeElementAt(0);
       return ev;
    }
    else return null;
}

public void enqueue(Event ev) {
	  //System.out.println("schedule= "+event.toString());
	_list.addElement(ev);
}

public Event getithevent(int i) {
  if (_list.size()>0)
  {
  	return (Event) _list.elementAt(i)	;
  }		
  return null;
}
  
public void eraseEventAti(int i) {	
	_list.removeElementAt(i);
}
  
public int size() {
	return _list.size();
}
  
}