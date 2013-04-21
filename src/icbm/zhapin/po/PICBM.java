package icbm.zhapin.po;

import icbm.core.ZhuYao;
import universalelectricity.prefab.potion.CustomPotion;

public abstract class PICBM extends CustomPotion
{

	public PICBM(int id, boolean isBadEffect, int color, String name)
	{
		super(ZhuYao.CONFIGURATION.get("Potion ID", name, id).getInt(id), isBadEffect, color, name);
	}

}
