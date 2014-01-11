package icbm.explosion.missile.modular;

import net.minecraft.item.Item;

/**
 * Container item that hold the module and info about it
 * 
 * @author DarkGuardsman
 */
public class ItemModular extends Item
{
	public ItemModular(int par1)
	{
		super(par1);
	}

	public static enum MissilePart
	{
		SolidEngine("Solid Fuel Engine", "Earlier tier engine that uses solid fuel to achieve trust", new ModuleMissileEngine("Solid", 1).setMaxspeed(222).setAcceleration(10).setMass(260)),
		SteelBody(), BasicWarhead();
		String name;
		String description;
		ModuleMissileBase module;

		private MissilePart()
		{

		}

		private MissilePart(String name, String description, ModuleMissileBase module)
		{
			this.name = name;
			this.description = description;
			this.module = module;
		}
	}
}
