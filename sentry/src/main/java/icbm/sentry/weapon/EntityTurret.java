package icbm.sentry.weapon;

import icbm.sentry.interfaces.ISentryTrait;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.interfaces.ITurretProvider;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.api.vector.IVector3;
import universalelectricity.api.vector.Vector3;

public class EntityTurret implements ITurret
{
    Entity entity;
    
    public EntityTurret(Entity entity)
    {
        this.entity = entity;
    }
    
    @Override
    public void save(NBTTagCompound nbt)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public World world()
    {
        return entity.worldObj;
    }

    @Override
    public double z()
    {
        return entity.posZ;
    }

    @Override
    public double x()
    {
        return entity.posX;
    }

    @Override
    public double y()
    {
        return entity.posY;
    }

    @Override
    public double yaw()
    {
        return entity.rotationYaw;
    }

    @Override
    public double pitch()
    {
        return entity.rotationPitch;
    }

    @Override
    public double roll()
    {
        return 0;
    }

    @Override
    public void update()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean fire(IVector3 target)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean fire(Entity target)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onSettingsChanged()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onInventoryChanged()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ITurretProvider getHost()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3 getWeaponOffset()
    {
        return new Vector3(yaw(), pitch()).scale(1);
    }

    @Override
    public Vector3 fromCenter()
    {
        return Vector3.fromCenter(entity);
    }

    @Override
    public HashMap<String, Double> upgrades()
    {
        return null;
    }

    @Override
    public double getUpgradeEffect(String upgrade)
    {
        return 0;
    }

    @Override
    public HashMap<String, ISentryTrait> traits()
    {
        return null;
    }

    @Override
    public ISentryTrait getTrait(String trait)
    {
        return null;
    }

}
