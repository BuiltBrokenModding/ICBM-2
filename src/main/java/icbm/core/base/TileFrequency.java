package icbm.core.base;

import calclavia.lib.terminal.TileTerminal;
import icbm.api.IBlockFrequency;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileFrequency extends TileTerminal implements IBlockFrequency
{
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
}
