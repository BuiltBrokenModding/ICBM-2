package icbm.core.base;

import net.minecraft.nbt.NBTTagCompound;
import icbm.api.ITier;

/** @author DarkGuardsman */
public class Modular implements ITier
{
    protected String name = "";
    protected int tier;

    public Modular(String name, int tier)
    {
        this.name = name;
        this.tier = tier;
    }

    @Override
    public int getTier()
    {
        return tier;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }

    public Modular setName(String name)
    {
        this.name = name;
        return this;
    }

    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return nbt;
    }
}
