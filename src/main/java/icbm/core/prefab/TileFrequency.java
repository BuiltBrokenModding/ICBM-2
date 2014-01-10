package icbm.core.prefab;

import java.util.HashSet;

import icbm.api.IBlockFrequency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import calclavia.lib.prefab.tile.IPlayerUsing;
import calclavia.lib.prefab.tile.TileElectrical;

public abstract class TileFrequency extends TileElectrical implements IBlockFrequency, IPlayerUsing
{
	public final HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	private int frequency = 0;

	@Override
	public int getFrequency()
	{
		return this.frequency;
	}

	@Override
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
	}

	/** Reads a tile entity from NBT. */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.frequency = nbt.getInteger("shengBuo");
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger("shengBuo", this.frequency);
	}

	@Override
	public HashSet<EntityPlayer> getPlayersUsing()
	{
		return this.playersUsing;
	}
}
