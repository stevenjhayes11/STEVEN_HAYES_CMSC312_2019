import java.util.*;

public class Part1RoundRobin {

	/*
	 * For the round Robin method, scheduler will create
	 * a list of processes from the Text that was read in
	 * and execute processes based on this list
	 */
	protected class Scheduler{
		protected ArrayList<Process> processList;
		protected Scheduler(ArrayList<Process> processList)
		{
			this.processList = processList;
		}
		protected void addProcess(Process newProcess)
		{
			processList.add(newProcess);
		}
		protected void removeProcess(int index)
		{
			processList.remove(index);
		}
	}
	
	/*
	 * Class holds process data
	 */
	protected class Process{
		//PCB DATA
		int ID;
		int state;
		//DATA
		int timeNeeded;
		int currentTime;
		
		protected Process(int ID, int state, int timeNeeded)
		{
			this.ID = ID;
			this.state = state;
			this.timeNeeded = timeNeeded;
			currentTime = 0;
		}
		protected void setState(int state)
		{
			this.state = state;
		}
		protected int getState()
		{
			return state;
		}
		protected void setID(int ID)
		{
			this.ID = ID;
		}
		protected int getID()
		{
			return ID;
		}
		protected void incrementTime()
		{
			currentTime++;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//initialize scheduler
		
		//load in file
		
		//read header info
		//---store Header info
		String name;
		int runtime;
		int availMem;
		
		//read in processes and create PCB on intialization
		//QUESTION - put all processes in then go through, correct?
		ArrayList<Process> processList;
		
		
		/*
		 * while(process isn't EXE)
		 * {
		 * 	for(int i = 0; i < 50; i++)
		 * 	{
		 * 		execute current process until complete or
		 * 		i == 49
		 * 	}
		 * 	change current process to next in Queue
		 * }
		 */
		//start executing Processes
		//---dispatcher starts at head of process list
		//---gives a process time Q to execute
		//---at end of time Q or when process is finished
		//------stop or end process(move to ready or terminate)
		//---move down list to next process unless process is exe
		
	}

}
