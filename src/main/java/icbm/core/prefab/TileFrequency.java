package icbm.core.prefab;

import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import resonant.api.blocks.IBlockFrequency;

public abstract class TileFrequency extends TileICBM implements IBlockFrequency
{
    private int frequency = 0;

    public TileFrequency(Material material)
    {
        super(material);
    }

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
        this.frequency = nbt.getInteger("frequency");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("frequency", this.frequency);
    }
}
