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
        nbt.setString("Name", this.name);
        nbt.setByte("tier", (byte) this.tier);
        return nbt;
    }

    public void load(NBTTagCompound nbt)
    {
        //TODO make a tileEntity like loader to remove issues with different classes
        this.tier = nbt.getByte("tier");
        this.name = nbt.getString("Name");
    }
}
