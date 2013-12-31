package icbm.core.base;

import com.builtbroken.minecraft.terminal.TileEntityTerminal;

import icbm.api.IBlockFrequency;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEnityBase extends TileEntityTerminal implements IBlockFrequency
{
    private int frequencyID = 0;
    
    public TileEnityBase()
    {
        super(0, 0);
    }

    public TileEnityBase(long wattsPerTick)
    {
        super(wattsPerTick);
    }

    public TileEnityBase(long wattsPerTick, long maxEnergy)
    {
        super(wattsPerTick, maxEnergy);
    }

    @Override
    public int getFrequency()
    {
        return this.frequencyID;
    }

    @Override
    public void setFrequency(int frequency)
    {
        this.frequencyID = frequency;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.frequencyID = nbt.getInteger("shengBuo");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("shengBuo", this.frequencyID);
    }
}
