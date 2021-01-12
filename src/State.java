import java.util.ArrayList;

public class State
{
	public int numCannibalsLeft; // to store number of cannibals on the left side
	public int numMissionariesLeft; // to store number of missionaries on the left side
	public boolean isBoatLeftSide; // check if the boat is on left side
	
	private State parent = null; // parent node/state
	private int level = 0; // depth of states
	
	public State(int numCannibalsLeft, int numMissionariesLeft, boolean isBoatLeftSide, State parent)
	{
		this.numCannibalsLeft = numCannibalsLeft;
		this.numMissionariesLeft = numMissionariesLeft;
		this.isBoatLeftSide = isBoatLeftSide;
		this.parent = parent;
	}
	
	/**
	 * sets parent node of current state
	 * @param parent
	 */
	public void setParent(State parent)
	{
		this.parent = parent;
	}
	
	public State getParent()
	{
		return this.parent;
	}
	
	public void incrementLevel()
	{
		this.level++;
	}
	
	public int getLevel()
	{
		return this.level;
	}
	
	public boolean boatHasCannibals()
	{
		if(this.parent == null)
			return false;
		
		return (this.parent.numCannibalsLeft != this.numCannibalsLeft);
	}
	
	public int getNumOfCannibalsInBoat()
	{
		return Math.abs(this.parent.numCannibalsLeft - this.numCannibalsLeft);
	}
	
	public boolean boatHasMissionaries()
	{
		if(this.parent == null)
			return false;
		
		return (this.parent.numMissionariesLeft != this.numCannibalsLeft);
	}
	
	public int getNumOfMissionariesInBoat()
	{
		return Math.abs(this.parent.numMissionariesLeft - this.numMissionariesLeft);
	}
	
	public int getNumOfCannibalsOnRight()
	{
		return 3 - this.numCannibalsLeft;
	}
	
	public int getNumOfMissionariesOnRight()
	{
		return 3 - this.numMissionariesLeft;
	}

	/**
	 * checks if current state is valid i.e. if cannibals is greater than missionaries on any of the sides
	 * @return
	 */
	public boolean isValidState()
	{
		int numCannibalsright = this.getNumOfCannibalsOnRight();
		int numMissionariesRight = this.getNumOfMissionariesOnRight();
		
		if(this.numCannibalsLeft > 3 || this.numCannibalsLeft < 0 || this.numMissionariesLeft > 3 || this.numMissionariesLeft < 0)
			return false;
		
		if(this.numMissionariesLeft != 0 && this.numCannibalsLeft > this.numMissionariesLeft)
			return false;

		if(numMissionariesRight != 0 && numCannibalsright > numMissionariesRight)
			return false;
		
		return true;
	}
	
	/**
	 * checks if current state is the final state. If all cannibals and missionaries are on right side
	 * @return
	 */
	public boolean isGoal()
	{
		return (this.numCannibalsLeft == 0 && this.numMissionariesLeft == 0 && !this.isBoatLeftSide);
	}
	
	/**
	 * compares two states and checks if they are same
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof State))
			return false;
		
		State y = (State) obj;
		
		return (this.numCannibalsLeft == y.numCannibalsLeft &&
				this.numMissionariesLeft == y.numMissionariesLeft &&
				this.isBoatLeftSide == y.isBoatLeftSide);
	}

	/**
	 * returns possible child states/moves of this state
	 * @return
	 */
	public ArrayList<State> children()
	{
		boolean leftSide = true;
		boolean rightSide = false;
		
		ArrayList <State> children = new ArrayList <State>();
		
		if(this.isBoatLeftSide)
		{
			this.validateAndAddChild(children, new State(this.numCannibalsLeft - 2, this.numMissionariesLeft, rightSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft, this.numMissionariesLeft - 2, rightSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft - 1, this.numMissionariesLeft - 1, rightSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft - 1, this.numMissionariesLeft, rightSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft, this.numMissionariesLeft - 1, rightSide, this));
		}
		else
		{
			this.validateAndAddChild(children, new State(this.numCannibalsLeft + 2, this.numMissionariesLeft, leftSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft, this.numMissionariesLeft + 2, leftSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft + 1, this.numMissionariesLeft + 1, leftSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft + 1, this.numMissionariesLeft, leftSide, this));
			this.validateAndAddChild(children, new State(this.numCannibalsLeft, this.numMissionariesLeft + 1, leftSide, this));
		}

		return children;
	}
	
	/**
	 * filters child states removing invalid states where missionaries is less than cannibals
	 * @param childrenReference
	 * @param child
	 */
	private void validateAndAddChild(ArrayList<State> childrenReference, State child)
	{
		if(child.isValidState())
			childrenReference.add(child);
		
	}
	
	/**
	 * visual representation of current state
	 */
	public String toString()
	{
		String graphic = "";
		int numCannibalsright = 3 - this.numCannibalsLeft;
		int numMissionariesRight = 3 - this.numMissionariesLeft;
		
		for(int i = 0; i < this.numCannibalsLeft; i++)
			graphic += this.getCannibalShape();
		
		graphic += this.getWhitespaceForFilling(this.numCannibalsLeft) + " ";
		
		for(int i = 0; i < this.numMissionariesLeft; i++)
			graphic += this.getMissionariesShape();

		graphic += this.getWhitespaceForFilling(this.numMissionariesLeft) + " | ";
		
		if(this.isBoatLeftSide)
			graphic += this.getBoatShape();
		else
			graphic += this.getWhiteSpaceForNoBoat();
		
		if(!this.isBoatLeftSide)
			graphic += this.getBoatShape();
		else
			graphic += this.getWhiteSpaceForNoBoat();
		
		graphic += " |  ";

		for(int i = 0; i < numCannibalsright; i++)
			graphic += this.getCannibalShape();
		
		graphic += this.getWhitespaceForFilling(numCannibalsright) + " ";
		
		for(int i = 0; i < numMissionariesRight; i++)
			graphic += this.getMissionariesShape();
		
		return graphic;
	}
	
	/**
	 * show empty spaces if some cannibals or missionaries are on other side. it is done to align the river in same position
	 * @param persons
	 * @return
	 */
	private String getWhitespaceForFilling(int persons)
	{
		String whiteSpaces = "";
		
		for(int i = persons; i <= 3; i++)
			whiteSpaces += " ";
		
		return whiteSpaces;
	}
	
	/**
	 * show empty spaces in left side if the boat is on other side and vice versa. It is used for aligning the river in same position
	 * @return
	 */
	private String getWhiteSpaceForNoBoat()
	{
		return "        ";
	}

	private String getBoatShape()
	{
		return " \\____/ ";
	}
	
	private String getCannibalShape()
	{
		return "C";
	}
	
	private String getMissionariesShape()
	{
		return "M";
	}
}
