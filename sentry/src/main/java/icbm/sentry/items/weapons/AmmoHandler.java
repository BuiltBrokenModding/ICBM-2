package icbm.sentry.items.weapons;

import net.minecraft.item.ItemStack;

public class AmmoHandler {

	private ItemStack stack;
	private int capacity;
	private int currentAmmo = 0;
	
	public AmmoHandler(ItemStack stack, int capacity) {
		this.capacity = capacity;
		this.stack = stack;
	}
	
	public void loadAmmo(int amt) {
		
		if((currentAmmo + amt) > capacity) {
			this.currentAmmo = capacity;
			return;
		}
		if(amt > capacity) {
			this.currentAmmo = capacity;
			return;
		}
		this.currentAmmo += amt;
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
		this.capacity = stack.stackTagCompound.getInteger("clipCapacity");
		this.currentAmmo = stack.stackTagCompound.getInteger("clipCurrentAmmo");
	}
	
	public void writeToTag() {
		stack.stackTagCompound.setInteger("clipCapacity", capacity);
		stack.stackTagCompound.setInteger("clipCurrentAmmo", currentAmmo);
	}
}
