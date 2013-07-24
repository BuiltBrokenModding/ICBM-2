package icbm.core.base;

import icbm.api.IBlockFrequency;
import net.minecraft.nbt.NBTTagCompound;
import universalelectricity.compatibility.TileEntityUniversalElectrical;

public abstract class TShengBuo extends TileEntityUniversalElectrical implements IBlockFrequency
{
	private int shengBuo = 0;

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

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.shengBuo = nbt.getInteger("shengBuo");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("shengBuo", this.shengBuo);
	}
}
