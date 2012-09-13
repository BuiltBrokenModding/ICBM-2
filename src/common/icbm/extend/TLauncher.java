package icbm.extend;

import icbm.jiqi.FaSheQiGuanLi;
import universalelectricity.Vector3;
import universalelectricity.electricity.TileEntityElectricUnit;

public abstract class TLauncher extends TileEntityElectricUnit implements IFrequency
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
