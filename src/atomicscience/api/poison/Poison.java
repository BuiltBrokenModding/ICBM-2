package atomicscience.api.poison;

import java.util.EnumSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.core.vector.Vector3;
import atomicscience.api.IAntiPoisonArmor;

public abstract class Poison
{
	public enum ArmorType
	{
		HELM, BODY, LEGGINGS, BOOTS, UNKNOWN
	}

	public static Poison[] list = new Poison[32];

	protected String name;
	protected EnumSet<ArmorType> armorRequired = EnumSet.range(ArmorType.HELM, ArmorType.BOOTS);

	public Poison(String name, int id)
	{
		this.name = name;

		if (list == null)
		{
			list = new Poison[32];
		}

		list[0] = this;
	}

	public String getName()
	{
		return this.name;
	}

	public EnumSet<ArmorType> getArmorRequired()
	{
		return this.armorRequired;
	}

	/**
	 * Called to poison this specific entity with this specific type of poison.
	 * 
	 * @amiplifier - The amplification value.
	 * @armorRequired - The amount of pieces of armor required to be protected.
	 * @param entity
	 */
	public void poisonEntity(Vector3 emitPosition, EntityLivingBase entity, int amplifier)
	{
		EnumSet<ArmorType> armorWorn = EnumSet.of(ArmorType.UNKNOWN);

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
							armorWorn.add(((IAntiPoisonArmor) entityPlayer.inventory.armorInventory[i].getItem()).getArmorType());
						}
					}
				}
			}
		}

		if (!armorWorn.containsAll(this.armorRequired))
		{
			this.doPoisonEntity(emitPosition, entity, armorWorn, amplifier);
		}
	}

	public void poisonEntity(Vector3 emitPosition, EntityLivingBase entity)
	{
		this.poisonEntity(emitPosition, entity, 0);
	}

	protected abstract void doPoisonEntity(Vector3 emitPosition, EntityLivingBase entity, EnumSet<ArmorType> armorWorn, int amplifier);
}