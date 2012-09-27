package icbm.extend;

import icbm.api.Launcher.ILauncher;
import icbm.jiqi.FaSheQiGuanLi;
import universalelectricity.prefab.TileEntityElectricityReceiver;
import universalelectricity.prefab.Vector3;

public abstract class TFaSheQi extends TileEntityElectricityReceiver implements ILauncher
{
	protected Vector3 muBiao = null;
	
	protected short shengBuo = 0;
	
	protected double dianXiaoShi = 0;
	
	public TFaSheQi()
	{
		super();
		FaSheQiGuanLi.jiaFaSheQi(this);
	}
	
	@Override
	public Vector3 getTarget()
	{
		if(this.muBiao == null)
		{
			this.muBiao = new Vector3(this.xCoord, this.yCoord, this.zCoord);
		}
		
		return this.muBiao;
	}
	
	@Override
	public void setTarget(Vector3 target)
	{
		this.muBiao = target;
	}
	
	@Override
	public short getFrequency(Object... data)
	{
		return this.shengBuo;
	}

	@Override
	public void setFrequency(short frequency, Object... data)
	{
		this.shengBuo = frequency;
	}
	
	@Override
	public double getWattHours(Object... data)
	{
		return this.dianXiaoShi;
	}

	@Override
	public void setWattHours(double wattHours, Object... data)
	{
		this.dianXiaoShi = Math.max(Math.min(wattHours, this.getMaxWattHours()), 0);
	}
}
