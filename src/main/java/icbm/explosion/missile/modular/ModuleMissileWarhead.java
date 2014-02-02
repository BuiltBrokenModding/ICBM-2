package icbm.explosion.missile.modular;

import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import net.minecraft.nbt.NBTTagCompound;

/** @author DarkGuardsman */
public class ModuleMissileWarhead extends ModuleMissileBase implements IExplosiveContainer
{

    protected NBTTagCompound nbtTagCompound;
    protected IExplosive explosive;

    public ModuleMissileWarhead(String name, int tier, IExplosive bomb)
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

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
    }

    @Override
    public String getOreName()
    {
        return "MissileWarhead";
    }

}
