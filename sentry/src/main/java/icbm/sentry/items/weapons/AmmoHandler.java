package icbm.sentry.items.weapons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AmmoHandler {

	private ItemStack weaponStack;

	public AmmoHandler(ItemStack stack, int capacity) {
		this.weaponStack = stack;		
		if(weaponStack.stackTagCompound == null) {
			weaponStack.stackTagCompound = new NBTTagCompound();
		}
		weaponStack.stackTagCompound.setInteger("clipCapacity", capacity);
		weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", 0);
	}
	
	public boolean isEmpty() {
		return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") == 0;
	}
	
	public void reload() {
		 weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", weaponStack.stackTagCompound.getInteger("clipCapacity")); 
	}
	
	public int getCurrentAmmo() {
		return weaponStack.stackTagCompound.getInteger("clipCurrentAmmo");
	}

	public void consume(int amt) {
		if(amt < 0) {
			return;
		}
		if(amt > weaponStack.stackTagCompound.getInteger("clipCapacity")) {
			weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", 0);
			return;
		}
		weaponStack.stackTagCompound.setInteger("clipCurrentAmmo", weaponStack.stackTagCompound.getInteger("clipCurrentAmmo") - amt);
	}
}
