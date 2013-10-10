package icbm.explosion.potion;

import icbm.core.ICBMConfiguration;
import calclavia.lib.CustomPotion;

public abstract class ICBMPotion extends CustomPotion
{
	public ICBMPotion(int id, boolean isBadEffect, int color, String name)
	{
		super(ICBMConfiguration.CONFIGURATION.get("Potion", name, id).getInt(id), isBadEffect, color, name);
	}
}
