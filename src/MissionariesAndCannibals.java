import java.util.ArrayList;
import java.util.Scanner;

public class MissionariesAndCannibals
{
	private ArrayList<State> openList = new ArrayList<State>(); // stores list of possible states/moves
	private ArrayList<State> closeList = new ArrayList<State>(); // stores list of visited states
	
	/**
	 * appends the new possible states to openList
	 * while appending, it checks if the state is already visited or in queue for visiting
	 * @param stateList
	 */
	private void pushStatesToOpenList(ArrayList<State> stateList)
	{
		for(int i = 0; i < stateList.size(); i++)
		{	
			if(!this.openList.contains(stateList.get(i)) || !this.closeList.contains(stateList.get(i)))
				openList.add(stateList.get(i));
		}
	}
	
	/**
	 * once the final state is found, print the results bubbling up to the root node/state
	 * @param finalState
	 */
	private void showResults(ArrayList<State> results)
	{
		for(int i = 0; i < results.size(); i++)
			System.out.println(results.get(i));
	}
	
	public ArrayList<State> getStates(State finalState)
	{
		ArrayList<State> states = new ArrayList<State>();
		
		State currentState = finalState;
		
		while(currentState.getParent() != null)
		{
			states.add(0, currentState);
			currentState = currentState.getParent();
		}
		
		states.add(0, currentState);
		
		return states;
	}
	
	/**
	 * start the program
	 */
	public void start()
	{
		State currentState = new State(3, 3, true, null);
		
		System.out.println("Cannibals and Missionaries\n");
		System.out.println("Initial State\n");
		System.out.println(currentState);
		System.out.println("\nCalculating...\n");
		
		int index = 0;
		this.openList.add(currentState);

		while(!currentState.isGoal())
		{	
			this.pushStatesToOpenList(currentState.children());
			
			// after pushing possible states/moves to openList, add current state to visited states list(closeList)
			// then remove it from the queue in openList
			this.closeList.add(currentState);
			this.openList.remove(index);
			
			currentState = this.openList.get(index); // get another state in the queue from openList
		}
		
		ArrayList<State> results = this.getStates(currentState);
		
		this.showResults(results);
		
		System.out.println("\nLoading animated solution...");

		AnimatedSolution as = new AnimatedSolution(results);
		
		as.setVisible(true);
	}

	public static void main(String[] args)
	{
		MissionariesAndCannibals program = new MissionariesAndCannibals();
		
		program.start();
	}

}
