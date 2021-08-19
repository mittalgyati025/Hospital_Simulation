import java.util.Vector;

public class EventQ {
Vector<Event> evQ = new Vector<Event>();

/**
 * To get the vector queue
 * @return
 */
public Event getfirstevent() {
		if (evQ.size()>0) {
			return (Event) evQ.elementAt(0)	;
		}		
		return null;
}

public Event getithevent(int i) {
	if (evQ.size()>0)
	{
		return (Event) evQ.elementAt(i)	;
	}		
	return null;
}

public int getsize() {
	if (evQ.size()>0)
	{
		return (int) evQ.size()	;
	}		
return 0;
}

//Just a simple add function to add end or other events
public void addEvent(Event evAdd) {
	if(evAdd.objsType.equalsIgnoreCase("end"))
	{
		evQ.add(evAdd);
	}
	else if (evQ.size()>0)
	{
		double time = evAdd.objdTime;
		for(int i = 0; i < evQ.size(); i++ )
		{
			Event ev = (Event) evQ.elementAt(i);
			if(time < ev.objdTime)
			{
				evQ.insertElementAt(evAdd, i);
				break;
			}
		}
	}
	else
    {
    	evQ.add(0,evAdd);	
	}
}
			
public void eraseEvent()
{	
	this.evQ.removeElementAt(0);
	
}

public void eraseEventAti(int i)
{	
	this.evQ.removeElementAt(i);
	
}
		
		
		
		}