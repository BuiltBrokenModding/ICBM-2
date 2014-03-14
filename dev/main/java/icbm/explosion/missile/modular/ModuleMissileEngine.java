package icbm.explosion.missile.modular;

import calclavia.api.icbm.explosion.IMissileModule.IMissileEngine;
import net.minecraft.nbt.NBTTagCompound;

/** @author DarkGuardsman */
public class ModuleMissileEngine extends ModuleMissileBase implements IMissileEngine
{

    protected float acceleration, maxSpeed, mass = 100;

    public ModuleMissileEngine(String name, int tier)
    {
        super(name, tier);
    }

    @Override
    public float getAcceleration()
    {
        return this.acceleration;
    }

    @Override
    public float getMaxSpeed()
    {
        return this.maxSpeed;
    }

    /** Base acceleration in meters a second */
    public ModuleMissileEngine setAcceleration(float acceleration)
    {
        this.acceleration = acceleration;
        return this;
    }

    /** Max speed in meters a second */
    public ModuleMissileEngine setMaxspeed(float maxSpeed)
    {
        this.maxSpeed = maxSpeed;
        return this;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        super.save(nbt);
        nbt.setFloat("acceleration", this.acceleration);
        nbt.setFloat("maxSpeed", this.maxSpeed);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.acceleration = nbt.getFloat("acceleration");
        this.maxSpeed = nbt.getFloat("maxSpeed");
    }

    @Override
    public String getOreName()
    {
        return "MissileEngine";
    }

}
