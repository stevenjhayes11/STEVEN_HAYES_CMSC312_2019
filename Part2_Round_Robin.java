import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;

public class Part2_Round_Robin {


	protected static class Thread
	{
		private int cyclesNeeded;
		private int currentCycles;
		private int threadID;
		String threadType;
		private boolean isCritical;
		
		
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
		protected boolean isCrit()
		{
			return isCritical;
		}
	}
	protected static class Process
	{
		private int state;
		private int timeNeeded;
		private int currentTime;
		private int memRequired;
		private int processID;
		private String processName;
		private ArrayList<Thread> threads;
		
		protected Process()
		{
			
		}
		protected Process(Scanner process, int processID)
		{
			
			threads = new ArrayList<Thread>();
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
		 * state 0 is ready, 1 is running, 2 is waiting
		 */
		protected void setState(int newState)
		{
			state = newState;
		}
		protected int getState()
		{
			return state;
		}
		protected void setTimeNeeded(int timeNeeded)
		{
			this.timeNeeded = timeNeeded;
		}
		protected int getTimeNeeded()
		{
			return timeNeeded;
		}
		protected void setCurrentTime(int timeModifier)
		{
			currentTime = currentTime - timeModifier;
		}
		protected int getCurrentTime()
		{
			return currentTime;
		}
		protected void setMemRequired(int memRequired)
		{
			this.memRequired = memRequired;
		}
		protected int getMemRequired()
		{
			return memRequired;
		}
		protected void setProcessName(String processName)
		{
			this.processName = processName;
		}
		protected String getProcessName()
		{
			return processName;
		}
		protected void addThread(Thread newThread)
		{
			threads.add(newThread);
		}
		protected int runProcess(int maxTime)
		{
			int count = 0;
			boolean threadComplete = false;
			//Run the thread until the max amount of cycles is reached or if its crit
			while((count < maxTime && !threadComplete) || (threads.get(0).isCrit() && !threadComplete))
			{
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
		protected int getSize()
		{
			return threads.size();
		}

	}
	protected static class Scheduler
	{
		private int totalMem;
		private int availMem;
		private ArrayList<Process> processList;
		private Dispatcher mainDispatch;
		
		protected Scheduler(int totalMem)
		{
			this.totalMem = totalMem;
			availMem = totalMem;
			processList = new ArrayList<Process>();
		}
		protected Scheduler(int totalMem, ArrayList<Process> processList)
		{
			processList = new ArrayList<Process>(); 
			this.totalMem = totalMem;
			availMem = totalMem;
			this.processList = processList;
		}	
		protected void modifyMem(int memModifier)
		{
			this.availMem = availMem + memModifier;
		}
		protected int getAvailMem()
		{
			return availMem;
		}
		protected void removeProcess(int processPos)
		{
			this.modifyMem(processList.get(processPos).getMemRequired());
			processList.remove(processPos);
		}
		protected void addProcess(Process newProcess)
		{
			processList.add(newProcess);
			this.modifyMem(-(newProcess.getMemRequired()));
		}
		protected Process getProcess(int processPos)
		{
			return processList.get(processPos);
		}
		protected int getSize()
		{
			return processList.size();
		}
		protected int executeCycle(int cycleTime)
		{
			int executionTime = processList.get(0).runProcess(cycleTime);
			return executionTime;
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
	public static void main(String[] args) throws FileNotFoundException
	{
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
		
		//gets a certain number of repitions from each file template
		//and stores them in either the RAM scheduler or HDD scheduler
		int currentID = 0;
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
				Process newProcess = new Process(sc, currentID++);
				if(schedulerRAM.getAvailMem() > newProcess.getMemRequired())
				{
					schedulerRAM.addProcess(newProcess);
				}
				else
				{
					schedulerHDD.addProcess(newProcess);
				}	
			}
		}
		
		//Now that the memory/scheduler is loaded with all the processes
		//start running through the RAM, executing the processes
		int totalCycles = 0;

		while(schedulerRAM.getSize() > 0)
		{
			if(schedulerRAM.getProcess(0).getSize() != 0)
			{
				int cyclesRun = schedulerRAM.executeCycle(cycleLength);
				totalCycles = totalCycles + cyclesRun;
				Process temp = schedulerRAM.getProcess(0);
				schedulerRAM.removeProcess(0);
				schedulerRAM.addProcess(temp);
			}
			else
			{
				schedulerRAM.removeProcess(0);
				System.out.println("Process Removed");
				
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
				
		//TODO add switching from HDD memory to RAM memory checks	
		}
	}
	
	
}
