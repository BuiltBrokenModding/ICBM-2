package icbm.zhapin.zhapin.ex;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import icbm.zhapin.zhapin.EZhaPin;
import icbm.zhapin.zhapin.ZhaPin;

public abstract class ExThr extends ZhaPin
{
	protected ExThr(String mingZi, int ID, int tier)
	{
		super(mingZi, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		EZhaPin source = (EZhaPin) explosionSource;

		if (!worldObj.isRemote && source.dataList1.size() > 0 && source.dataList1.get(0) instanceof ThrSheXian)
		{
			ThrSheXian thread = (ThrSheXian) source.dataList1.get(0);

			if (thread.isComplete)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	protected int proceduralInterval()
	{
		return 1;
	}
}
