package icbm.explosion.missile.modular;

import icbm.api.ILauncherContainer;
import icbm.api.IMissile;
import icbm.api.IMissileLockable;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.api.sentry.IAATarget;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityModularMissile extends Entity implements IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, IAATarget
{
    protected float currentHealt;
    protected ModuleContainerMissile missileDesign;
    protected Vector3 startPos, targetPos, launcherPos;

    public EntityModularMissile(World par1World)
    {
        super(par1World);
        this.setSize(1F, 1F);
        this.renderDistanceWeight = 3;
        this.isImmuneToFire = true;
        this.ignoreFrustumCheck = true;
    }

    /** Spawns a traditional missile and cruise missiles
     * 
     * @param explosiveId - Explosive ID
     * @param startPos - Starting Position
     * @param launcherPos - Missile Launcher Position */
    public EntityModularMissile(World world, Vector3 startPos, Vector3 launcherPos)
    {
        this(world);
        this.startPos = startPos;
        this.launcherPos = launcherPos;

        this.setPosition(this.startPos.x, this.startPos.y, this.startPos.z);
        this.setRotation(0, 90);
    }

    /** For rocket launchers
     * 
     * @param explosiveId - Explosive ID
     * @param startPos - Starting Position
     * @param targetVector - Target Position */
    public EntityModularMissile(World world, Vector3 startPos, float yaw, float pitch)
    {
        this(world);
        this.launcherPos = this.startPos = startPos;

        this.setPosition(this.startPos.x, this.startPos.y, this.startPos.z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void destroyCraft ()
    {

    }

    @Override
    public int doDamage (int damage)
    {
        return 0;
    }

    @Override
    public boolean canBeTargeted(Object entity)
    {
        // TODO later add radar damping modular to decrease targeting chance at higher ranges
        // Possible take this as far as real world conditions were weather, and light effect visual
        // targeting, in which a radar damping setup would make a missile get closer before being
        // seen
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
        // TODO drop modular parts with chance depending on total hp left, and damage to missile

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
