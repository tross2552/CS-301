/**
 * Class-responsibility-collaboration card
 * Name: BasicRobot
 * Collaborators: 
 * 		is a Robot 
 * 		uses a MazeController
 * 		has a 	MazeController mazeControl;
 *				Direction currentDirection;
 * 				int[] currentPosition;
 *				float batteryLevel;
 *				float senseCost;
 *				float rotateCost;
 *				float moveCost;
 *				int pathLength;
 *				boolean hasStopped;
 * Responsibilities:
 * 1. Rotate the bot
 * 2. Moves the bot
 * 3. Gets and sets fields
 * 
 * General Summary: this class is a robot, that can move around, provide information 
 * on its own position, on its own battery level and distance traveled, and on various
 * obstacles which may surround it. 
 * 					
 * */

/**     [X] rotate
 * 		[X] move
 * 		[X] canSeeExit
 * 		[X] distanceToObstacle
 * 		[X] set privacy of methods and fields*/

package falstad;

import falstad.MazeController.UserInput;
import generation.CardinalDirection;

public class BasicRobot implements Robot {
	
	public MazeController mazeControl;
	protected CardinalDirection currentDirection;
	protected int[] currentPosition;
	public final float initialBatteryLevel = 3000;
	public final float senseCost = 1;
	public final float rotateCost = 3;
	public final float moveCost = 5;
	private float currentBatteryLevel;
	private int pathLength;
	public final int INFINITY = Integer.MAX_VALUE;
	private boolean stopped;
	
	/**Turn calls the KeyDown method of MazeController
	 * It has a switch statement: one case Left, which passes the key left, one case
	 * Right which passes the key right, and one case Around which passes the key left twice.
	 * It only performs these if there is sufficient battery level. It then updates the battery
	 * level, and the direction. 
	 * For left: direction = direction.rotateCounterClockwise
	 * For right: direction = direction.rotateClockwise 
	 * For around: direction = direction.oppositeDirection
	 * @param turn- (LEFT, RIGHT, AROUND)
	 * */
	
	public BasicRobot() {
		currentBatteryLevel = initialBatteryLevel;
		pathLength = 0;
		currentDirection = CardinalDirection.East;
		stopped = false;
		mazeControl = new MazeController();
	}
	
	public BasicRobot(MazeController controller) {
		currentBatteryLevel = initialBatteryLevel;
		pathLength = 0;
		currentDirection = CardinalDirection.East;
		stopped = false;
		mazeControl = controller;
	}
	
	@Override
	public void rotate(Turn turn) {
		switch(turn) {
			case LEFT:
				if (currentBatteryLevel > rotateCost) { 
					mazeControl.keyDown(UserInput.Left, 0);
					currentBatteryLevel -= rotateCost;
					currentDirection = currentDirection.rotateCounterClockwise();
				}
				else stopped = true;	
				break;
			
			case RIGHT:
				if (currentBatteryLevel > rotateCost) {
					mazeControl.keyDown(UserInput.Right, 0);
					currentBatteryLevel -= rotateCost;
					currentDirection = currentDirection.rotateClockwise();
				}
				else stopped = true;
				break;
			
			case AROUND:
				if (currentBatteryLevel > 2*rotateCost ) {
					mazeControl.keyDown(UserInput.Right, 0);
					mazeControl.keyDown(UserInput.Right, 0);
					currentDirection = currentDirection.oppositeDirection();
					currentBatteryLevel -= 2*rotateCost;
				}	
				else stopped = true;
				break;
		}
	}
	
	/**Move should call the KeyDown method of MazeController
	 * It should always call it with the parameter up, because it only can move forwards,
	 * and it calls it however many times the distance is set to. Move must also 
	 * update the currentPosition and currentBatteryLevel and the pathLength for every 
	 * call to KeyDown. If the move is illegal, such as it's hit a wall, it doesn't call KeyDown
	 * @param distance- number of steps to take 
	 * @param manual- whether the robot is manual */
	
	@Override
	public void move(int distance, boolean manual) {
		
		for (int i = 0; i < distance; i++) {
			
			if (currentBatteryLevel < moveCost) {
				stopped = true;
				break;
			}
			
			/**This always uses 1, because 1 is forwards, and this only goes forward*/
			if (mazeControl.checkMove(1)) {
				
				/**value in keyDown is used when the maze is in title, and is therefore irrelevant
				 * here. Zero was chosen arbitrarily. */
				mazeControl.keyDown(UserInput.Up, 0);
				
				pathLength ++;
				
				currentBatteryLevel -= getEnergyForStepForward();
			}
			
			else if (currentBatteryLevel - getEnergyForStepForward() < 0) {
				System.out.println("Your bot doesn't have the energy to complete a step forwards");
				break;
			}
			
			else if (!mazeControl.checkMove(1)) {
				System.out.println("Your bot's hit a wall, so it can't move anymore.");
				break;
			}
		}

	}

	@Override
	public int[] getCurrentPosition() throws Exception {
		if (!mazeControl.getMazeConfiguration().isValidPosition(currentPosition[0], currentPosition[1]))
			throw new Exception();
		return currentPosition;
				
	}

	@Override
	public void setMaze(MazeController maze) {
		this.mazeControl = maze;
	}

	/**This method tells us whether a given robot is at the exit. To accomplish this it 
	 * must go through the maze controller's maze Configuration to use Maze Configuration's
	 * cells to use their ExitPosition method on the robot's current position
	 * @return boolean true if at the exit, false if not*/
	
	@Override
	public boolean isAtExit() {
		return mazeControl.getMazeConfiguration()
				.getMazecells().isExitPosition(currentPosition[0], currentPosition[1]);
	}
	
	/**This method tells us whether the robot stares into the black void in a given direction,
	 * by using the distanceToObstacle method, which returns INFINITY if the robot looks into
	 * the exit.
	 * @param direction- relative robot direction
	 * @throws UnsupportedOperationException if no distance sensor exists in given direction
	 * @return boolean true if can see exit, false if not*/
	
	@Override
	public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {
		
		if (distanceToObstacle(direction) == INFINITY) {
			return true;
		}
		
		return false;
	}
	
	/**This method returns the distance in a given direction to an obstacle. It uses a helper method 
	 * robotToCardinalDirection in order to convert the robot's subjective direction into an 
	 * absolute cardinal direction. It sets the checkedPosition (whichever cell the sensor has 
	 * reached) to the currentPosition, and then it sets the difference in those positions from 
	 * the getDirection method in CardinalDirection. getDirection will convert the direction
	 * to a vector which we can use to increment the checkedPostion. It increments a count that
	 * acts as the distance.  
	 * @param direction
	 * @throws UnsupportedOperationException- if no distance sensor exists in given direction
	 * @return int- distance to a wall or -1 if not enough battery exists or INFINITY if the
	 * robot stares out the exit into the void. */
	
	@Override
	public int distanceToObstacle(Direction direction) {
		
		if (!hasDistanceSensor(direction)) throw new UnsupportedOperationException();
		
		//TODO: end game
		if (currentBatteryLevel < senseCost) {
			stopped = true;
			return -1; 
		}
			
		CardinalDirection cd = translateDirection(direction);
		int[] checkedPosition = currentPosition;
		int[] differenceInPositions = cd.getDirection();
		int count = 0;
		currentBatteryLevel -= senseCost;
			
		while (mazeControl.getMazeConfiguration().hasWall(checkedPosition[0], checkedPosition[1], cd)){
			
			if (mazeControl.getMazeConfiguration()
				.getMazecells().isExitPosition(currentPosition[0], currentPosition[1]))
					return INFINITY;
			checkedPosition[0] += differenceInPositions[0];
			checkedPosition[1] += differenceInPositions[1];
			count++;
		}
			
		return count;
	}
	
	/**A helper method for distanceToObstacle which converts the robot's subjective 
	 * direction into an absolute cardinal direction.
	 * @param robot direction
	 * @return cardinal direction*/
	
	protected CardinalDirection translateDirection(Direction dir) {
		switch(dir) {
		case LEFT: 
			return currentDirection.rotateCounterClockwise();
		case RIGHT:
			return currentDirection.rotateClockwise();
		case BACKWARD:
			return currentDirection.oppositeDirection();
		default:
			return currentDirection;
		}
	}
	
	/**This method tells us whether a given robot is in a room. To accomplish this it 
	 * must go through the maze controller's maze Configuration to use Maze Configuration's
	 * cells to use the cell's isInRoom method on the robot's current position
	 * @throws UnsupportedOperationException if there is no room sensor
	 * @return boolean for whether it's in a room*/
	
	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		
		if (!hasRoomSensor()) throw new UnsupportedOperationException();
		
		return mazeControl.getMazeConfiguration()
				.getMazecells().isInRoom(currentPosition[0], currentPosition[1]);
	}

	@Override
	public boolean hasRoomSensor() {
		return true;
	}

	@Override
	public CardinalDirection getCurrentDirection() {
		return currentDirection;
	}

	@Override
	public float getBatteryLevel() {
		return currentBatteryLevel;
	}

	@Override
	public void setBatteryLevel(float level) {
		assert(level > 0);
		currentBatteryLevel = level;
	}

	@Override
	public int getOdometerReading() {
		return pathLength;
	}

	@Override
	public void resetOdometer() {
		pathLength = 0;

	}

	@Override
	public float getEnergyForFullRotation() {
		return rotateCost;
	}

	@Override
	public float getEnergyForStepForward() {
		return moveCost;
	}
	
	/**If the battery level is too low to do anything, the robot has Stopped, 
	 * no other actions should be performed. 
	 * @return true is the robot can't do anything else*/
	
	@Override
	public boolean hasStopped() {
		return stopped;
	}
	
	/**So this method is lazily coded, but the project Specifications told us that a basic 
	 * robot has sensors in all directions, so this robot will always return true in all directions*/
	@Override
	public boolean hasDistanceSensor(Direction direction) {
		return true;
	}

}
