import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/*
 * This program simulates an operating system as defined in the project guidelines
 * using a round robin scheduler
 */

public class Part1RoundRobin {

	/*
	 * For the round Robin method, scheduler will create
	 * a list of processes from the Text that was read in
	 * and execute processes based on this list
	 */
	protected static class Scheduler{
		//List of processes
		protected ArrayList<Process> processList;
		
		/*
		 * Constructor, assigns process list to Scheduler's arrayList
		 * @param list of processes
		 */
		protected Scheduler(ArrayList<Process> processList)
		{
			this.processList = processList;
		}
		/*
		 * adds a process to the current list of processes
		 * @param new process
		 */
		protected void addProcess(Process newProcess)
		{
			processList.add(newProcess);
		}
		/*
		 * removes a process from the specified location
		 * @param location of process to be removed
		 */
		protected void removeProcess(int index)
		{
			processList.remove(index);
		}
		/*
		 * get process from specified location
		 * @param location of process to be retrieved
		 * @return process at that location
		 */
		protected Process getProcess(int index)
		{
			return processList.get(index);
		}
		/*
		 * gets how many processes are in the array list
		 * @return number of processes remaining
		 */
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
		int timeNeeded; //how many times a process will execute
		int currentTime; //how many times a process has executed
		String processType;
		
		/*
		 * Constructor, creates all PCB data except currentTime, which is always initially 0
		 * @param ID - number ID of process
		 * @param state - current state of process
		 * @param timeNeeded - how long to execute the process
		 * @param processType - type of process
		 */
		protected Process(int ID, int state, int timeNeeded, String processType)
		{
			this.ID = ID;
			this.state = state;
			this.timeNeeded = timeNeeded;
			currentTime = 0;
			this.processType = processType;
		}
		/*
		 * sets the state of this process
		 * @param state to be set to
		 */
		protected void setState(int state)
		{
			this.state = state;
		}
		/*
		 * gets the state this process is in
		 * @return state process is in
		 */
		protected int getState()
		{
			return state;
		}
		/*
		 * sets process ID
		 * @param ID to be set this to
		 */
		protected void setID(int ID)
		{
			this.ID = ID;
		}
		/*
		 * gets process ID
		 * @return this process ID
		 */
		protected int getID()
		{
			return ID;
		}
		/*
		 * increments that of the number of times this has executed
		 */
		protected void incrementTime()
		{
			currentTime++;
		}
		/*
		 * gets how many times this has executed
		 * @return how many times it has executed
		 */
		protected int getCurrentTime()
		{
			return currentTime;
		}
		/*
		 * gets how much time this needs to execute from start to finish,
		 * independent of current execution time
		 * @return time needed to finish
		 */
		protected int getTimeNeeded()
		{
			return timeNeeded;
		}
		/*
		 * gets the type of process
		 * @return this process type
		 */
		protected String getProcessType()
		{
			return processType;
		}
	}
	
	/*
	 * Dispatcher only changes the state of processes
	 */
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
		
		String name;
		int runtime;
		int availMem;
		Dispatcher dispatch = new Dispatcher();
		
		//read in processes and create PCB on intialization
		ArrayList<Process> processList = new ArrayList<Process>(1);
		File source = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor1.txt");
		
		Scanner sc = new Scanner(source);
		int ID = 0;
		//while there are more lines remaining from the file, keep getting new data
		for(int i = 0; i < 4; i++)
			sc.nextLine();
		boolean isEXE = false;
		
		while(sc.hasNextLine())
		{
			String temp = sc.nextLine();
			int state;
			int timeNeeded;
			String processType;
			
			//get process type
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
			else if(temp.contains("OUT"))
			{
				processType = "OUT";
				temp = temp.substring(4);
			}
			else
			{
				processType = "EXE";
				isEXE = true;
			}
			
			//get number of process executions then add a random between 1-25
			if(isEXE)
				timeNeeded = 1;
			else
			{
				timeNeeded = Integer.parseInt(temp);
				Random rand = new Random();
				int x = rand.nextInt(25);
				timeNeeded = timeNeeded + x;
			}
	
			//create a new process using parsed data and add it to the process List
			Process newProcess = new Process(ID++, 0, timeNeeded, processType);
			processList.add(newProcess);
		}
		
		//add process list to scheduler
		scheduler = new Scheduler(processList);
		
		
		int currentProcess = 0;
		 
		
		//as long as scheduler has items still in it
		while(scheduler.getSize() != 0)
		{
		//execute currentProcess until complete or
  		//i == 49
				boolean processRemoved = false;
				//execute until process completes of i == 25
				System.out.println(scheduler.getProcess(currentProcess).getProcessType());
				for(int i = 0; i < 25; i++)
			  	{
					//if EXE is reached and there are no other processes, end
					if(scheduler.getSize() == 1 && scheduler.getProcess(currentProcess).getProcessType().equalsIgnoreCase("EXE"))
					{
						System.out.println("All Processes Completed");
						i = 25;
						scheduler.removeProcess(currentProcess);
						processRemoved = true;
					}
					//if EXE is reached and there ARE other process, skip EXE
					else if(scheduler.getSize() != 1 && scheduler.getProcess(currentProcess).getProcessType().equalsIgnoreCase("EXE"))
					{
						i = 25;
					}
					//if the process has not been completed, execute next part
					else if(scheduler.getProcess(currentProcess).getCurrentTime() != scheduler.getProcess(currentProcess).getTimeNeeded())
			  		{
			  			scheduler.getProcess(currentProcess).incrementTime();
			  			System.out.print(scheduler.getProcess(currentProcess).getID() + " ");
			  			System.out.println(scheduler.getProcess(currentProcess).getProcessType() + " " + scheduler.getProcess(currentProcess).getCurrentTime());
			  		}
			  		//if process has been completed, remove it from scheduler list and
					//attempt to go to next process
			  		else
			  		{
			  			i = 25;
			  			scheduler.removeProcess(currentProcess);
			  			processRemoved = true;
			  		}
					
			  		
			  	}
				
			//if the process was completed, remove it from the list
			if(processRemoved)
			{
				if(currentProcess >= scheduler.getSize())
					currentProcess = 0;
				processRemoved = false;
			}
			//if the process was not completed, change state to ready and
			//move on to next process on the list or to beginning of list if at the end
			else
			{
				dispatch.changeStateToReady(scheduler.getProcess(currentProcess));
			 	currentProcess++;
			 	if(currentProcess >= processList.size())
			  		currentProcess = 0;	
			}
			 
			//as long as there are still processes remaining, change the new process
			//to be worked's state on to "running"
			if(scheduler.getSize() != 0)
			{
				//If the process is I/O it goes to waiting, else goes to running
				if(scheduler.getProcess(currentProcess).getProcessType().equalsIgnoreCase("I/O"))
					dispatch.changeStateToWaiting(scheduler.getProcess(currentProcess));
				else
					dispatch.changeStateToRunning(scheduler.getProcess(currentProcess));
			}
					
		}
		
	}

}
//TODO
//if calc(25) represents 25 calc processes, not a calc process that runs for 25 cycles
//1. change process class/create a new class which will have be an arraylist of the 
//types of process and will hold the number of those processes to be executed and their PCB info
//
//2. give each individual process a random amount of time needed to run
//
//
//OR
//upon creating a process object like normal, instead of creating 1 process to add to the scheduler
//create the required amount and give them all random run times

