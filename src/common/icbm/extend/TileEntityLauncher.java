package icbm.extend;

import icbm.LauncherManager;
import universalelectricity.Vector3;
import universalelectricity.electricity.TileEntityElectricUnit;

public abstract class TileEntityLauncher extends TileEntityElectricUnit implements IFrequency
{
	public Vector3 target = null;
	
	public TileEntityLauncher()
	{
		super();
		LauncherManager.addLauncher(this);
	}
	
	public abstract void launch();

	public abstract boolean canLaunch();

	public abstract String getStatus();
}
