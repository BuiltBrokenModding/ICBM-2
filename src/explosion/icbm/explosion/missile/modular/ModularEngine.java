package icbm.explosion.missile.modular;

import net.minecraft.nbt.NBTTagCompound;
import icbm.core.base.Modular;

/** @author DarkGuardsman */
public class ModularEngine extends Modular
{

    protected float acceleration, maxSpeed;

    public ModularEngine(String name, int tier)
    {
        super(name, tier);
    }

    public float getAcceleration()
    {
        return this.acceleration;
    }

    public float getMaxspeed()
    {
        return this.maxSpeed;
    }

    public ModularEngine setAcceleration(float acceleration)
    {
        this.acceleration = acceleration;
        return this;
    }

    public ModularEngine setMaxspeed(float maxSpeed)
    {
        this.maxSpeed = maxSpeed;
        return this;
    }

    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setFloat("acceleration", this.acceleration);
        nbt.setFloat("maxSpeed", this.maxSpeed);
        return nbt;
    }

    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.acceleration = nbt.getFloat("acceleration");
        this.maxSpeed = nbt.getFloat("maxSpeed");
    }
}
