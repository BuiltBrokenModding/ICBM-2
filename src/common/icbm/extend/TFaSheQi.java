package icbm.extend;

import net.minecraft.src.ItemStack;
import icbm.jiqi.FaSheQiGuanLi;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

public abstract class TFaSheQi extends TileEntityElectricityReceiver implements IFrequency
{
	public Vector3 target = null;
	
	public TFaSheQi()
	{
		super();
		FaSheQiGuanLi.addLauncher(this);
	}
	
	public abstract void launch();

	public abstract boolean canLaunch();

	public abstract String getStatus();
}
