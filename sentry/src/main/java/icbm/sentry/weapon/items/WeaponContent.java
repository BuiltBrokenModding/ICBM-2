package icbm.sentry.weapon.items;

public class WeaponContent {

	private int capacity, damage, cooldown;
	private double inaccuracy;
	private String soundname;
	
	public WeaponContent(int capacity, int damage, double inaccuracy, int cooldown, String soundname) {
		this.capacity = capacity;
		this.damage = damage;
		this.inaccuracy = inaccuracy;
		this.cooldown = cooldown;
		this.soundname = soundname;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public double getInaccuracy() {
		return inaccuracy;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public String getSoundname() {
		return soundname;
	}
}
