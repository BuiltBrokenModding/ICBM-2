package icbm.zhapin.baozha.ex;

import icbm.zhapin.baozha.EBaoZha;
import icbm.zhapin.baozha.thr.ThrSheXian;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public abstract class ExThr extends ZhaPin
{
	protected ExThr(String mingZi, int ID, int tier)
	{
		super(mingZi, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		EBaoZha source = (EBaoZha) explosionSource;

		if (!worldObj.isRemote && source.dataList1.size() > 0 && source.dataList1.getZhaPin(0) instanceof ThrSheXian)
		{
			ThrSheXian thread = (ThrSheXian) source.dataList1.getZhaPin(0);

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
