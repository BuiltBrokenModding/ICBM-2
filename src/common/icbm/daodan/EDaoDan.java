package icbm.daodan;

import icbm.BaoHu;
import icbm.ParticleSpawner;
import icbm.ZhuYao;
import icbm.api.IMissile;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.zhapin.ZhaPin;

import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EDaoDan extends Entity implements IEntityAdditionalSpawnData, IMissile
{
	public static final float JIA_KUAI_SU_DU = 0.012F;

	public int haoMa = 0;
	public int tianGao = 200;
	public Vector3 muBiao = null;
	public Vector3 kaoShi = null;
	public Vector3 faSheQi = null;
	public boolean zhengZaiBaoZha = false;

	public int baoZhaGaoDu = 0;
	public int feiXingTick = -1;
	//Difference
	public double xXiangCha;
	public double yXiangCha;
	public double zXiangCha;
	//Flat Distance
	public double diShangJuLi;
	// The flight time in ticks
	public float feiXingShiJian;
	//Acceleration
	public float jiaSu;
	//Protection Time
	public int baoHuShiJian = 20;

	private Ticket chunkTicket;

	// For anti-ballistic missile
	public EDaoDan lockedTarget;
	// Has this missile lock it's target before?
	public boolean didTargetLockBefore = false;
	// Tracking
	public int genZongE = -1;
	// For cluster missile
	public int daoDanCount = 0;

	public double daoDanGaoDu = 2;

	// Cruise Missile
	public boolean isCruise;
	private boolean setExplode;
	private boolean setNormalExplode;

	public EDaoDan(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
		this.renderDistanceWeight = 2F;
		this.isImmuneToFire = true;
	}

	/**
	 * Spawns a traditional missile
	 */
	public EDaoDan(World par1World, Vector3 position, Vector3 launcherPosition, int metadata)
	{
		this(par1World);
		this.haoMa = metadata;
		this.kaoShi = position;
		this.faSheQi = launcherPosition;

		this.setPosition(this.kaoShi.x, this.kaoShi.y, this.kaoShi.z);
		this.setRotation(0, 90);
	}

	@Override
	public String getEntityName()
	{
		if (this.haoMa > 100) { return DaoDan.list[this.haoMa].getMing(); }

		return ZhaPin.list[this.haoMa].getDaoDanMing();
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		data.writeInt(this.haoMa);

		data.writeDouble(this.kaoShi.x);
		data.writeDouble(this.kaoShi.y);
		data.writeDouble(this.kaoShi.z);

		data.writeDouble(this.faSheQi.x);
		data.writeDouble(this.faSheQi.y);
		data.writeDouble(this.faSheQi.z);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data)
	{
		this.haoMa = data.readInt();

		this.kaoShi = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());

		this.faSheQi = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
	}

	public void launchMissile(Vector3 muBiao)
	{
		this.kaoShi = new Vector3(this.posX, this.posY, this.posZ);
		this.muBiao = muBiao;
		this.baoZhaGaoDu = (int) muBiao.y;

		// Calculate the distance difference of
		// the missile
		this.xXiangCha = this.muBiao.x - this.kaoShi.x;
		this.yXiangCha = this.muBiao.y - this.kaoShi.y;
		this.zXiangCha = this.muBiao.z - this.kaoShi.z;

		// Calculate the power required to reach
		// the target co-ordinates
		this.diShangJuLi = Vector2.distance(this.kaoShi.toVector2(), this.muBiao.toVector2());

		this.tianGao = 160 + (int) (this.diShangJuLi * 3);

		this.feiXingShiJian = (float) Math.max(100, 2 * diShangJuLi);
		this.jiaSu = (float) tianGao * 2 / (feiXingShiJian * feiXingShiJian);

		this.feiXingTick = 0;

		this.worldObj.playSoundAtEntity(this, "icbm.missilelaunch", 4F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		DaoDanGuanLi.addMissile(this);

		FMLLog.fine("Launching " + this.getEntityName() + " from " + kaoShi.intX() + ", " + kaoShi.intY() + ", " + kaoShi.intZ() + " to " + muBiao.intX() + ", " + muBiao.intY() + ", " + muBiao.intZ());
	}

	@Override
	public void entityInit()
	{
		this.dataWatcher.addObject(16, -1);
		this.daoDanInit(ForgeChunkManager.requestTicket(ZhuYao.instance, this.worldObj, Type.ENTITY));
	}

	public void daoDanInit(Ticket ticket)
	{
		if (this.chunkTicket == null)
		{
			this.chunkTicket = ticket;
			this.chunkTicket.bindEntity(this);
			this.chunkTicket.getModData();
		}

		ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ));
	}

	public void updateLoadChunk(int newChunkX, int newChunkZ)
	{
		if (!this.worldObj.isRemote && ZhuYao.ALLOW_LOAD_CHUNKS)
		{
			for (int x = -2; x <= 2; x++)
			{
				for (int z = -2; z <= 2; z++)
				{
					this.worldObj.getChunkFromChunkCoords(newChunkX + x, newChunkZ + z);
				}
			}

			for (int x = -1; x <= 1; x++)
			{
				for (int z = -1; z <= 1; z++)
				{
					ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(newChunkX + x, newChunkZ + z));
				}
			}
		}
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	/**
	 * Called to update the entity's
	 * position/logic.
	 */
	@Override
	public void onUpdate()
	{
		if (!BaoHu.nengDaoDanBaoHu(this.worldObj, Vector3.get(this).toVector2()))
		{
			if (this.feiXingTick >= 0)
			{
				this.dropMissileAsItem();
			}

			this.setDead();
			return;
		}

		if (this.setNormalExplode)
		{
			this.normalExplode();
			return;
		}

		if (this.setExplode)
		{
			this.explode();
			return;
		}

		super.onUpdate();

		try
		{
			if (this.worldObj.isRemote)
			{
				this.feiXingTick = this.dataWatcher.getWatchableObjectInt(16);
			}
			else
			{
				this.dataWatcher.updateObject(16, this.feiXingTick);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		this.updateLoadChunk(this.chunkCoordX, this.chunkCoordZ);

		if (this.feiXingTick >= 0)
		{
			if (!this.worldObj.isRemote)
			{
				if (this.isCruise)
				{
					if (this.feiXingTick == 0)
					{
						this.motionX = this.xXiangCha / (feiXingShiJian * 0.4);
						this.motionY = this.yXiangCha / (feiXingShiJian * 0.4);
						this.motionZ = this.zXiangCha / (feiXingShiJian * 0.4);
					}

					this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

					// Look at the next point
					this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

					DaoDan.list[this.haoMa].onTickFlight(this);

					this.noClip = false;

					this.moveEntity(this.motionX, this.motionY, this.motionZ);

					Vector3 position = Vector3.get(this);
					// this.isCollided =
					// this.worldObj.getBlockId(MathHelper.floor_double(this.posX),
					// MathHelper.floor_double(this.posY),
					// MathHelper.floor_double(this.posZ))
					// != 0;

					if ((this.isCollided && this.feiXingTick >= 20) || this.feiXingTick > 20 * 600)
					{
						this.explode();
					}
				}
				else
				{
					// Start the launch
					if (this.feiXingTick < 20)
					{
						this.motionY = this.JIA_KUAI_SU_DU * this.feiXingTick * (this.feiXingTick / 2);
						this.moveEntity(this.motionX, this.motionY, this.motionZ);
					}
					else if (this.feiXingTick == 20)
					{
						this.motionY = this.jiaSu * (this.feiXingShiJian / 2);

						this.motionX = this.xXiangCha / feiXingShiJian;
						this.motionZ = this.zXiangCha / feiXingShiJian;
					}
					else
					{
						Vector3 currentPosition = new Vector3(this.posX, this.posY, this.posZ);
						double currentDistance = Vector2.distance(currentPosition.toVector2(), this.muBiao.toVector2());

						this.motionY -= this.jiaSu;

						this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

						// Look at the next point
						this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

						DaoDan.list[this.haoMa].onTickFlight(this);

						this.moveEntity(this.motionX, this.motionY, this.motionZ);

						this.lastTickPosX = this.posX;
						this.lastTickPosY = this.posY;
						this.lastTickPosZ = this.posZ;

						// If the missile contacts
						// anything, it will
						// explode.
						if (this.isCollided)
						{
							this.explode();
						}

						// If the missile is
						// commanded to explode
						// before impact
						if (baoZhaGaoDu > 0 && this.motionY < 0)
						{
							// Check the block
							// below it.
							int blockBelowID = this.worldObj.getBlockId((int) this.posX, (int) this.posY - baoZhaGaoDu, (int) this.posZ);

							if (blockBelowID > 0)
							{
								baoZhaGaoDu = 0;
								this.explode();
							}
						}
					} // end else
				}
			}

			this.spawnMissileSmoke();
			this.baoHuShiJian--;
			this.feiXingTick++;
		}
		else
		{

			// Check to find the launcher in which
			// this missile belongs in.
			if (this.faSheQi == null)
			{
				this.setDead();
				return;
			}

			TileEntity tileEntity = this.worldObj.getBlockTileEntity((int) faSheQi.x, (int) faSheQi.y, (int) faSheQi.z);

			if (tileEntity == null)
			{
				this.setDead();
				return;
			}

			if (tileEntity.isInvalid())
			{
				this.setDead();
				return;
			}

			if (tileEntity instanceof TFaSheDi)
			{
				if (((TFaSheDi) tileEntity).eDaoDan == null)
				{
					((TFaSheDi) tileEntity).eDaoDan = this;
				}
			}
			else if (tileEntity instanceof TXiaoFaSheQi)
			{
				if (((TXiaoFaSheQi) tileEntity).eDaoDan == null)
				{
					((TXiaoFaSheQi) tileEntity).eDaoDan = this;
				}

				this.isCruise = true;
				this.noClip = true;

				this.xXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().x - this.kaoShi.x;
				this.yXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().y - this.kaoShi.y;
				this.zXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().z - this.kaoShi.z;

				this.diShangJuLi = Vector2.distance(this.kaoShi.toVector2(), ((TXiaoFaSheQi) tileEntity).getTarget().toVector2());
				this.tianGao = 150 + (int) (this.diShangJuLi * 1.8);
				this.feiXingShiJian = (float) Math.max(100, 2.4 * diShangJuLi);
				this.jiaSu = (float) tianGao * 2 / (feiXingShiJian * feiXingShiJian);

				this.motionX = this.xXiangCha / (feiXingShiJian * 0.6);
				this.motionY = this.yXiangCha / (feiXingShiJian * 0.6);
				this.motionZ = this.zXiangCha / (feiXingShiJian * 0.6);

				float newRotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);
				float newRotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

				if (newRotationYaw - this.rotationYaw != 0)
				{
					this.rotationYaw += (newRotationYaw - this.rotationYaw) * 0.1;
				}

				if (newRotationPitch - this.rotationPitch != 0)
				{
					this.rotationPitch += (newRotationPitch - this.rotationPitch) * 0.1;
				}
			}
		}
	}

	@Override
	public boolean interact(EntityPlayer par1EntityPlayer)
	{
		if (DaoDan.list[this.haoMa] != null) { return DaoDan.list[this.haoMa].onInteract(this, par1EntityPlayer); }

		return false;
	}

	private void spawnMissileSmoke()
	{
		if (this.worldObj.isRemote)
		{
			Vector3 position = Vector3.get(this);
			// The distance of the smoke relative
			// to the missile.
			double distance = -daoDanGaoDu - 0.2f;
			Vector3 delta = new Vector3();
			// The delta Y of the smoke.
			delta.y = Math.sin(Math.toRadians(this.rotationPitch)) * distance;
			// The horizontal distance of the
			// smoke.
			double dH = Math.cos(Math.toRadians(this.rotationPitch)) * distance;
			// The delta X and Z.
			delta.x = Math.sin(Math.toRadians(this.rotationYaw)) * dH;
			delta.z = Math.cos(Math.toRadians(this.rotationYaw)) * dH;
			position.add(delta);
			ParticleSpawner.spawnParticle("smoke", this.worldObj, position);
			this.worldObj.spawnParticle("flame", position.x, position.y, position.z, 0, 0, 0);
		}
	}

	/**
	 * Checks to see if and entity is touching the
	 * missile. If so, blow up!
	 */

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		// Make sure the entity is not an item
		if (!(par1Entity instanceof EntityItem) && this.baoHuShiJian <= 0)
		{
			if (par1Entity instanceof EDaoDan)
			{
				((EDaoDan) par1Entity).setNormalExplode();
			}

			this.setExplode();
		}

		return null;
	}

	public void setNormalExplode()
	{
		this.setNormalExplode = true;
	}

	public void setExplode()
	{
		this.setExplode = true;
	}

	@Override
	public void setDead()
	{
		if (chunkTicket != null)
		{
			ForgeChunkManager.releaseTicket(chunkTicket);
		}
		super.setDead();
	}

	@Override
	public void explode()
	{
		try
		{
			// Make sure the missile is not
			// already exploding
			if (!this.zhengZaiBaoZha)
			{
				this.zhengZaiBaoZha = true;

				if (!this.worldObj.isRemote)
				{
					if (this.haoMa == 0)
					{
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F, true);
					}
					else
					{
						DaoDan.list[this.haoMa].onExplode(this);
					}

					FMLLog.fine(this.getEntityName() + " landed on " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
				}

				this.setDead();
			}
		}
		catch (Exception e)
		{
			System.err.println("Missile failed to explode properly. Report this to the developers.");
		}
	}

	@Override
	public void normalExplode()
	{
		if (!this.zhengZaiBaoZha)
		{
			this.zhengZaiBaoZha = true;

			if (!this.worldObj.isRemote)
			{
				this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F, true);
			}

			this.setDead();
		}
	}

	public void dropMissileAsItem()
	{
		if (!this.zhengZaiBaoZha && !this.worldObj.isRemote)
		{
			EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ZhuYao.itDaoDan, 1, this.haoMa + 1));
			float var13 = 0.05F;
			Random random = new Random();
			entityItem.motionX = ((float) random.nextGaussian() * var13);
			entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
			entityItem.motionZ = ((float) random.nextGaussian() * var13);
			this.worldObj.spawnEntityInWorld(entityItem);
			this.setDead();
		}
	}

	/**
	 * (abstract) Protected helper method to read
	 * subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		this.kaoShi = Vector3.readFromNBT("startingPosition", par1NBTTagCompound);
		this.muBiao = Vector3.readFromNBT("targetPosition", par1NBTTagCompound);
		this.faSheQi = Vector3.readFromNBT("missileLauncherPosition", par1NBTTagCompound);
		this.jiaSu = par1NBTTagCompound.getFloat("acceleration");
		this.baoZhaGaoDu = par1NBTTagCompound.getInteger("HeightBeforeHit");
		this.haoMa = par1NBTTagCompound.getInteger("missileID");
		this.feiXingTick = par1NBTTagCompound.getInteger("ticksInAir");
		this.isCruise = par1NBTTagCompound.getBoolean("isCruise");
	}

	/**
	 * (abstract) Protected helper method to write
	 * subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		this.kaoShi.writeToNBT("startingPosition", par1NBTTagCompound);
		if (this.muBiao != null)
		{
			this.muBiao.writeToNBT("targetPosition", par1NBTTagCompound);
		}
		this.faSheQi.writeToNBT("missileLauncherPosition", par1NBTTagCompound);

		par1NBTTagCompound.setFloat("acceleration", this.jiaSu);
		par1NBTTagCompound.setInteger("missileID", this.haoMa);
		par1NBTTagCompound.setInteger("HeightBeforeHit", this.baoZhaGaoDu);
		par1NBTTagCompound.setInteger("ticksInAir", this.feiXingTick);
		par1NBTTagCompound.setBoolean("isCruise", this.isCruise);
	}

	@Override
	public float getShadowSize()
	{
		return 1.0F;
	}
}
