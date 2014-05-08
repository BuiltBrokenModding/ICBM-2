package icbm.sentry.items.weapons;

import net.minecraft.item.ItemStack;

public class AmmoHandler {

	private ItemStack weaponStack;
	private int capacity;
	public int currentAmmo = capacity;
	
	public AmmoHandler(ItemStack stack, int capacity) {
		this.capacity = capacity;
		this.weaponStack = stack;		
	}
	
	public boolean isEmpty() {
		return currentAmmo == 0;
	}
	
	public void reload() {
		this.currentAmmo = capacity;
	}
	
	public void consume(int amt) {
		if(amt < 0) {
			return;
		}
		if(amt > capacity) {
			this.currentAmmo = 0;
			return;
		}
		this.currentAmmo -= amt;
	}
	
	
	public void readFromTag() {
		this.capacity = weaponStack.stackTagCompound.getInteger("clipCapacity");
		this.currentAmmo = weaponStack.stackTagCompound.getInteger("clipCurrentAmmo");
	}
	
	public void writeToTag() {
		weaponStack.stackTagCompound.setInteger("clipCapacity", capacity);
		weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", currentAmmo);
	}
}
