package icbm.explosion.missile;

import net.minecraft.nbt.NBTTagCompound;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.core.base.Modular;

/** @author DarkGuardsman */
public class ModularWarhead extends Modular implements IExplosiveContainer
{

    protected NBTTagCompound nbtTagCompound;
    protected IExplosive explosive;

    public ModularWarhead(String name, int tier, IExplosive bomb)
    {
        super(name, tier);
        this.explosive = bomb;
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        if (this.nbtTagCompound == null)
        {
            this.nbtTagCompound = new NBTTagCompound();
        }
        return this.nbtTagCompound;
    }

    @Override
    public IExplosive getExplosiveType()
    {
        return this.explosive;
    }

}
