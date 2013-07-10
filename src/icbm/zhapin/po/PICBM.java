package icbm.zhapin.po;

import icbm.core.ZhuYaoICBM;
import calclavia.lib.CustomPotion;

public abstract class PICBM extends CustomPotion
{
	public PICBM(int id, boolean isBadEffect, int color, String name)
	{
		super(ZhuYaoICBM.CONFIGURATION.get("Potion", name, id).getInt(id), isBadEffect, color, name);
	}
}
