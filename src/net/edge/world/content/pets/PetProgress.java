package net.edge.world.content.pets;

public final class PetProgress {
	
	private final PetData data;
	
	private double hunger;
	
	private double growth;
	
	public PetProgress(PetData data) {
		this.data = data;
	}
	
	public double getHunger() {
		return hunger;
	}
	
	public void setHunger(double hunger) {
		this.hunger = hunger;
	}
	
	public void updateHunger() {
		this.hunger += this.data.getStage() == 0 ? 0.025 : 0.018;
		if(this.hunger < 0.0) {
			this.hunger = 0.0;
		} else if(growth > 100.0) {
			this.hunger = 100.0;
		}
	}
	
	public double getGrowth() {
		return growth;
	}
	
	public void addGrowth(double amount) {
		this.growth += amount;
		if(this.growth < 0.0) {
			this.growth = 0.0;
		} else if(growth > 100.0) {
			this.growth = 100.0;
		}
	}
	
	public void updateGrowth() {
		this.growth += this.data.getType().getGrowthRate();
		if(this.growth < 0.0) {
			this.growth = 0.0;
		} else if(growth > 100.0) {
			this.growth = 100.0;
		}
	}
	
	public PetData getData() {
		return data;
	}
	
}
