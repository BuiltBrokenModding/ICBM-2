package icbm.explosion.baozha;

import icbm.core.ZhuYaoICBM;

import java.lang.reflect.Constructor;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

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

	public EBaoZha(World par1World)
	{
		super(par1World);
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.renderDistanceWeight = 2f;
		this.ignoreFrustumCheck = true;
		this.ticksExisted = 0;
	}

	public EBaoZha(BaoZha baoZha)
	{
		this(baoZha.worldObj);
		this.baoZha = baoZha;
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
		try
		{
			NBTTagCompound nbt = new NBTTagCompound();
			this.writeEntityToNBT(nbt);
			PacketManager.writeNBTTagCompound(nbt, data);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		try
		{
			this.readEntityFromNBT(PacketManager.readNBTTagCompound(data));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
		if (this.baoZha == null)
		{
			this.setDead();
			ZhuYaoICBM.LOGGER.severe("Procedural explosion ended due to null! This is a bug!");
			return;
		}

		this.baoZha.controller = this;
		this.baoZha.position = new Vector3(this);

		if (this.baoZha.isMovable() && (this.motionX != 0 || this.motionY != 0 || this.motionZ != 0))
		{
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}

		if (this.ticksExisted == 1)
		{
			this.baoZha.preExplode();
		}
		else if (this.ticksExisted % this.baoZha.proceduralInterval() == 0)
		{
			if (!this.endExplosion)
			{
				this.baoZha.onExplode();
			}
			else
			{
				this.baoZha.postExplode();
				this.setDead();
			}
		}
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
				Constructor constructor = clazz.getConstructor(World.class, Entity.class, double.class, double.class, double.class, float.class);
				this.baoZha = (BaoZha) constructor.newInstance(this.worldObj, null, this.posX, this.posY, this.posZ, 0);
			}

			this.baoZha.readFromNBT(baoZhaNBT);
		}
		catch (Exception e)
		{
			ZhuYaoICBM.LOGGER.severe("ICBM error in loading an explosion!");
			e.printStackTrace();
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound baoZhaNBT = new NBTTagCompound();
		baoZhaNBT.setString("class", this.baoZha.getClass().getCanonicalName());
		this.baoZha.writeToNBT(baoZhaNBT);
		nbt.setCompoundTag("baoZha", baoZhaNBT);
	}

	@Override
	public float getShadowSize()
	{
		return 0F;
	}
}
