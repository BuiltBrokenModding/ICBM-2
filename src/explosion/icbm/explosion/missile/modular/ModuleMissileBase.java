package icbm.explosion.missile.modular;

import icbm.api.explosion.IMissileModule;
import icbm.core.base.Module;
import icbm.core.implement.IModuleContainer;

public abstract class ModuleMissileBase extends Module implements IMissileModule
{
    float mass = 1;

    public ModuleMissileBase(String name, int tier)
    {
        super(name, tier);
    }

    @Override
    public void init(IModuleContainer missile)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(IModuleContainer container)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean shouldUpdate()
    {
        return false;
    }

    @Override
    public float getMass()
    {
        return this.mass;
    }

    public void setMass(float kilograms)
    {
        this.mass = kilograms;
    }

}
