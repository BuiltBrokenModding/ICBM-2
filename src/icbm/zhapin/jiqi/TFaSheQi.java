package icbm.zhapin.jiqi;

import calclavia.lib.TileEntityUniversalElectrical;
import icbm.api.ILauncherController;
import icbm.api.LauncherType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public abstract class TFaSheQi extends TileEntityUniversalElectrical implements ILauncherController, IPeripheral
{
	protected Vector3 muBiao = null;

	protected int shengBuo = 0;

	public TFaSheQi()
	{
		super();
		FaSheQiGuanLi.jiaFaSheQi(this);
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}

	@Override
	public Vector3 getTarget()
	{
		if (this.muBiao == null)
		{
			if (this.getLauncherType() == LauncherType.CRUISE)
			{
				this.muBiao = new Vector3(this.xCoord, this.yCoord, this.zCoord);
			}
			else
			{
				this.muBiao = new Vector3(this.xCoord, 0, this.zCoord);
			}
		}

		return this.muBiao;
	}

	@Override
	public void setTarget(Vector3 target)
	{
		this.muBiao = target.floor();
	}

	@Override
	public int getFrequency()
	{
		return this.shengBuo;
	}

	@Override
	public void setFrequency(int frequency)
	{
		this.shengBuo = frequency;
	}

	@Override
	public String getType()
	{
		return "ICBMLauncher";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[] { "launch", "getTarget", "setTarget", "canLaunch", "setFrequency", "getFrequency", "getMissile" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{
		switch (method)
		{
			case 0:
				this.launch();
				return null;
			case 1:
				return new Object[] { this.getTarget().x, this.getTarget().y, this.getTarget().z };
			case 2:
				if (arguments[0] != null && arguments[1] != null && arguments[2] != null)
				{
					try
					{
						this.setTarget(new Vector3(((Double) arguments[0]).doubleValue(), ((Double) arguments[1]).doubleValue(), ((Double) arguments[2]).doubleValue()));
					}
					catch (Exception e)
					{
						e.printStackTrace();
						throw new Exception("Target Parameter is Invalid.");
					}
				}
				return null;
			case 3:
				return new Object[] { this.canLaunch() };
			case 4:
				if (arguments[0] != null)
				{
					try
					{
						double arg = ((Double) arguments[0]).doubleValue();
						arg = Math.max(Math.min(arg, Short.MAX_VALUE), Short.MIN_VALUE);
						this.setFrequency((short) arg);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						throw new Exception("Frequency Parameter is Invalid.");
					}
				}
				return null;
			case 5:
				return new Object[] { this.getFrequency() };
			case 6:
				if (this.getMissile() != null)
				{
					return new Object[] { this.getMissile().getExplosiveType().getMissileName() };
				}
				else
				{
					return null;
				}
		}

		throw new Exception("Invalid ICBM Launcher Function.");
	}

	@Override
	public boolean canAttachToSide(int side)
	{
		return true;
	}

	@Override
	public void attach(IComputerAccess computer)
	{

	}

	@Override
	public void detach(IComputerAccess computer)
	{

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.setFrequency(nbt.getInteger("frequency"));
		this.muBiao = Vector3.readFromNBT(nbt.getCompoundTag("target"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("frequency", this.getFrequency());

		if (this.muBiao != null)
		{
			nbt.setCompoundTag("target", this.muBiao.writeToNBT(new NBTTagCompound()));
		}
	}
}
