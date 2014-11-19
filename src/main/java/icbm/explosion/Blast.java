package icbm.explosion;

import icbm.content.entity.EntityMissile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import resonant.api.explosion.IExplosion;
import resonant.api.explosion.ExplosionEvent.DoExplosionEvent;
import resonant.api.explosion.ExplosionEvent.ExplosionConstructionEvent;
import resonant.api.explosion.ExplosionEvent.PostExplosionEvent;
import resonant.api.explosion.ExplosionEvent.PreExplosionEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import resonant.lib.transform.vector.IVectorWorld;
import resonant.lib.transform.vector.Vector3;
import resonant.lib.transform.vector.VectorWorld;

public abstract class Blast extends Explosion implements IExplosion, IVectorWorld
{
    public VectorWorld position;

    /** The amount of times the explosion has been called */
    protected int callCount = 0;

    public Blast(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.position = new VectorWorld(world, x, y, z);
    }

    public Blast(VectorWorld pos, Entity entity, float size)
    {
        super(pos.world(), entity, pos.x(), pos.y(), pos.z(), size);
        this.position = pos;
    }

    public Blast(Entity entity, float size)
    {
        super(entity.worldObj, entity, entity.posX, entity.posY, entity.posZ, size);
        this.position = new VectorWorld(entity);
    }

    protected void doPreExplode()
    {
    }

    /** Called before an explosion happens. */
    public final void preExplode()
    {
        PreExplosionEvent evt = new PreExplosionEvent(this.world(), this);
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled())
        {
            this.doPreExplode();
        }
    }

    /** Called every tick when this explosive is being progressed. */
    protected abstract void doExplode();

    public final void onExplode()
    {
        DoExplosionEvent evt = new DoExplosionEvent(this.world(), this);
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled())
        {
            this.doExplode();
            this.callCount++;
        }
    }

    protected void doPostExplode()
    {
    }

    /** Called after the explosion is completed. */
    public final void postExplode()
    {
        PostExplosionEvent evt = new PostExplosionEvent(this.world(), this);
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled())
        {
            this.doPostExplode();
        }
    }

    /** Make the default functions useless. */
    @Override
    public void doExplosionA()
    {
    }

    @Override
    public void doExplosionB(boolean par1)
    {
    }

    /** All outside classes should call this. */
    @Override
    public void explode()
    {
        ExplosionConstructionEvent evt = new ExplosionConstructionEvent(this.world(), this);
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled())
        {
            if (this.proceduralInterval() > 0)
            {
                if (!this.world().isRemote)
                {
                    //this.world().spawnEntityInWorld(new EntityExplosion(this));
                }
            }
            else
            {
                this.doPreExplode();
                this.doExplode();
                this.doPostExplode();
            }
        }
    }

    public int countIncrement()
    {
        return 1;
    }

    @Override
    public float getRadius()
    {
        return this.explosionSize;
    }

    @Override
    public long getEnergy()
    {
        return 0;
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @return - Return -1 if this explosive does not need procedural calls */
    public int proceduralInterval()
    {
        return -1;
    }

    protected void doDamageEntities(float radius, float power)
    {
        this.doDamageEntities(radius, power, true);
    }

    protected void doDamageEntities(float radius, float power, boolean destroyItem)
    {
        // Step 2: Damage all entities
        radius *= 2.0F;
        Vector3 minCoord = position.clone();
        minCoord.add(-radius - 1);
        Vector3 maxCoord = position.clone();
        maxCoord.add(radius + 1);
        List<Entity> allEntities = world().getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minCoord.xi(), minCoord.yi(), minCoord.zi(), maxCoord.xi(), maxCoord.yi(), maxCoord.zi()));
        Vec3 var31 = Vec3.createVectorHelper(position.x(), position.y(), position.z());

        for (int i = 0; i < allEntities.size(); ++i)
        {
            Entity entity = allEntities.get(i);

            if (this.onDamageEntity(entity))
            {
                continue;
            }

            if (entity instanceof EntityMissile)
            {
                ((EntityMissile) entity).setExplode();
                continue;
            }

            if (entity instanceof EntityItem && !destroyItem)
                continue;

            double distance = entity.getDistance(position.x(), position.y(), position.z()) / radius;

            if (distance <= 1.0D)
            {
                double xDifference = entity.posX - position.x();
                double yDifference = entity.posY - position.y();
                double zDifference = entity.posZ - position.z();
                double var35 = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                xDifference /= var35;
                yDifference /= var35;
                zDifference /= var35;
                double var34 = world().getBlockDensity(var31, entity.boundingBox);
                double var36 = (1.0D - distance) * var34;
                int damage = 0;

                damage = (int) ((var36 * var36 + var36) / 2.0D * 8.0D * power + 1.0D);

                entity.attackEntityFrom(DamageSource.setExplosionSource(this), damage);

                entity.motionX += xDifference * var36;
                entity.motionY += yDifference * var36;
                entity.motionZ += zDifference * var36;
            }
        }
    }

    /** Called by doDamageEntity on each entity being damaged. This function should be inherited if
     * something special is to happen to a specific entity.
     * 
     * @return True if something special happens to this specific entity. */
    protected boolean onDamageEntity(Entity entity)
    {
        return false;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        this.callCount = nbt.getInteger("callCount");
        this.explosionSize = nbt.getFloat("explosionSize");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("callCount", this.callCount);
        nbt.setFloat("explosionSize", this.explosionSize);
    }

    @Override
    public World world()
    {
        return this.position.world();
    }

    @Override
    public double x()
    {
        return this.position.x();
    }

    @Override
    public double y()
    {
        return this.position.y();
    }

    @Override
    public double z()
    {
        return this.position.z();
    }

}
