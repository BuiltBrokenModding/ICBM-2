package icbm.zhapin.baozha;

import icbm.core.ZhuYaoICBM;

import java.lang.reflect.Constructor;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * The Entity handler responsible for entity explosions.
 * 
 * @author Calclavia
 * 
 */
public class EBaoZha extends Entity implements IEntityAdditionalSpawnData
{
	public BaoZha baoZha;

	private boolean endExplosion = false;

	private boolean isMobile = false;

	public EBaoZha(World par1World)
	{
		super(par1World);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.renderDistanceWeight = 2f;
		this.ignoreFrustumCheck = true;
	}

	public EBaoZha(BaoZha baoZha)
	{
		this(baoZha.worldObj);
		this.baoZha = baoZha;
		this.isMobile = baoZha.isMovable();
		this.setPosition(baoZha.position.x, baoZha.position.y, baoZha.position.z);
	}

	@Override
	public String getEntityName()
	{
		return "Explosion";
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeBoolean(this.isMobile);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.isMobile = data.readBoolean();
	}

	@Override
	protected void entityInit()
	{
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for
	 * spiders and wolves to prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		/*
		 * if (!this.worldObj.isRemote) { if (ZhuYaoZhaPin.shiBaoHu(this.worldObj, new
		 * Vector3(this), ZhaPinType.ZHA_DAN, 0)) { this.setDead(); return; } }
		 */

		if (this.isMobile && (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0))
		{
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}

		this.baoZha.position = new Vector3(this);

		if (this.ticksExisted == 1)
		{
			this.baoZha.doPreExplode();
		}

		if (this.ticksExisted % this.baoZha.proceduralInterval() == 0)
		{
			if (!this.endExplosion)
			{
				this.baoZha.explode();
			}
			else
			{
				this.baoZha.doPostExplode();
				this.setDead();
			}
		}

		this.ticksExisted++;
	}

	public void endExplosion()
	{
		this.endExplosion = true;
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		try
		{
			NBTTagCompound baoZhaNBT = nbt.getCompoundTag("baoZha");

			if (this.baoZha == null)
			{
				Class clazz = Class.forName(baoZhaNBT.getString("class"));
				Constructor constructor = clazz.getConstructor(World.class, Entity.class, Double.class, Double.class, Double.class, Float.class);
				this.baoZha = (BaoZha) constructor.newInstance(this.worldObj, null, this.posX, this.posY, this.posZ);
			}

			this.baoZha.readEntityFromNBT(baoZhaNBT);
		}
		catch (Exception e)
		{
			ZhuYaoICBM.LOGGER.severe("ICBM error in loading an explosion!");
			e.printStackTrace();
		}

		this.isMobile = nbt.getBoolean("isMobile");
		this.ticksExisted = nbt.getInteger("ticksExisted");
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound baoZhaNBT = new NBTTagCompound();
		baoZhaNBT.setString("class", this.baoZha.getClass().getCanonicalName());
		this.baoZha.writeEntityToNBT(baoZhaNBT);

		nbt.setBoolean("isMobile", this.isMobile);
		nbt.setInteger("ticksExisted", this.ticksExisted);
	}

	@Override
	public float getShadowSize()
	{
		return 0F;
	}
}
