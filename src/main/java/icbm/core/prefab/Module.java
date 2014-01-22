package icbm.core.prefab;

import icbm.api.IModuleContainer;
import icbm.api.ITier;
import icbm.api.sentry.IICBMModule;
import net.minecraft.nbt.NBTTagCompound;

/** Prefab for ICBM module parts uses in the creation, modification, or design of a more complex
 * object.
 * 
 * @author DarkGuardsman */
public abstract class Module implements ITier, IICBMModule
{
    protected String name = "";
    protected int tier;

    public Module(String name, int tier)
    {
        this.name = name;
        this.tier = tier;
    }

    @Override
    public int getTier()
    {
        return tier;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }

    public IICBMModule setName(String name)
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
        // TODO make a tileEntity like loader to remove issues with different classes
        this.tier = nbt.getByte("tier");
        this.name = nbt.getString("Name");
    }

    @Override
    public boolean canBeUsedIn(IModuleContainer container)
    {
        return true;
    }

}
