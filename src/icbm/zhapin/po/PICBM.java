package icbm.zhapin.po;

import icbm.core.ZhuYaoBase;
import universalelectricity.prefab.potion.CustomPotion;

public abstract class PICBM extends CustomPotion
{
	public PICBM(int id, boolean isBadEffect, int color, String name)
	{
		super(ZhuYaoBase.CONFIGURATION.get("Potion ID", name, id).getInt(id), isBadEffect, color, name);
	}
}
