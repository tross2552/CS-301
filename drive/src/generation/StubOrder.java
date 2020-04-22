package generation;

import generation.Order.Builder;

/**
 * This is a stub class created to directly manipulate order fields for the sake of testing.
 * It simulates the behavior of MazeController when passed into MazeFactory. 
 * @author  Meg Anderson and Trenten Ross
 * @version 2.0
 * @since   2017-09-23
 */

public class StubOrder implements Order {
	
	public MazeConfiguration mazeConfig;
	public int skill;
	public Builder builder;
	public boolean perfect;
	public Factory factory;
	public boolean deterministic;
	
	public StubOrder() {
		mazeConfig = new MazeContainer();
		factory = new MazeFactory();
	}
	
	public StubOrder(Builder b, int s, boolean p, boolean d) {
		this.setBuilder(b);
		this.setSkillLevel(s);
		this.setPerfect(p); 
		this.setDeterministic(d);
	}
	
	/**
	 * based on current field values, generate
	 * a new maze
	 */
	public MazeConfiguration order() {
		factory.order(this);
		factory.waitTillDelivered();
		return mazeConfig;
		
	}

	@Override
	public int getSkillLevel() {
		return this.skill;
	}
	
	public void setSkillLevel(int skill) {
		this.skill = skill;
	}

	@Override
	public Builder getBuilder() {
		
		return this.builder;
	}
	
	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	@Override
	public boolean isPerfect() {
		
		return this.perfect;
	}
	
	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
	}
	
	
	public boolean isDeterministic() {
		
		return this.deterministic;
	}
	
	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
		factory = new MazeFactory(deterministic);
	}
	
	
	
	public MazeConfiguration getMazeConfig() {
		return mazeConfig;
	}
	
	@Override
	public void deliver(MazeConfiguration mazeConfig) {
		
		this.mazeConfig = mazeConfig ;

	}


	@Override
	public void updateProgress(int percentage) {
		
		//don't need anything here, just have to satisfy compiler
	}
	

	
	

}
