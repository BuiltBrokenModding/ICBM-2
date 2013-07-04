package icbm.zhapin.daodan;

import icbm.api.ILauncherContainer;
import icbm.api.IMissile;
import icbm.api.IMissileLockable;
import icbm.api.RadarRegistry;
import icbm.api.explosion.IExplosive;
import icbm.api.explosion.IExplosiveContainer;
import icbm.core.ZhuYaoBase;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.jiqi.TXiaoFaSheQi;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EDaoDan extends Entity implements IMissileLockable, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile
{
	public enum XingShi
	{
		DAO_DAN, XIAO_DAN, HUO_JIAN
	}

	public static final float JIA_KUAI_SU_DU = 0.012F;

	public int haoMa = 0;
	public int tianGao = 200;
	public Vector3 muBiao = null;
	public Vector3 kaiShi = null;
	public Vector3 faSheQi = null;
	public boolean zhengZaiBaoZha = false;

	public int baoZhaGaoDu = 0;
	public int feiXingTick = -1;
	// Difference
	public double xXiangCha;
	public double yXiangCha;
	public double zXiangCha;
	// Flat Distance
	public double diShangJuLi;
	// The flight time in ticks
	public float feiXingShiJian;
	// Acceleration
	public float jiaSu;
	// Protection Time
	public int baoHuShiJian = 2;

	private Ticket chunkTicket;

	// For anti-ballistic missile
	public Entity lockedTarget;
	// Has this missile lock it's target before?
	public boolean didTargetLockBefore = false;
	// Tracking
	public int genZongE = -1;
	// For cluster missile
	public int daoDanCount = 0;

	public double daoDanGaoDu = 2;

	private boolean setExplode;
	private boolean setNormalExplode;

	// Missile Type
	public XingShi xingShi = XingShi.DAO_DAN;

	public Vector3 xiaoDanMotion = new Vector3();

	private double qiFeiGaoDu = 3;

	// Client side
	protected final IUpdatePlayerListBox shengYin;

	public EDaoDan(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
		this.renderDistanceWeight = 3;
		this.isImmuneToFire = true;
		this.ignoreFrustumCheck = true;
		this.shengYin = this.worldObj != null ? ZhuYaoZhaPin.proxy.getDaoDanShengYin(this) : null;
	}

	/**
	 * Spawns a traditional missile and cruise missiles
	 * 
	 * @param haoMa - Explosive ID
	 * @param diDian - Starting Position
	 * @param faSheQiDiDian - Missile Launcher Position
	 */
	public EDaoDan(World par1World, Vector3 diDian, Vector3 faSheQiDiDian, int haoMa)
	{
		this(par1World);
		this.haoMa = haoMa;
		this.kaiShi = diDian;
		this.faSheQi = faSheQiDiDian;

		this.setPosition(this.kaiShi.x, this.kaiShi.y, this.kaiShi.z);
		this.setRotation(0, 90);
	}

	/**
	 * For rocket launchers
	 * 
	 * @param haoMa - Explosive ID
	 * @param diDian - Starting Position
	 * @param muBiao - Target Position
	 */
	public EDaoDan(World par1World, Vector3 diDian, int haoMa, float yaw, float pitch)
	{
		this(par1World);
		this.haoMa = haoMa;
		this.faSheQi = this.kaiShi = diDian;
		this.xingShi = XingShi.HUO_JIAN;
		this.baoHuShiJian = 10;

		this.setPosition(this.kaiShi.x, this.kaiShi.y, this.kaiShi.z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public String getEntityName()
	{
		if (this.haoMa >= 100)
		{
			return TranslationHelper.getLocal("icbm.missile." + DaoDan.list[this.haoMa].getUnlocalizedName() + ".name");
		}

		return TranslationHelper.getLocal("icbm.missile." + ZhaPin.list[this.haoMa].getUnlocalizedName() + ".name");
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data)
	{
		try
		{
			data.writeInt(this.haoMa);
			data.writeInt(this.xingShi.ordinal());

			data.writeDouble(this.kaiShi.x);
			data.writeDouble(this.kaiShi.y);
			data.writeDouble(this.kaiShi.z);

			data.writeInt(this.faSheQi.intX());
			data.writeInt(this.faSheQi.intY());
			data.writeInt(this.faSheQi.intZ());
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
			this.haoMa = data.readInt();
			this.xingShi = XingShi.values()[data.readInt()];
			this.kaiShi = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
			this.faSheQi = new Vector3(data.readInt(), data.readInt(), data.readInt());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void launch(Vector3 target)
	{
		this.kaiShi = new Vector3(this);
		this.muBiao = target;
		this.baoZhaGaoDu = this.muBiao.intY();
		DaoDan.list[this.haoMa].launch(this);
		this.feiXingTick = 0;
		this.jiSuan();
		this.worldObj.playSoundAtEntity(this, "icbm.missilelaunch", 4F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		RadarRegistry.register(this);
		ZhuYaoBase.LOGGER.info("Launching " + this.getEntityName() + " from " + kaiShi.intX() + ", " + kaiShi.intY() + ", " + kaiShi.intZ() + " to " + muBiao.intX() + ", " + muBiao.intY() + ", " + muBiao.intZ());
	}

	@Override
	public void launch(Vector3 target, int height)
	{
		this.qiFeiGaoDu = height;
		this.launch(target);
	}

	/**
	 * Recalculates required parabolic path for the missile.
	 * 
	 * @param target
	 */
	public void jiSuan()
	{
		if (this.muBiao != null)
		{
			// Calculate the distance difference of the missile
			this.xXiangCha = this.muBiao.x - this.kaiShi.x;
			this.yXiangCha = this.muBiao.y - this.kaiShi.y;
			this.zXiangCha = this.muBiao.z - this.kaiShi.z;

			// TODO: Calcualte parabola and relative out the height.
			// Calculate the power required to reach the target co-ordinates
			this.diShangJuLi = Vector2.distance(this.kaiShi.toVector2(), this.muBiao.toVector2());
			this.tianGao = 160 + (int) (this.diShangJuLi * 3);
			this.feiXingShiJian = (float) Math.max(100, 2 * this.diShangJuLi) - this.feiXingTick;
			this.jiaSu = (float) this.tianGao * 2 / (this.feiXingShiJian * this.feiXingShiJian);
		}
	}

	@Override
	public void entityInit()
	{
		this.dataWatcher.addObject(16, -1);
		this.daoDanInit(ForgeChunkManager.requestTicket(ZhuYaoZhaPin.instance, this.worldObj, Type.ENTITY));
	}

	public void daoDanInit(Ticket ticket)
	{
		if (ticket != null)
		{
			if (this.chunkTicket == null)
			{
				this.chunkTicket = ticket;
				this.chunkTicket.bindEntity(this);
				this.chunkTicket.getModData();
			}

			ForgeChunkManager.forceChunk(this.chunkTicket, new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ));
		}
	}

	public void updateLoadChunk(int newChunkX, int newChunkZ)
	{
		if (!this.worldObj.isRemote && ZhuYaoBase.ZAI_KUAI && this.chunkTicket != null)
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
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		if (this.shengYin != null)
		{
			this.shengYin.update();
		}

		if (!this.worldObj.isRemote)
		{
			if (ZhuYaoZhaPin.shiBaoHu(this.worldObj, new Vector3(this), ZhaPinType.DAO_DAN, this.haoMa))
			{
				if (this.feiXingTick >= 0)
				{
					this.dropMissileAsItem();
				}

				this.setDead();
				return;
			}
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
			RadarRegistry.register(this);

			if (!this.worldObj.isRemote)
			{
				if (this.xingShi == XingShi.XIAO_DAN || this.xingShi == XingShi.HUO_JIAN)
				{
					if (this.feiXingTick == 0 && this.xiaoDanMotion != null)
					{
						this.xiaoDanMotion = new Vector3(this.xXiangCha / (feiXingShiJian * 0.3), this.yXiangCha / (feiXingShiJian * 0.3), this.zXiangCha / (feiXingShiJian * 0.3));
					}

					this.motionX = this.xiaoDanMotion.x;
					this.motionY = this.xiaoDanMotion.y;
					this.motionZ = this.xiaoDanMotion.z;

					this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

					// Look at the next point
					this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

					DaoDan.list[this.haoMa].update(this);

					Block block = Block.blocksList[this.worldObj.getBlockId((int) this.posX, (int) this.posY, (int) this.posZ)];

					if (this.baoHuShiJian <= 0 && ((block != null && !(block instanceof BlockFluid)) || this.posY > 1000 || this.isCollided || this.feiXingTick > 20 * 1000 || (this.motionX == 0 && this.motionY == 0 && this.motionZ == 0)))
					{
						this.explode();
						return;
					}

					this.moveEntity(this.motionX, this.motionY, this.motionZ);
				}
				else
				{
					// Start the launch
					if (this.qiFeiGaoDu > 0)
					{
						this.motionY = EDaoDan.JIA_KUAI_SU_DU * this.feiXingTick * (this.feiXingTick / 2);
						this.motionX = 0;
						this.motionZ = 0;
						this.qiFeiGaoDu -= this.motionY;
						this.moveEntity(this.motionX, this.motionY, this.motionZ);

						if (this.qiFeiGaoDu <= 0)
						{
							this.motionY = this.jiaSu * (this.feiXingShiJian / 2);
							this.motionX = this.xXiangCha / feiXingShiJian;
							this.motionZ = this.zXiangCha / feiXingShiJian;
						}
					}
					else
					{
						this.motionY -= this.jiaSu;

						this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

						// Look at the next point
						this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

						DaoDan.list[this.haoMa].update(this);

						this.moveEntity(this.motionX, this.motionY, this.motionZ);

						// If the missile contacts anything, it will explode.
						if (this.isCollided)
						{
							this.explode();
						}

						// If the missile is commanded to explode before impact
						if (this.baoZhaGaoDu > 0 && this.motionY < 0)
						{
							// Check the block below it.
							int blockBelowID = this.worldObj.getBlockId((int) this.posX, (int) this.posY - baoZhaGaoDu, (int) this.posZ);

							if (blockBelowID > 0)
							{
								this.baoZhaGaoDu = 0;
								this.explode();
							}
						}
					} // end else
				}
			}
			else
			{
				this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);
				// Look at the next point
				this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
			}

			this.lastTickPosX = this.posX;
			this.lastTickPosY = this.posY;
			this.lastTickPosZ = this.posZ;

			this.spawnMissileSmoke();
			this.baoHuShiJian--;
			this.feiXingTick++;
		}
		else if (this.xingShi != XingShi.HUO_JIAN)
		{
			// Check to find the launcher in which this missile belongs in.
			ILauncherContainer launcher = this.getLauncher();

			if (launcher != null)
			{
				launcher.setContainingMissile(this);

				if (launcher instanceof TXiaoFaSheQi)
				{
					this.xingShi = XingShi.XIAO_DAN;
					this.noClip = true;

					if (this.worldObj.isRemote)
					{
						this.rotationYaw = -((TXiaoFaSheQi) launcher).rotationYaw + 90;
						this.rotationPitch = ((TXiaoFaSheQi) launcher).rotationPitch;
					}

					this.posY = ((TXiaoFaSheQi) launcher).yCoord + 1;
				}
			}
			else
			{
				this.setDead();
			}
		}

		super.onUpdate();
	}

	@Override
	public ILauncherContainer getLauncher()
	{
		if (this.faSheQi != null)
		{
			TileEntity tileEntity = this.faSheQi.getTileEntity(this.worldObj);

			if (tileEntity != null && tileEntity instanceof ILauncherContainer)
			{
				if (!tileEntity.isInvalid())
				{
					return (ILauncherContainer) tileEntity;
				}
			}
		}

		return null;
	}

	@Override
	public boolean interact(EntityPlayer entityPlayer)
	{
		if (DaoDan.list[this.haoMa] != null)
		{
			if (DaoDan.list[this.haoMa].onInteract(this, entityPlayer))
			{
				return true;
			}
		}

		if (!this.worldObj.isRemote && (this.riddenByEntity == null || this.riddenByEntity == entityPlayer))
		{
			entityPlayer.mountEntity(this);
			return true;
		}

		return false;
	}

	@Override
	public double getMountedYOffset()
	{
		if (this.feiXingShiJian <= 0 && this.xingShi == XingShi.DAO_DAN)
		{
			return this.height;
		}
		else if (this.xingShi == XingShi.XIAO_DAN)
		{
			return this.height * 0.1;
		}

		return this.height / 2 + this.motionY;
	}

	private void spawnMissileSmoke()
	{
		if (this.worldObj.isRemote)
		{
			Vector3 position = new Vector3(this);
			// The distance of the smoke relative
			// to the missile.
			double distance = -this.daoDanGaoDu - 0.2f;
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
			this.worldObj.spawnParticle("flame", position.x, position.y, position.z, 0, 0, 0);
			ZhuYaoZhaPin.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
			position.multiply(1 - 0.001 * Math.random());
			ZhuYaoZhaPin.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
			position.multiply(1 - 0.001 * Math.random());
			ZhuYaoZhaPin.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
			position.multiply(1 - 0.001 * Math.random());
			ZhuYaoZhaPin.proxy.spawnParticle("missile_smoke", this.worldObj, position, 4, 2);
		}
	}

	/**
	 * Checks to see if and entity is touching the missile. If so, blow up!
	 */

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		// Make sure the entity is not an item
		if (!(entity instanceof EntityItem) && entity != this.riddenByEntity && this.baoHuShiJian <= 0)
		{
			if (entity instanceof EDaoDan)
			{
				((EDaoDan) entity).setNormalExplode();
			}

			this.setExplode();
		}

		return null;
	}

	@Override
	public Vector3 getPredictedPosition(int t)
	{
		Vector3 guJiDiDian = new Vector3(this);
		double tempMotionY = this.motionY;

		if (this.feiXingTick > 20)
		{
			for (int i = 0; i < t; i++)
			{
				if (this.xingShi == XingShi.XIAO_DAN || this.xingShi == XingShi.HUO_JIAN)
				{
					guJiDiDian.x += this.xiaoDanMotion.x;
					guJiDiDian.y += this.xiaoDanMotion.y;
					guJiDiDian.z += this.xiaoDanMotion.z;
				}
				else
				{
					guJiDiDian.x += this.motionX;
					guJiDiDian.y += tempMotionY;
					guJiDiDian.z += this.motionZ;

					tempMotionY -= this.jiaSu;
				}
			}
		}

		return guJiDiDian;
	}

	@Override
	public void setNormalExplode()
	{
		this.setNormalExplode = true;
	}

	@Override
	public void setExplode()
	{
		this.setExplode = true;
	}

	@Override
	public void setDead()
	{
		RadarRegistry.unregister(this);

		if (this.chunkTicket != null)
		{
			ForgeChunkManager.releaseTicket(this.chunkTicket);
		}

		super.setDead();

		if (this.shengYin != null)
		{
			this.shengYin.update();
		}
	}

	@Override
	public void explode()
	{
		try
		{
			// Make sure the missile is not already exploding
			if (!this.zhengZaiBaoZha)
			{
				if (this.haoMa == 0)
				{
					if (!this.worldObj.isRemote)
					{
						this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 5F, true);
					}
				}
				else
				{
					DaoDan.list[this.haoMa].onExplode(this);
				}

				this.zhengZaiBaoZha = true;

				ZhuYaoBase.LOGGER.info(this.getEntityName() + " exploded in " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
			}

			this.setDead();

		}
		catch (Exception e)
		{
			ZhuYaoBase.LOGGER.severe("Missile failed to explode properly. Report this to the developers.");
			e.printStackTrace();
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

	@Override
	public void dropMissileAsItem()
	{
		if (!this.zhengZaiBaoZha && !this.worldObj.isRemote)
		{
			EntityItem entityItem;

			if (this.haoMa >= 100)
			{
				entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ZhuYaoZhaPin.itTeBieDaoDan, 1, this.haoMa - 100));

			}
			else
			{
				entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, this.haoMa));
			}

			float var13 = 0.05F;
			Random random = new Random();
			entityItem.motionX = ((float) random.nextGaussian() * var13);
			entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
			entityItem.motionZ = ((float) random.nextGaussian() * var13);
			this.worldObj.spawnEntityInWorld(entityItem);
		}

		this.setDead();
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		this.kaiShi = Vector3.readFromNBT(nbt.getCompoundTag("kaiShi"));
		this.muBiao = Vector3.readFromNBT(nbt.getCompoundTag("muBiao"));
		this.faSheQi = Vector3.readFromNBT(nbt.getCompoundTag("faSheQi"));
		this.jiaSu = nbt.getFloat("jiaSu");
		this.baoZhaGaoDu = nbt.getInteger("baoZhaGaoDu");
		this.haoMa = nbt.getInteger("haoMa");
		this.feiXingTick = nbt.getInteger("feiXingTick");
		this.qiFeiGaoDu = nbt.getDouble("qiFeiGaoDu");
		this.xingShi = XingShi.values()[nbt.getInteger("xingShi")];
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setCompoundTag("kaiShi", this.kaiShi.writeToNBT(new NBTTagCompound()));

		if (this.muBiao != null)
		{
			nbt.setCompoundTag("muBiao", this.muBiao.writeToNBT(new NBTTagCompound()));
		}

		if (this.faSheQi != null)
		{
			nbt.setCompoundTag("faSheQi", this.faSheQi.writeToNBT(new NBTTagCompound()));
		}

		nbt.setFloat("jiaSu", this.jiaSu);
		nbt.setInteger("haoMa", this.haoMa);
		nbt.setInteger("baoZhaGaoDu", this.baoZhaGaoDu);
		nbt.setInteger("feiXingTick", this.feiXingTick);
		nbt.setDouble("qiFeiGaoDu", this.qiFeiGaoDu);
		nbt.setInteger("xingShi", this.xingShi.ordinal());

	}

	@Override
	public float getShadowSize()
	{
		return 1.0F;
	}

	@Override
	public int getTicksInAir()
	{
		return this.feiXingTick;
	}

	@Override
	public IExplosive getExplosiveType()
	{
		if (this.haoMa > ZhaPin.list.length)
		{
			return DaoDan.list[this.haoMa];
		}

		return ZhaPin.list[this.haoMa];
	}

	@Override
	public boolean canLock(IMissile missile)
	{
		return this.feiXingTick > 0;
	}

	@Override
	public void destroyCraft()
	{
		this.normalExplode();
	}

	@Override
	public int doDamage(int damage)
	{
		return -1;
	}

	@Override
	public boolean canBeTargeted(Object turret)
	{
		return this.getTicksInAir() > 0;
	}
}
