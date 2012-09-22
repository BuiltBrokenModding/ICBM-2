package icbm.extend;

import icbm.jiqi.FaSheQiGuanLi;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

public abstract class TLauncher extends TileEntityElectricityReceiver implements IFrequency
{
	public Vector3 target = null;
	
	public TLauncher()
	{
		super();
		FaSheQiGuanLi.addLauncher(this);
	}
	
	public abstract void launch();

	public abstract boolean canLaunch();

	public abstract String getStatus();
}
