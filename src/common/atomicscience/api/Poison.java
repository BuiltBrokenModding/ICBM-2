package atomicscience.api;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;

public abstract class Poison
{
	public static Poison[] list = new Poison[32];

	String name;

	public Poison(String name, int id)
	{
		this.name = name;

		if (list == null)
		{
			list = new Poison[32];
		}

		list[0] = this;
	}

	/**
	 * Called to poison this specific entity with
	 * this specific type of poison.
	 * 
	 * @amiplifier - The amplification value.
	 * @armorRequired - The amount of pieces of
	 *                armor required to be
	 *                protected.
	 * @param entity
	 */
	public void poisonEntity(EntityLiving entity, int amplifier, int armorRequired)
	{
		int protectiveArmor = 0;

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer entityPlayer = (EntityPlayer) entity;

			for (int i = 0; i < entityPlayer.inventory.armorInventory.length; i++)
			{
				if (entityPlayer.inventory.armorInventory[i] != null)
				{
					if (entityPlayer.inventory.armorInventory[i].getItem() instanceof IAntiPoisonArmor)
					{
						if (((IAntiPoisonArmor) entityPlayer.inventory.armorInventory[i].getItem()).isProtectedFromPoison(entityPlayer.inventory.armorInventory[i], entity, this))
						{
							((IAntiPoisonArmor) entityPlayer.inventory.armorInventory[i].getItem()).onProtectFromPoison(entityPlayer.inventory.armorInventory[i], entity, this);
							protectiveArmor++;
						}
					}
				}
			}
		}

		if (protectiveArmor < armorRequired)
		{
			this.doPoisonEntity(entity, amplifier);
		}
	}

	public void poisonEntity(EntityLiving entity, int amplifier)
	{
		this.poisonEntity(entity, amplifier, 4);
	}

	public void poisonEntity(EntityLiving entity)
	{
		this.poisonEntity(entity, 0);
	}

	protected abstract void doPoisonEntity(EntityLiving entity, int amplifier);
}