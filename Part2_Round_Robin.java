import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;

public class Part2_Round_Robin {

	protected static class Cache
	{
		Register register = new Register();
		ArrayList<Page> pageList;
		protected Cache()
		{
			pageList = new ArrayList<Page>();
		}
		protected boolean addPage(Page newPage)
		{
			boolean success = register.addPage(newPage);
			if(success)
				return true;
			else
			{
				if(pageList.size() < 10)
				{
					pageList.add(newPage);
					return true;
				}
				else
					return false;
			}
		}
		protected boolean addToRegister(Page newPage)
		{
			return register.addPage(newPage);
		}
		protected Page getPage()
		{
			Page tempPage = pageList.remove(0);
			return tempPage;
		}
		protected Register getRegister()
		{
			return register;
		}
		protected int getSize()
		{
			return pageList.size();
		}
		protected boolean isEmpty()
		{
			return (this.getSize() == 0 && register.getSize() == 0);
		}
	}
	protected static class Register
	{
		ArrayList<Page> pageList;
		protected Register()
		{
			pageList = new ArrayList<Page>();
		}
		protected boolean addPage(Page newPage)
		{
			if(pageList.size() < 5)
			{
				pageList.add(newPage);
				return true;
			}
			else
				return false;
		}
		protected int getSize()
		{
			return pageList.size();
		}
		protected Page getPage()
		{
			Page tempPage = pageList.remove(0);
			return tempPage;
		}
	}
	protected static class Page
	{
		int processID;
		boolean isCrit;
		ArrayList<Operation> operations;
		protected Page()
		{
			operations = new ArrayList<Operation>();
			isCrit = false;
		}
		protected void setCrit()
		{
			isCrit = true;
		}
		protected boolean addOperation(Operation op, int parentProcessID)
		{
			processID = parentProcessID;
			if(operations.size() < 10)
			{
				operations.add(op);
				return true;
			}
			else
				return false;
		}
		/*protected String runPage()
		{
			String result = "";
			for(int i = 0; i < operations.size(); i++)
				result = result + this.runOperation() + "\n";
			return result;
		}*/
		protected String runPage()
		{
			String result = "";
			int top = operations.size();
			for(int i = 0; i < top; i++)
			{
				Operation temp = operations.get(0);
				result = result + "Process ID: " + this.processID + " ";
				result = result + temp.getOperationType() + ": " + temp.getID() + " ";
				result = result + "Cycle: " + temp.operationID + "\n"; 
				operations.remove(0);
			}
			return result;
		}
		public String toString()
		{
			String result = "";
			for(int i = 0; i < operations.size(); i++)
			{
				Operation temp = operations.get(i);
				result = result + "Process ID: " + this.processID + " ";
				result = result + temp.getOperationType() + ": " + temp.getID() + " ";
				result = result + "Cycle: " + temp.operationID + "\n";
			}
				return result;
		}
	}
	
	/*
 * operation objects are the individual commands taken from processes
 * such as calculate, also hold the time required to execute
 * a operation
 */
	protected static class Operation
	{
		protected int cyclesNeeded;
		protected int currentCycles;
		protected int superOperationID;
		protected int operationID;
		String operationType;
		protected boolean isCritical;
		
		/*
		 * Constructor
		 * parses out the operation info from a string
		 * @param String representation of operation
		 * @param ID of new operation
		 */
		protected Operation(String operationType, int superID, int childID, boolean isCrit)
		{
			this.operationID = childID;
			this.operationType = operationType;
			this.superOperationID = superID;
			this.isCritical = isCrit;
			cyclesNeeded = 1;
			currentCycles = 0;
		}
		protected Operation(String operation, int ID)
		{
			superOperationID = ID;
			if(operation.contains("CALCULATE"))
			{
				operationType = "CALCULATE";
				operation = operation.substring(10);
				cyclesNeeded = Integer.parseInt(operation);
				isCritical = true;
			}
			else if(operation.contains("I/O"))
			{
				operationType = "I/O";
				operation = operation.substring(4);
				cyclesNeeded = Integer.parseInt(operation);
				isCritical = false;
			}
			else if(operation.contains("YIELD"))
			{
				operationType = "YIELD";
				operation = operation.substring(6);
				cyclesNeeded = Integer.parseInt(operation);
				isCritical = false;
			}
			else if(operation.contains("OUT"))
			{
				operationType = "OUT";
				operation = operation.substring(4);
				cyclesNeeded = Integer.parseInt(operation);
				isCritical = false;
			}
			else
			{
				operationType = "EXE";
				cyclesNeeded = 1;
				isCritical = false;
			}
			currentCycles = 0;
		}
		protected int getID()
		{
			return superOperationID;
		}
		/*
		 * Runs the operation through a single iteration of
		 * its command
		 * @return whether operation has completed all
		 * cycles
		 * TODO for next part of project
		 * this iteration of runOperation does not allow
		 * for accurate tracking of run time due to critical
		 * section handling
		 */
		protected boolean runOperation()
		{
			
			if(this.currentCycles == this.cyclesNeeded)
			{
				System.out.println("Operation Complete");
				return true;
			}
			else
			{
				currentCycles++;
				if(this.isCrit())
					System.out.print("Critical: ");
				System.out.println(this.superOperationID + " " + this.operationType + "Current Cycle: " + currentCycles);
				return false;
			}
		}
		/*
		 * @return if operation is critical/uninterruptible
		 */
		protected boolean isCrit()
		{
			return isCritical;
		}
		/*
		 * @return type of operation i.e. calculate
		 */
		protected String getOperationType()
		{
			return operationType;
		}
		protected int getCyclesNeeded()
		{
			return cyclesNeeded;
		}
	}
	
	/*
	 * The Process object's main duty is to keep a list of
	 * operations and to run those operations when called from a
	 * scheduler
	 */
	protected static class Process
	{
		protected int state;
		protected int timeNeeded;
		protected int currentTime;
		protected int memRequired;
		protected int processID;
		protected String processName;
		protected ArrayList<Operation> operations;
		ArrayList<Page> pageList;
		
		/*
		 * Constructor
		 * Brings in a scanner which contains the output from 
		 * the template files and an integer ID
		 * The scanner then is run through to extract PCB data
		 * and individual operation strings, which are then passed
		 * to the operation class for further parsing
		 */
		protected Process(Scanner process, int processID)
		{
			pageList = new ArrayList<Page>();
			Page firstPage = new Page();
			pageList.add(firstPage);
			operations = new ArrayList<Operation>();
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
			
			//create pages with 10 operations from remaining scanner
			int counterID = 1;
			while(process.hasNextLine())
			{
				String temp = process.nextLine();
				System.out.println(temp);
				Operation superOperation = new Operation(temp, counterID);
				System.out.println("Parent: " + superOperation.getOperationType());
				for(int i = 0; i < superOperation.getCyclesNeeded(); i++)
				{
					//Split Large operation into smaller single Operations
					String childType = superOperation.getOperationType();
					int ID = superOperation.getID();
					boolean isCrit = superOperation.isCrit();
					Operation childOperation = new Operation(childType, ID, i+1, isCrit);
					
					//add child operation to current Page or create new page to add
					//it to
					boolean success = pageList.get(pageList.size() - 1).addOperation(childOperation, this.processID);
					if(!success)
					{
						System.out.println(pageList.get(pageList.size() - 1).toString());
						Page tempPage = new Page();
						pageList.add(tempPage);
						pageList.get(pageList.size() - 1).addOperation(childOperation, this.processID);
					}
				}

				//if there was space on that page for an operation, add it
				//otherwise create a new page and add the operation to the new page
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
		protected void addPage(Page newPage)
		{
			pageList.add(newPage);
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
		 * @return the operation at the front of the
		 * list of operations, which is the operation 
		 * that will be executing at any given time
		 */
		protected Operation getcurrentOperation()
		{
			return operations.get(0);
		}
		protected Page getNextPage()
		{
			Page tempPage = pageList.remove(0);
			return tempPage;
		}

		/*
		 * @return integer ID value for process
		 */
		protected int getProcessID()
		{
			return processID;
		}
		/*
		 * RunProcess's function is to call a page from it's
		 * list of pages and to execute that page
		 * @return number of cycles taken to run
		 */
		
		protected int getSize()
		{
			return pageList.size();
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
		protected int totalMem;
		protected int availMem;
		protected ArrayList<Process> processList;
		protected Dispatcher mainDispatch;
		
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
		int semaphore = 0;
		
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

		Cache core1 = new Cache();
		Cache core2 = new Cache();
		Cache core3 = new Cache();
		Cache core4 = new Cache();
		int core1Cycles = 0;
		int core2Cycles = 0;
		int core3Cycles = 0;
		int core4Cycles = 0;
		
		boolean processesExist = true;
		System.out.println("Start running");
		while(processesExist)
		{
			boolean success = true;
			int counter = 0;
			//while there are processes in RAM and Cores are not full
			//uses round robin to get processes from scheduler
			while(schedulerRAM.getSize() > 0 && success)
			{
				//pop a page from the RAM
				double IOChance = Math.random();
				Page tempPage = new Page();
				if(IOChance < .01)
				{
					int numOps = (int) (Math.random()*10);
					tempPage.setCrit();
					for(int i = 0; i < numOps; i++)
					{
						Operation IOinterrupt = new Operation("I/O", 0, i+1, true);
						tempPage.addOperation(IOinterrupt, 0);
					}
							
				}
				else
				{
					tempPage = schedulerRAM.getProcess(0).getNextPage();
				}
				
				//Send the Pages to Cores (which in turn send to registers if space 
				//is available) until Cores are full or RAM is empty
				success = core1.addPage(tempPage);
				if(!success)
					success = core2.addPage(tempPage);
				if(!success)
					success = core3.addPage(tempPage);
				if(!success)
					success = core4.addPage(tempPage);
				if(!success)
					schedulerRAM.getProcess(0).addPage(tempPage);
					
				//cycles process to end of RAM scheduler for round robin req
				Process tempProcess = schedulerRAM.getProcess(0);
				schedulerRAM.removeProcess(0);
				schedulerRAM.addProcess(tempProcess);
				
				//get new processes from HDD if possible
				if(schedulerRAM.getProcess(0).getSize() == 0)
				{
					schedulerRAM.removeProcess(0);
					System.out.println("Process Removed From RAM");
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
					counter = 0;
				}
				//if 10 pages have been added from a process and there are still more
				//round robin to a new process
				if(counter > 10)
				{
					Process cycleProcess = schedulerRAM.getProcess(0);
					schedulerRAM.removeProcess(0);
					schedulerRAM.addProcess(cycleProcess);
				}
				
					
			}
			
			
			
			//Run all processes in Registers
			System.out.println("Running Core Registers");
			System.out.println("Core1 Register");
			for(int i = 0; i < core1.getRegister().getSize(); i++)
				System.out.println(core1.getRegister().getPage().runPage());
			System.out.println("Core2 Register");
			for(int i = 0; i < core2.getRegister().getSize(); i++)
				System.out.println(core2.getRegister().getPage().runPage());
			System.out.println("Core3 Register");
			for(int i = 0; i < core3.getRegister().getSize(); i++)
				System.out.println(core3.getRegister().getPage().runPage());
			System.out.println("Core4 Register");
			for(int i = 0; i < core4.getRegister().getSize(); i++)
				System.out.println(core4.getRegister().getPage().runPage());
			
			int core1CacheCycles;
			if(core1.getSize() > 5)
				core1CacheCycles = 5;
			else
				core1CacheCycles = core1.getSize();
			
			int core2CacheCycles;
			if(core2.getSize() > 5)
				core2CacheCycles = 5;
			else
				core2CacheCycles = core2.getSize();
			
			int core3CacheCycles;
			if(core3.getSize() > 5)
				core3CacheCycles = 5;
			else
				core3CacheCycles = core3.getSize();
			
			int core4CacheCycles;
			if(core4.getSize() > 5)
				core4CacheCycles = 5;
			else
				core4CacheCycles = core4.getSize();
			
			//Run cache pages either 5 pages apeice or until the cache is empty
			//each for loop creates 5 threads which each take one page from the
			//core and execute on their own
			System.out.println("Running Core Cache (Multi)");
			System.out.println("Core1 Cache");
			ArrayList<Page> core1List = new ArrayList<Page>();
			for(int i = 0; i < core1CacheCycles; i++)
			{
				Page tempPage = core1.getPage();
				core1List.add(tempPage);
				//System.out.println(core1.getPage().runPage());
			}
			ProcessorThread core1Threads = new ProcessorThread(core1List);
			core1Threads.start();
			System.out.println("Core2 Cache");
			ArrayList<Page> core2List = new ArrayList<Page>();
			for(int i = 0; i < core2CacheCycles; i++)
			{
				Page tempPage = core2.getPage();
				core2List.add(tempPage);
				//System.out.println(core1.getPage().runPage());
			}
			ProcessorThread core2Threads = new ProcessorThread(core2List);
			core2Threads.start();
			System.out.println("Core3 Cache");
			ArrayList<Page> core3List = new ArrayList<Page>();
			for(int i = 0; i < core3CacheCycles; i++)
			{
				Page tempPage = core3.getPage();
				core3List.add(tempPage);
				//System.out.println(core1.getPage().runPage());
			}
			ProcessorThread core3Threads = new ProcessorThread(core3List);
			core3Threads.start();
			System.out.println("Core4 Cache");
			for(int i = 0; i < core4CacheCycles; i++)
			{
				Page tempPage = core4.getPage();
				ProcessorThread newThread = new ProcessorThread(tempPage);
				newThread.start();
				//System.out.println(core1.getPage().runPage());
			}
			
			boolean coresEmpty = core1.isEmpty() && core2.isEmpty() && core3.isEmpty() && core4.isEmpty();
			boolean schedulersEmpty = (schedulerRAM.getSize() == 0 && schedulerHDD.getSize() == 0);
			if(schedulersEmpty && coresEmpty)
				processesExist = false;
		}
		System.out.println("All processes complete");
	}
	protected static class ProcessorThread extends Thread
	{
		ArrayList<Page> pageList;
		public ProcessorThread(ArrayList<Page> pageList)
		{
			this.pageList = pageList;
		}
		public void run()
		{
			try
			{
				for(int i = 0; i < pageList.size(); i++)
					System.out.println(pageList.get(i).runPage());
			}
			catch(Exception e)
			{
				System.out.println("Exception Caught");
			}
		}
	}
				
}
	
	
	

