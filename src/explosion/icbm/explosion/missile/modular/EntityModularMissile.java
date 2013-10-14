package icbm.explosion.missile.modular;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import icbm.api.ILauncherContainer;
import icbm.api.IMissile;
import icbm.api.IMissileLockable;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.api.sentry.IAATarget;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityModularMissile extends Entity implements IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, IAATarget
{
    /** Missile warhead */
    ModularWarhead warhead;
    /** Missile casing */
    ModularCasing casing;
    /** Missile engine */
    ModularEngine engine;
    float health;

    public EntityModularMissile(World par1World)
    {
        super(par1World);
    }

    public EntityModularMissile(World world, Vector3 spawnPoint, ModularWarhead warhead, ModularCasing casing, ModularEngine engine)
    {
        this(world);
        this.setLocationAndAngles(spawnPoint.x, spawnPoint.y, spawnPoint.z, 0, 0);
        this.warhead = warhead;
        this.casing = casing;
        this.engine = engine;
        this.health = casing.health;
    }

    @Override
    public void destroyCraft()
    {
        this.explode();
    }

    @Override
    public int doDamage(int damage)
    {
        return;
    }

    @Override
    public boolean canBeTargeted(Object entity)
    {
        //TODO later add radar damping modular to decrase targeting chance
        return true;
    }

    @Override
    public void explode()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExplode()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void normalExplode()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNormalExplode()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void dropMissileAsItem()
    {
        //TODO drop modular parts with chance depending on total hp left, and damage to missile

    }

    @Override
    public int getTicksInAir()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ILauncherContainer getLauncher()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void launch(Vector3 target)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void launch(Vector3 target, int height)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeSpawnData(ByteArrayDataOutput data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void readSpawnData(ByteArrayDataInput data)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IExplosive getExplosiveType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canLock(IMissile missile)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Vector3 getPredictedPosition(int ticks)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void entityInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        // TODO Auto-generated method stub

    }

}
