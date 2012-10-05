package atomicscience.api;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;

public abstract class Poison
{	
	public static Poison[] list = new Poison[32];
	
	String name;
	
	public Poison(String name, int id)
	{
		this.name = name;
		
		if(list == null)
		{
			list = new Poison[32];
		}
		
		list[0] = this;
	}
	
	/**
	 * Called to poison this specific entity with this specific type of poison
	 * @param entity
	 */
	public void poisonEntity(EntityLiving entity, int amplifier)
	{
		int protectiveArmor = 0;
		
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer entityPlayer = (EntityPlayer)entity;
			
			for(int i = 0; i < entityPlayer.inventory.armorInventory.length; i ++)
			{
				if(entityPlayer.inventory.armorInventory[i]  != null)
				{
					if(entityPlayer.inventory.armorInventory[i].getItem() instanceof IAntiPoisonArmor)
					{
						if(((IAntiPoisonArmor)entityPlayer.inventory.armorInventory[i].getItem()).isProtectedFromPoison(this))
						{
							protectiveArmor ++;
						}
					}
				}
			}
		}
		
		if(protectiveArmor < 4)
		{
			this.doPoisonEntity(entity, amplifier);
		}
	}
	
	public void poisonEntity(EntityLiving entity)
	{
		this.poisonEntity(entity, 0);
	}
	
	protected abstract void doPoisonEntity(EntityLiving entity, int amplifier);
}