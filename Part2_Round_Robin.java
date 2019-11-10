import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;

public class Part2_Round_Robin {

/*
 * Thread objects are the individual commands taken from processes
 * such as calculate, also hold the time required to execute
 * a thread
 */
	protected static class Thread
	{
		private int cyclesNeeded;
		private int currentCycles;
		private int threadID;
		String threadType;
		private boolean isCritical;
		
		/*
		 * Constructor
		 * parses out the thread info from a string
		 * @param String representation of thread
		 * @param ID of new thread
		 */
		protected Thread(String thread, int ID)
		{
			threadID = ID;
			if(thread.contains("CALCULATE"))
			{
				threadType = "CALCULATE";
				thread = thread.substring(10);
				cyclesNeeded = Integer.parseInt(thread);
				isCritical = true;
			}
			else if(thread.contains("I/O"))
			{
				threadType = "I/O";
				thread = thread.substring(4);
				cyclesNeeded = Integer.parseInt(thread);
				isCritical = false;
			}
			else if(thread.contains("YIELD"))
			{
				threadType = "YIELD";
				thread = thread.substring(6);
				cyclesNeeded = Integer.parseInt(thread);
				isCritical = false;
			}
			else if(thread.contains("OUT"))
			{
				threadType = "OUT";
				thread = thread.substring(4);
				cyclesNeeded = Integer.parseInt(thread);
				isCritical = false;
			}
			else
			{
				threadType = "EXE";
				cyclesNeeded = 1;
				isCritical = false;
			}
			currentCycles = 0;
		}
		/*
		 * Runs the thread through a single iteration of
		 * its command
		 * @return whether thread has completed all
		 * cycles
		 * TODO for next part of project
		 * this iteration of runThread does not allow
		 * for accurate tracking of run time due to critical
		 * section handling
		 */
		protected boolean runThread()
		{
			
			if(this.currentCycles == this.cyclesNeeded)
			{
				System.out.println("Thread Complete");
				return true;
			}
			else
			{
				currentCycles++;
				if(this.isCrit())
					System.out.print("Critical: ");
				System.out.println(this.threadID + " " + this.threadType + "Current Cycle: " + currentCycles);
				return false;
			}
		}
		/*
		 * @return if thread is critical/uninterruptible
		 */
		protected boolean isCrit()
		{
			return isCritical;
		}
		/*
		 * @return type of thread i.e. calculate
		 */
		protected String getThreadType()
		{
			return threadType;
		}
	}
	
	/*
	 * The Process object's main duty is to keep a list of
	 * threads and to run those threads when called from a
	 * scheduler
	 */
	protected static class Process
	{
		private int state;
		private int timeNeeded;
		private int currentTime;
		private int memRequired;
		private int processID;
		private String processName;
		private ArrayList<Thread> threads;
		
		/*
		 * Constructor
		 * Brings in a scanner which contains the output from 
		 * the template files and an integer ID
		 * The scanner then is run through to extract PCB data
		 * and individual Thread strings, which are then passed
		 * to the thread class for further parsing
		 */
		protected Process(Scanner process, int processID)
		{
			
			threads = new ArrayList<Thread>();
			this.processID = processID;
			//Extract PCB data
			state = 0;
			for(int i = 0; i < 4; i++)
			{
				if(i == 0)
				{
					processName = process.nextLine();
					processName = processName.substring(6);
					System.out.println("Name: " + processName);
				}
				else if(i == 1)
				{
					String temp = process.nextLine();
					temp = temp.substring(15);
					timeNeeded = Integer.parseInt(temp);
					System.out.println("Time Needed: " + timeNeeded);
				}
				else if(i == 2)
				{
					String temp = process.nextLine();
					temp = temp.substring(8);
					memRequired = Integer.parseInt(temp);
					System.out.println("Memory Required: " + memRequired);
				}
				else
				{
					process.nextLine();
				}
			}
			
			//create threads from remaining scanner
			int counterID = 1;
			while(process.hasNextLine())
			{
				String temp = process.nextLine();
				System.out.println(temp);
				Thread newThread = new Thread(temp, counterID);
				threads.add(newThread);
				counterID++;
			}
		}
		/*
		 * Sets the state of a process, only used in Dispatcher
		 * state 0 is ready, 1 is running, 2 is waiting
		 * @param new State
		 */
		protected void setState(int newState)
		{
			state = newState;
		}
		/*
		 * @return current state
		 */
		protected int getState()
		{
			return state;
		}
		/*
		 * Sets memory needed by the process
		 * @param memory needed by process
		 */
		protected void setMemRequired(int memRequired)
		{
			this.memRequired = memRequired;
		}
		/*
		 * @return memory required by processor
		 */
		protected int getMemRequired()
		{
			return memRequired;
		}
		/*
		 * @return the thread at the front of the
		 * list of threads, which is the thread 
		 * that will be executing at any given time
		 */
		protected Thread getcurrentThread()
		{
			return threads.get(0);
		}
		/*
		 * @param thread to be added to thread list
		 */
		protected void addThread(Thread newThread)
		{
			threads.add(newThread);
		}
		/*
		 * @return integer ID value for process
		 */
		protected int getProcessID()
		{
			return processID;
		}
		/*
		 * runProcess's main duty is to call the runThread function
		 * it first checks if the current thread is EXE and alone,
		 * if so it will recognize the process has been completed
		 * If it is not an EXE thread, then it will run a thread 
		 * either maxTime times or, if the thread is critical, until
		 * the thread has completed
		 * @param maximum number of times to runThread
		 * @return number of cycles taken to run
		 */
		protected int runProcess(int maxTime)
		{
			if(threads.get(0).getThreadType().equalsIgnoreCase("EXE"))
			{	
				if(threads.size() == 1)
				{	
					System.out.println(this.processID + " EXE");
					System.out.println("PROCESS ENDED");
					threads.remove(0);
					return 1;
				}
				else
				{
					Thread tempThread = threads.remove(0);
					threads.add(tempThread);
				}
			}
			int count = 0;
			boolean threadComplete = false;
			//Run the thread until the max amount of cycles is reached or if its crit
			while((count < maxTime && !threadComplete) || (threads.get(0).isCrit() && !threadComplete))
			{
				System.out.print("Process ID: " + getProcessID() + " ");
				threadComplete = threads.get(0).runThread();
				count++;
			}
			if(threadComplete)
				threads.remove(0);
			else
			{
				Thread tempThread = threads.remove(0);
				threads.add(tempThread);
			}
			return count;
		}
		/*
		 * @return number of threads in this process
		 */
		protected int getSize()
		{
			return threads.size();
		}

	}
	/*
	 * The scheduler object is primarily used as a memory location
	 * and to keep track of how many processes are remaining, and
	 * what state they are in. It's other primary function is to call the 
	 * executeCycle function, which then calls the runProcess function
	 * and changes the states of the relevant processes
	 */
	protected static class Scheduler
	{
		private int totalMem;
		private int availMem;
		private ArrayList<Process> processList;
		private Dispatcher mainDispatch;
		
		/*
		 * Constructor
		 * @param total memory this scheduler has to use
		 */
		protected Scheduler(int totalMem)
		{
			this.totalMem = totalMem;
			availMem = totalMem;
			processList = new ArrayList<Process>();
			mainDispatch = new Dispatcher();
		}
		/*
		 * Constructor
		 * @param total memory to use
		 * @param list of processes
		 */
		protected Scheduler(int totalMem, ArrayList<Process> processList)
		{
			processList = new ArrayList<Process>(); 
			this.totalMem = totalMem;
			availMem = totalMem;
			this.processList = processList;
			mainDispatch = new Dispatcher();
		}	
		/*
		 * change available memory
		 * @param amount to be changed by
		 */
		protected void modifyMem(int memModifier)
		{
			this.availMem = availMem + memModifier;
		}
		/*
		 * @return available memory
		 */
		protected int getAvailMem()
		{
			return availMem;
		}
		/*
		 * removes a process from the process array list
		 * @param position of process to be removed
		 */
		protected void removeProcess(int processPos)
		{
			this.modifyMem(processList.get(processPos).getMemRequired());
			processList.remove(processPos);
		}
		/*
		 * adds a process to the end of the list
		 * @param process to be added
		 */
		protected void addProcess(Process newProcess)
		{
			processList.add(newProcess);
			this.modifyMem(-(newProcess.getMemRequired()));
		}
		/*
		 * @param list position
		 * @return process at processPos
		 */
		protected Process getProcess(int processPos)
		{
			return processList.get(processPos);
		}
		/*
		 * @return process array list size
		 */
		protected int getSize()
		{
			return processList.size();
		}
		/*
		 * changes states of processes as they are being executed 
		 * and calls the runProcess cycle
		 * @param time allocated for a non critical process to run
		 * @return how many cycles were needed
		 */
		protected int executeCycle(int cycleTime)
		{
			if(processList.get(0).getcurrentThread().getThreadType().equalsIgnoreCase("Calculate"))
				mainDispatch.changeStateToRunning(processList.get(0));
			else if(processList.get(0).getcurrentThread().getThreadType().equalsIgnoreCase("I/O"))
				mainDispatch.changeStateToWaiting(processList.get(0));
			int executionTime = processList.get(0).runProcess(cycleTime);
			mainDispatch.changeStateToReady(processList.get(0));
			return executionTime;
		}
	}
	
	/*
	 * Dispatcher class only changes the states of processes
	 * 0 is ready
	 * 1 is running
	 * 2 is waiting
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
	public static void main(String[] args) throws FileNotFoundException
	{
		//initialize memory
		Scheduler schedulerRAM = new Scheduler(150);
		Scheduler schedulerHDD = new Scheduler(100000);
		int cycleLength = 25;
		
		//Get user input for how many times to execute each process
		Scanner kb = new Scanner(System.in);
		System.out.println("How many processes to run for test files 1, 2, 3, and 4?");
		int test1 = kb.nextInt();
		int test2 = kb.nextInt();
		int test3 = kb.nextInt();
		int test4 = kb.nextInt();
		
		//gets a certain number of repetitions from each file template
		//and stores them in either the RAM scheduler or HDD scheduler
		int currentID = 1;
		for(int i = 0; i < 4; i++)
		{
			int reps;
			if(i == 0)
				reps = test1;
			else if(i == 1)
				reps = test2;
			else if(i == 2)
				reps = test3;
			else
				reps = test4;
			Scanner sc;
			
			
			while(reps > 0)
			{
				reps--;
				switch(i)
				{
				case(0):
					File source1 = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor1.txt");
					sc = new Scanner(source1);
					break;
				case(1):
					File source2 = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor2.txt");
					sc = new Scanner(source2);
					break;
				case(2):
					File source3 = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor3.txt");
					sc = new Scanner(source3);
					break;
				case(3):
					File source4 = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor4.txt");
					sc = new Scanner(source4);
					break;
				default:
					File source5 = new File("C:\\Users\\steve\\Desktop\\VCU\\CMSC 312\\Test Files for Part 1\\TextProcessor1.txt");
					sc = new Scanner(source5);
					break;
				}
				System.out.println("currentID: " + currentID);
				Process newProcess = new Process(sc, currentID);
				System.out.println("New Process ID: " + newProcess.getProcessID());
				currentID++;
				if(schedulerRAM.getAvailMem() > newProcess.getMemRequired())
				{
					schedulerRAM.addProcess(newProcess);
				}
				else
				{
					schedulerHDD.addProcess(newProcess);
					System.out.println("added to HDD");
				}	
			}
		}
		
		//Now that the memory/scheduler is loaded with all the processes
		//start running through the RAM, executing the processes
		int totalCycles = 0;

		while(schedulerRAM.getSize() > 0)
		{
			//if the current process in ram still has threads remaining
			//then execute a cycle
			if(schedulerRAM.getProcess(0).getSize() != 0)
			{
				int cyclesRun = schedulerRAM.executeCycle(cycleLength);
				totalCycles = totalCycles + cyclesRun;
				Process temp = schedulerRAM.getProcess(0);
				schedulerRAM.removeProcess(0);
				schedulerRAM.addProcess(temp);
			}
			//else remove the process, check and see if a new process
			//from the HDD can be inserted into the RAM
			else
			{
				System.out.println("Process " + schedulerRAM.getProcess(0).getProcessID() + " Removed post completion");
				schedulerRAM.removeProcess(0);
				if(schedulerHDD.getSize() != 0)
				{
					if(schedulerHDD.getProcess(0).getMemRequired() < schedulerRAM.getAvailMem())
					{
						Process temp = schedulerHDD.getProcess(0);
						schedulerHDD.removeProcess(0);
						schedulerRAM.addProcess(temp);
						System.out.println("Switch from HDD to RAM");
					}
				}
			}
				
		}
	}
	
	
}
