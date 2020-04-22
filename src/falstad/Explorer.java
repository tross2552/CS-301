package falstad;

import java.util.ArrayList;
import java.util.Arrays;

import falstad.Robot.Direction;
import falstad.Robot.Turn;

public class Explorer extends ManualDriver {
	
	Pivot currentPivot;
	ArrayList<Pivot> pivots;
	
	public Explorer() {
		super();
	}
	
	public Explorer(MazeController controller) {
		super(controller);
	}
	
	@Override
	public boolean drive2Exit() throws Exception {
		
		//move to first pivot
		
		//loop:
			//set direction: room or hall?
				//if hall, set direction to go
				//if room, move to another door in the room, then set direction to go
			//after set direction, follow path
				//new path or previously explored?
		
		//if see exit at ANY point, turn and leave
		
		return r.mazeControl.isOutside(r.mazeControl.getCurrentPosition()[0], r.mazeControl.getCurrentPosition()[1]);
	}
	
	protected boolean followPath(Pivot nextPivot) {
		
		assert(!isAtCurrentPivot());

		Turn turn=null;
		
		do {
			int moveDist=r.distanceToObstacle(Direction.FORWARD);
			
			
			for(int i=0; i<moveDist-1;i++) {
				
				//if don't know where the next pivot will be, need to use sensors
				boolean flag = (nextPivot==null) ? (canGoLeft()||canGoRight()) : (isAtPivot(nextPivot));
				
				if(flag) return true;
				r.move(1, false);
			}
			
			boolean flag = (nextPivot==null) ? (canGoLeft() && canGoRight()) : isAtPivot(nextPivot);
			if(flag) return true;
			
			//TODO: remove redundant wall-senses
			if(canGoLeft()) turn=Turn.LEFT;
			else if(canGoRight())turn=Turn.RIGHT;
			else turn=Turn.AROUND;
			
		
		} while(turn!=Turn.AROUND);
		//note to self: to move robot back to currPivot, re-call this method, passing in currPivot
		
		return false;
	}
	
	protected void searchRoom() {
		int[] left;
		int[] right;
		int[] up;
		int[] down;
		int[] roomDimension = new int[2];
		int dist;
		
		//move to a corner of the room
		r.rotate(Turn.LEFT);
		dist = r.distanceToObstacle(Direction.FORWARD);
		for(int i=0;i < dist; i++) {
			r.move(1, false);
			if(!r.isInsideRoom()) {
				r.rotate(Turn.AROUND);
				r.move(1, false);
				r.rotate(Turn.LEFT);
				break;
			}
			if(i==dist-1) r.rotate(Turn.RIGHT);
		}
		
		dist = r.distanceToObstacle(Direction.FORWARD);
		
		left = new int[dist+1];
		right = new int[dist+1];
		
		left[0]=r.distanceToObstacle(Direction.LEFT);
		right[0]=r.distanceToObstacle(Direction.RIGHT);
		
		if(dist==0) {
			roomDimension[1]=1;
			r.rotate(Turn.RIGHT);
		}
		
		
		//move up and record next values, assuming 
		for(int i=1;i <= dist; i++) {
			r.move(1, false);
			if(!r.isInsideRoom()) {
				roomDimension[1]=i;
				r.rotate(Turn.AROUND);
				r.move(1, false);
				r.rotate(Turn.LEFT);
				break;
			}
			left[i]=r.distanceToObstacle(Direction.LEFT);
			right[i]=r.distanceToObstacle(Direction.RIGHT);
			if(i==dist) {
				roomDimension[1]=i+1;
				r.rotate(Turn.RIGHT);
			}
		}
		
		dist = r.distanceToObstacle(Direction.FORWARD);
		
		up = new int[dist+1];
		down = new int[dist+1];
		
		up[0]=r.distanceToObstacle(Direction.LEFT);
		down[0]=r.distanceToObstacle(Direction.RIGHT);
		
		if(dist==0) {
			roomDimension[0]=1;
			r.rotate(Turn.RIGHT);
		}
		
		for(int i=1;i <= dist; i++) {
			r.move(1, false);
			if(!r.isInsideRoom()) {
				roomDimension[0]=i;
				r.rotate(Turn.AROUND);
				r.move(1, false);
				r.rotate(Turn.LEFT);
				break;
			}
			up[i]=r.distanceToObstacle(Direction.LEFT);
			down[i]=r.distanceToObstacle(Direction.RIGHT);
			if(i==dist) {
				roomDimension[0]=i+1;
				r.rotate(Turn.RIGHT);
			}
		}
		
		
		for(int i=0;i<roomDimension[0];i++) {
			if(up[i]!=0); //door position
			if(down[i]!=roomDimension[1]-1); //door position
		}
		for(int i=0;i<roomDimension[1];i++) {
			if(left[i]!=0); //door position
			if(right[i]!=roomDimension[0]-1); //door position
		}
	}
	
	protected void createNewPivot() {
		
	}
	
	
	protected boolean isAtCurrentPivot() {
		return isAtPivot(currentPivot);
	}
	
	protected boolean isAtPivot(Pivot pivot) {
		try {
			return isAtPivot(r.getCurrentPosition(),pivot);
		} catch (Exception e) { //keep in mind that this will also catch any null pointer exceptions 
			
			e.printStackTrace();
			return false;
		}
	}
	
	protected boolean isAtPivot(int[] coords, Pivot pivot) {
		return Arrays.equals(coords,currentPivot.position);
	}

	protected boolean canGoLeft() {//only use if actual distance doesn't matter 
		try {
			return(r.distanceToObstacle(Direction.LEFT)!=0);
		}
		catch(UnsupportedOperationException e) {
			//TODO: Turn robot based on which sensors do exist
			return false;
		}
	}
	protected boolean canGoRight() {//only use if actual distance doesn't matter 
		try {
			return(r.distanceToObstacle(Direction.RIGHT)!=0);
		}
		catch(UnsupportedOperationException e) {
			//TODO: Turn robot based on which sensors do exist
			return false;
		}
	}
	
	protected class Pivot {
		int[] position;
		ArrayList<Pivot> adjacentPivots = new ArrayList<>();
	}
}
