import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.JFrame;

public class AnimatedSolution extends JFrame
{
	public AnimatedSolution(ArrayList<State> solution)
	{
		MyCanvas c = new MyCanvas(solution);
		
	    add("Center", c);
	    
	    setSize(c.getWidth(), c.getHeight() + 20); // 20 is frame header height
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    
	    c.start();
	}
	
	class MyCanvas extends Canvas
	{
		ArrayList<State> solution; // list of states
		
		State currentState; // storing current state while looping
		
		int passengersSpeed = 500; // speed of missionaries or cannibals going in or coming out of the boat
		int boatSpeed = 10; // speed of the boat
		
		int riverWidth = 300;
		int riverHeight = 180;
		
		int groundWidth = 180;
		int groundHeight = 200;
		
		int topSpace = 200; // height from top to ground level
		int canvasPadding = 20; // space around the canvas
		int spaceBetweenBoatAndGround = 10;
		
		int boatWidth = 100;
		int boatHeight = 20;
		int boatSideWidth = 10;
		
		int personWidth = 20;
		int personHeight = 30;
		
		int personSpacing = 5;
		
		// coordinates of the boat
		int[] boatXPoints = new int[4];
		int[] boatYPoints = new int[4];
		
		Shape leftSideGround;
		Shape rightSideGround;
		Shape river;
		Polygon boat;
		
		// list of missionaries and cannibals shapes
		Rectangle2D[] missionaries = new Rectangle2D[3];
		Rectangle2D[] cannibals = new Rectangle2D[3];
		
		// list of persons on the boat
		ArrayList<Rectangle2D> personsOnBoat = new ArrayList<Rectangle2D>();
		
		public MyCanvas(ArrayList<State> solution)
		{
			this.solution = solution;
		}
		
		public void start()
		{
			setupInitialState();
			showAnimatedSolution();
		}
		
		public void setupInitialState()
		{
			// creating static object like ground and river
		    leftSideGround = new Rectangle2D.Double(canvasPadding, topSpace, groundWidth, groundHeight);
		    rightSideGround = new Rectangle2D.Double(canvasPadding + groundWidth + riverWidth, topSpace, groundWidth, groundHeight);
		    river = new Rectangle2D.Double(canvasPadding + groundWidth, topSpace + (groundHeight - riverHeight), riverWidth, riverHeight);
		    
		    // moving boat to left at the beginning
		    this.moveBoatToLeft();
		    
		    this.currentState = this.solution.get(0);
		    
		    // initializing cannibals and missionaries shapes
		    for(int i = 0; i < 3; i++)
		    {
		    	cannibals[i] = new Rectangle2D.Double(0, 0, 0 ,0);
		    	missionaries[i] = new Rectangle2D.Double(0, 0, 0, 0);
		    }
		    
		    // setting initial positions
		    this.setLeftMissionariesAndCannibalsPositions();
		}
		
		/**
		 * loop through the states and display solution in frames
		 */
		private void showAnimatedSolution()
		{
			for(int i = 1; i < this.solution.size(); i++)
			{
				this.currentState = this.solution.get(i);
				
				if(this.currentState.isBoatLeftSide)
					this.setRightMissionariesAndCannibalsPositions();
				else
					this.setLeftMissionariesAndCannibalsPositions();
				
				this.choosePersonsToBeOnBoat();
				this.setPersonsInTheBoat();
				
				repaint();
				
				this.pauseAnimation(this.passengersSpeed);
				this.moveBoatAndTravelersToOtherSide();
				
				if(this.currentState.isBoatLeftSide)
					this.setLeftMissionariesAndCannibalsPositions();
				else
					this.setRightMissionariesAndCannibalsPositions();
				
				repaint();
				
				this.pauseAnimation(this.passengersSpeed);
			}
		}
		
		private void setLeftMissionariesAndCannibalsPositions()
		{	
			this.setLeftMissionariesPosition();
			this.setLeftCannibalsPosition();
		}
		
		/**
		 * sets the positions of cannibals standing on the left ground
		 */
		private void setLeftCannibalsPosition()
		{
			int x;
			int y = topSpace - personHeight;
			
			for(int i = 0; i < this.currentState.numCannibalsLeft; i++)
			{
				x = canvasPadding + groundWidth - (personSpacing + personWidth) * (3 - i);
				
				cannibals[i].setFrame(x, y, personWidth, personHeight);
			}
		}
		
		/**
		 * sets the positions of missionaries standing on the left ground
		 */
		private void setLeftMissionariesPosition()
		{
			int x;
			int y = topSpace - personHeight;
			int spaceOccupiedByCannibals = (personSpacing + personWidth) * 3;
			
			for(int i = 0; i < this.currentState.numMissionariesLeft; i++)
			{
				x = canvasPadding + groundWidth - (personSpacing + personWidth) * (3 - i) - spaceOccupiedByCannibals;
				
				missionaries[i].setRect(x, y, personWidth, personHeight);
			}
		}
		
		private void setRightMissionariesAndCannibalsPositions()
		{
			this.setRightCannibalsPosition();
			this.setRightMissionariesPosition();
		}
		
		/**
		 * sets the positions of cannibals standing on the right ground
		 */
		private void setRightCannibalsPosition()
		{
			int x;
			int y = topSpace - personHeight;
			int spaceOccupiedByMissionaries = (personSpacing + personWidth) * 3;
			
			for(int i = this.currentState.numCannibalsLeft; i < 3; i++)
			{
				x = canvasPadding + groundWidth + riverWidth + personSpacing + (personSpacing + personWidth) * i + spaceOccupiedByMissionaries;
				
				cannibals[i].setFrame(x, y, personWidth, personHeight);
			}
		}
		
		/**
		 * sets the positions of missionaries standing on teh right ground
		 */
		private void setRightMissionariesPosition()
		{
			int x;
			int y = topSpace - personHeight;
			
			for(int i = this.currentState.numMissionariesLeft; i < 3; i++)
			{
				x = canvasPadding + groundWidth + riverWidth + personSpacing + (personSpacing + personWidth) * i;
				
				missionaries[i].setRect(x, y, personWidth, personHeight);
			}
		}
		
		/**
		 * if the boat is on left side in a state than show it by animating
		 * do same if the boat is on right side
		 */
		private void moveBoatAndTravelersToOtherSide()
		{	
			if(this.currentState.isBoatLeftSide)
			{
				while(!this.boatArrivesLeftSide())
					this.moveBoat(-1);
			}
			else
			{
				while(!this.boatArrivesRightSide())
					this.moveBoat(1);
			}
		}
		
		/**
		 * move boat by the distance passed as argument.
		 * also move the passengers in it.
		 * @param distance
		 */
		private void moveBoat(int distance)
		{
			for(int i = 0; i < boatXPoints.length; i++)
				boatXPoints[i] += distance;
			
			boat = new Polygon(boatXPoints, boatYPoints, boatXPoints.length);
			
			this.setPersonsInTheBoat(); // changes the position of travelers according to the boat position
			
			repaint();
			
			this.pauseAnimation(this.boatSpeed);
		}
		
		/**
		 * chooses which missionaries and cannibals shape object should be taken for placing on the boat
		 * if 2 cannibals are on the left ground then third cannibal shape from cannibals array is chosen
		 */
		private void choosePersonsToBeOnBoat()
		{
			this.personsOnBoat.clear();
			
			int currentIndex;
			int numOfCannibalsInBoat = this.currentState.getNumOfCannibalsInBoat();
			int numOfMissionariesInBoat = this.currentState.getNumOfMissionariesInBoat();
			
			if(this.currentState.numCannibalsLeft < this.currentState.getParent().numCannibalsLeft)
				currentIndex = this.currentState.numCannibalsLeft;
			else
				currentIndex = this.currentState.getParent().numCannibalsLeft;
			
			for(int i = 0; i < numOfCannibalsInBoat; i++)
				personsOnBoat.add(cannibals[currentIndex++]);
			
			if(this.currentState.numMissionariesLeft < this.currentState.getParent().numMissionariesLeft)
				currentIndex = this.currentState.numMissionariesLeft;
			else
				currentIndex = this.currentState.getParent().numMissionariesLeft;
			
			for(int i = 0; i < numOfMissionariesInBoat; i++)
				personsOnBoat.add(missionaries[currentIndex ++]);
		}
		
		/**
		 * places the persons in the boat according to boat x coordinates
		 */
		private void setPersonsInTheBoat()
		{
			int x = boatXPoints[0];
			int y = boatYPoints[0] - personHeight;
			
			for(int i = 0; i < this.personsOnBoat.size(); i++)
				this.personsOnBoat.get(i).setRect(x + ((personWidth + personSpacing) * (i + 1)), y, personWidth, personHeight);
		}
		
		/**
		 * checks if the boat arrives near the left ground
		 * used when moving boat
		 * @return
		 */
		private boolean boatArrivesLeftSide()
		{
			return (boatXPoints[0] == (canvasPadding + groundWidth + spaceBetweenBoatAndGround));
		}
		
		/**
		 * checks if the boat arrives near the right ground
		 * @return
		 */
		private boolean boatArrivesRightSide()
		{
			return (boatXPoints[1] == (canvasPadding + groundWidth + riverWidth - spaceBetweenBoatAndGround));
		}
		
		/**
		 * moves the boat near the left ground
		 */
		private void moveBoatToLeft()
		{
		    boatXPoints[0] = canvasPadding + groundWidth + spaceBetweenBoatAndGround;
		    boatXPoints[1] = boatXPoints[0] + boatWidth;
		    boatXPoints[2] = boatXPoints[1] - boatSideWidth;
		    boatXPoints[3] = boatXPoints[0] + boatSideWidth; 
		    
		    boatYPoints[0] = topSpace + (groundHeight - riverHeight) - boatHeight;
		    boatYPoints[1] = boatYPoints[0];
		    boatYPoints[2] = boatYPoints[0] + boatHeight;
		    boatYPoints[3] = boatYPoints[2];
		    
		    boat = new Polygon(boatXPoints, boatYPoints, boatXPoints.length);
		}
		
		/**
		 * dynamically calculate the width of the window frame
		 */
		public int getWidth()
		{
			return canvasPadding * 2 + groundWidth * 2 + riverWidth;
		}
		
		/**
		 * dynamically calculate the height of the window frame
		 */
		public int getHeight()
		{
			return topSpace + groundHeight + canvasPadding;
		}
		
		/**
		 * painting the canvas
		 */
	    public void paint(Graphics graphics)
	    {
	    	setBackground(Color.white);
	    	
	    	Graphics2D g = (Graphics2D) graphics;
	      
	      	g.setPaint(new Color(90, 141, 73)); // set light green color for drawing ground
	      
	      	g.fill(leftSideGround); // draw left ground
	      	g.fill(rightSideGround); // draw right ground
	      	
	      	g.setPaint(new Color(155, 204, 255)); // set light blue color for drawing river
	      	
	      	g.fill(river); // draw river
	      	
	      	g.setPaint(new Color(177, 94, 0)); // set light brown color for drawing boat
	      	
	      	g.fill(boat);
	      	
	      	g.setPaint(new Color(28, 143, 202)); // set light blue color for drawing missionaries
	      	
	      	for(int i = 0; i < 3; i++)
	      		g.fill(missionaries[i]);

	      	g.setPaint(new Color(210, 42, 42)); // set light red color for drawing cannibals
	      	
	      	for(int i = 0; i < 3; i++)
	      		g.fill(cannibals[i]);
	    }
	    
	    /**
	     * for pausing the animation
	     * @param time - in milliseconds
	     */
	    public void pauseAnimation(int time)
	    {
	    	try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	  }
}
