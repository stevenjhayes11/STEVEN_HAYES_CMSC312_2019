import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Part1RoundRobin {

	/*
	 * For the round Robin method, scheduler will create
	 * a list of processes from the Text that was read in
	 * and execute processes based on this list
	 */
	protected static class Scheduler{
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
		protected Process getProcess(int index)
		{
			return processList.get(index);
		}
		protected int getSize()
		{
			return processList.size();
		}

	}
	
	/*
	 * Class holds process data such as state, how many iterations of the process
	 * will be executed, and what type of process it is
	 */
	protected static class Process{
		//PCB DATA
		int ID;
		int state; //0 - ready, 1 - running, 2 - waiting
		//DATA
		int timeNeeded;
		int currentTime;
		String processType;
		
		protected Process(int ID, int state, int timeNeeded, String processType)
		{
			this.ID = ID;
			this.state = state;
			this.timeNeeded = timeNeeded;
			currentTime = 0;
			this.processType = processType;
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
		protected int getCurrentTime()
		{
			return currentTime;
		}
		protected int getTimeNeeded()
		{
			return timeNeeded;
		}
		protected String getProcessType()
		{
			return processType;
		}
	}
	
	protected static class Dispatcher{
		protected Dispatcher()
		{
			
		}
		protected void changeStateToReady(Process processToChangeState)
		{
			processToChangeState.setState(0);
		}
		protected void changeStateToRunning(Process processToChangeState)
		{
			processToChangeState.setState(1);
		}
		protected void changeStateToWaiting(Process processToChangeState)
		{
			processToChangeState.setState(2);
		}
		
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// TODO Keep track of number of cycles for efficiency analysis		
		
		//initialize scheduler
		
		Scheduler scheduler;
		
		//load in file
		
		//read header info
		//---store Header info
		String name;
		int runtime;
		int availMem;
		Dispatcher dispatch = new Dispatcher();
		
		//read in processes and create PCB on intialization
		//QUESTION - put all processes in then go through, correct?
		ArrayList<Process> processList = new ArrayList<Process>(1);
		File source = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor1.txt");
		
		Scanner sc = new Scanner(source);
		int ID = 0;
		while(sc.hasNextLine())
		{
			String temp = sc.nextLine();
			int state;
			int timeNeeded;
			String processType;
			
			if(temp.contains("CALCULATE"))
			{
				processType = "CALCULATE";
				temp = temp.substring(10);
			}
			else if(temp.contains("I/O"))
			{
				processType = "I/O";
				temp = temp.substring(4);
			}
			else if(temp.contains("YIELD"))
			{
				processType = "YIELD";
				temp = temp.substring(6);
			}
			else
			{
				processType = "OUT";
				temp = temp.substring(4);
			}
			timeNeeded = Integer.parseInt(temp);
			
			Process newProcess = new Process(ID++, 0, timeNeeded, processType);
			processList.add(newProcess);
		}
		scheduler = new Scheduler(processList);
		
		
		int currentProcess = 0;
		 
		while(scheduler.getSize() != 0)
		{
		//execute currentProcess until complete or
  		//i == 49
				boolean processRemoved = false;
				for(int i = 0; i < 25; i++)
			  	{
				 	//TODO need to initialize processList before this
					//if the process has not been completed
					if(scheduler.getProcess(currentProcess).getCurrentTime() != scheduler.getProcess(currentProcess).getTimeNeeded())
			  		{
			  			scheduler.getProcess(currentProcess).incrementTime();
			  			System.out.print(scheduler.getProcess(currentProcess).getID() + " ");
			  			System.out.println(scheduler.getProcess(currentProcess).getProcessType() + " " + scheduler.getProcess(currentProcess).getCurrentTime());
			  		}
			  		//if process has been completed, remove it from scheduler list
			  		else
			  		{
			  			i = 25;
			  			scheduler.removeProcess(currentProcess);
			  			processRemoved = true;
			  		}
					
			  		
			  	}
			//TODO change state of old process
			System.out.println("first " + currentProcess);
			if(processRemoved)
			{
				if(currentProcess >= scheduler.getSize())
					currentProcess = 0;
				processRemoved = false;
			}
			else
			{
				dispatch.changeStateToReady(scheduler.getProcess(currentProcess));
			 	currentProcess++;
			 	if(currentProcess >= processList.size())
			  		currentProcess = 0;	
			}
			System.out.println("second " + currentProcess);
			 
			if(scheduler.getSize() != 0)
			{
				dispatch.changeStateToRunning(scheduler.getProcess(currentProcess));
			}
					
		}
		
		 
		 
		 
		//start executing Processes
		//---dispatcher starts at head of process list
		//---gives a process time Q to execute
		//---at end of time Q or when process is finished
		//------stop or end process(move to ready or terminate)
		//---move down list to next process unless process is exe
		
	}

}
