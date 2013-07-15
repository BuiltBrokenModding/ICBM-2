package icbm.zhapin.baozha;

import icbm.api.explosion.ExplosionEvent.DoExplosionEvent;
import icbm.api.explosion.ExplosionEvent.ExplosionConstructionEvent;
import icbm.api.explosion.ExplosionEvent.PostExplosionEvent;
import icbm.api.explosion.ExplosionEvent.PreExplosionEvent;
import icbm.api.explosion.IExplosion;
import icbm.core.di.MICBM;
import icbm.zhapin.zhapin.daodan.EDaoDan;

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
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BaoZha extends Explosion implements IExplosion
{
	public World worldObj;
	protected Vector3 position;
	protected EBaoZha controller = null;

	/** The amount of times the explosion has been called */
	protected int callCount = 0;

	public BaoZha(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
		this.worldObj = world;
		this.position = new Vector3(x, y, z);
	}

	protected void doPreExplode()
	{
	}

	/**
	 * Called before an explosion happens.
	 */
	public final void preExplode()
	{
		PreExplosionEvent evt = new PreExplosionEvent(this.worldObj, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled())
		{
			this.doPreExplode();
		}
	}

	/**
	 * Called every tick when this explosive is being progressed.
	 */
	protected abstract void doExplode();

	public final void onExplode()
	{
		DoExplosionEvent evt = new DoExplosionEvent(this.worldObj, this);
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

	/**
	 * Called after the explosion is completed.
	 */
	public final void postExplode()
	{
		PostExplosionEvent evt = new PostExplosionEvent(this.worldObj, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled())
		{
			this.doPostExplode();
		}
	}

	/**
	 * Make the default functions useless.
	 */
	@Override
	public void doExplosionA()
	{
	}

	@Override
	public void doExplosionB(boolean par1)
	{
	}

	/**
	 * All outside classes should call this.
	 */
	@Override
	public void explode()
	{
		ExplosionConstructionEvent evt = new ExplosionConstructionEvent(this.worldObj, this);
		MinecraftForge.EVENT_BUS.post(evt);

		if (!evt.isCanceled())
		{
			if (this.proceduralInterval() > 0)
			{
				if (!this.worldObj.isRemote)
				{
					this.worldObj.spawnEntityInWorld(new EBaoZha(this));
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
	public float getEnergy()
	{
		return 0;
	}

	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * 
	 * @param return - Return -1 if this explosive does not need procedural calls
	 */
	protected int proceduralInterval()
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
		List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minCoord.intX(), minCoord.intY(), minCoord.intZ(), maxCoord.intX(), maxCoord.intY(), maxCoord.intZ()));
		Vec3 var31 = Vec3.createVectorHelper(position.x, position.y, position.z);

		for (int i = 0; i < allEntities.size(); ++i)
		{
			Entity entity = allEntities.get(i);

			if (this.onDamageEntity(entity))
			{
				continue;
			}

			if (entity instanceof EDaoDan)
			{
				((EDaoDan) entity).setExplode();
				continue;
			}

			if (entity instanceof EntityItem && !destroyItem)
				continue;

			double distance = entity.getDistance(position.x, position.y, position.z) / radius;

			if (distance <= 1.0D)
			{
				double xDifference = entity.posX - position.x;
				double yDifference = entity.posY - position.y;
				double zDifference = entity.posZ - position.z;
				double var35 = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
				xDifference /= var35;
				yDifference /= var35;
				zDifference /= var35;
				double var34 = worldObj.getBlockDensity(var31, entity.boundingBox);
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

	/**
	 * Called by doDamageEntity on each entity being damaged. This function should be inherited if
	 * something special is to happen to a specific entity.
	 * 
	 * @return True if something special happens to this specific entity.
	 */
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

	public boolean isMovable()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	public MICBM getRenderModel()
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getRenderResource()
	{
		return null;
	}

}
