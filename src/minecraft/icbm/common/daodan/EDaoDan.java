package icbm.common.daodan;

import icbm.api.IMissile;
import icbm.client.fx.ParticleSpawner;
import icbm.common.BaoHu;
import icbm.common.ZhuYao;
import icbm.common.jiqi.TFaSheDi;
import icbm.common.jiqi.TXiaoFaSheQi;
import icbm.common.zhapin.ZhaPin;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
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

	private boolean setExplode;
	private boolean setNormalExplode;

	// Missile Type
	public XingShi xingShi = XingShi.DAO_DAN;

	public Vector3 xiaoDanMotion = new Vector3();

	public EDaoDan(World par1World)
	{
		super(par1World);
		this.setSize(1F, 1F);
		this.renderDistanceWeight = 2F;
		this.isImmuneToFire = true;
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
	public EDaoDan(World par1World, int haoMa, Vector3 diDian, float yaw, float pitch)
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
		if (this.haoMa > 100) { return DaoDan.list[this.haoMa].getTranslatedMing(); }

		return ZhaPin.list[this.haoMa].getMingZi();
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

			data.writeDouble(this.faSheQi.x);
			data.writeDouble(this.faSheQi.y);
			data.writeDouble(this.faSheQi.z);
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
			this.faSheQi = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void faShe(Vector3 muBiao)
	{
		this.kaiShi = new Vector3(this.posX, this.posY, this.posZ);
		this.muBiao = muBiao;
		this.baoZhaGaoDu = (int) muBiao.y;

		// Calculate the distance difference of the missile
		this.xXiangCha = this.muBiao.x - this.kaiShi.x;
		this.yXiangCha = this.muBiao.y - this.kaiShi.y;
		this.zXiangCha = this.muBiao.z - this.kaiShi.z;

		// Calculate the power required to reach the target co-ordinates
		this.diShangJuLi = Vector2.distance(this.kaiShi.toVector2(), this.muBiao.toVector2());

		this.tianGao = 160 + (int) (this.diShangJuLi * 3);

		this.feiXingShiJian = (float) Math.max(100, 2 * diShangJuLi);
		this.jiaSu = (float) tianGao * 2 / (feiXingShiJian * feiXingShiJian);

		this.feiXingTick = 0;

		this.worldObj.playSoundAtEntity(this, "icbm.missilelaunch", 4F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		DaoDanGuanLi.addMissile(this);

		FMLLog.fine("Launching " + this.getEntityName() + " from " + kaiShi.intX() + ", " + kaiShi.intY() + ", " + kaiShi.intZ() + " to " + muBiao.intX() + ", " + muBiao.intY() + ", " + muBiao.intZ());
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
		if (!this.worldObj.isRemote && ZhuYao.ZAI_KUAI)
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
		if (!BaoHu.nengDaoDanBaoHu(this.worldObj, new Vector3(this).toVector2()))
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
			if (this.feiXingTick == 1)
			{
				DaoDanGuanLi.addMissile(this);
			}

			if (!this.worldObj.isRemote)
			{
				if (this.xingShi == XingShi.XIAO_DAN || this.xingShi == XingShi.HUO_JIAN)
				{
					if (this.feiXingTick == 0)
					{
						this.xiaoDanMotion = new Vector3(this.xXiangCha / (feiXingShiJian * 0.3), this.yXiangCha / (feiXingShiJian * 0.3), this.zXiangCha / (feiXingShiJian * 0.3));
					}

					this.motionX = this.xiaoDanMotion.x;
					this.motionY = this.xiaoDanMotion.y;
					this.motionZ = this.xiaoDanMotion.z;

					this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

					// Look at the next point
					this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

					DaoDan.list[this.haoMa].onTickFlight(this);

					if (this.baoHuShiJian <= 0 && (this.posY > 1000 || this.worldObj.getBlockId((int) this.posX, (int) this.posY, (int) this.posZ) != 0 || this.isCollided || this.feiXingTick > 20 * 1000 || (this.motionX == 0 && this.motionY == 0 && this.motionZ == 0)))
					{
						this.explode();
						return;
					}

					this.moveEntity(this.motionX, this.motionY, this.motionZ);
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
						Vector3 currentPosition = new Vector3(this);
						double currentDistance = Vector2.distance(currentPosition.toVector2(), this.muBiao.toVector2());

						this.motionY -= this.jiaSu;

						this.rotationPitch = (float) (Math.atan(this.motionY / (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ))) * 180 / Math.PI);

						// Look at the next point
						this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);

						DaoDan.list[this.haoMa].onTickFlight(this);

						this.moveEntity(this.motionX, this.motionY, this.motionZ);

						// If the missile contacts anything, it will explode.
						if (this.isCollided)
						{
							this.explode();
						}

						// If the missile is commanded to explode before impact
						if (baoZhaGaoDu > 0 && this.motionY < 0)
						{
							// Check the block below it.
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

				this.xingShi = XingShi.XIAO_DAN;
				this.noClip = true;

				this.xXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().x - this.kaiShi.x;
				this.yXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().y - this.kaiShi.y;
				this.zXiangCha = ((TXiaoFaSheQi) tileEntity).getTarget().z - this.kaiShi.z;

				this.diShangJuLi = Vector2.distance(this.kaiShi.toVector2(), ((TXiaoFaSheQi) tileEntity).getTarget().toVector2());
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

		super.onUpdate();
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
			Vector3 position = new Vector3(this);
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
	 * Checks to see if and entity is touching the missile. If so, blow up!
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

	public Vector3 guJi(int t)
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

				FMLLog.fine(this.getEntityName() + " exploded in " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
				System.out.println(this.getEntityName() + " exploded in " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
			}

			this.setDead();

		}
		catch (Exception e)
		{
			System.err.println("Missile failed to explode properly. Report this to the developers.");
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
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		this.kaiShi = Vector3.readFromNBT("kaiShi", par1NBTTagCompound);
		this.muBiao = Vector3.readFromNBT("muBiao", par1NBTTagCompound);
		this.faSheQi = Vector3.readFromNBT("faSheQi", par1NBTTagCompound);
		this.jiaSu = par1NBTTagCompound.getFloat("jiaSu");
		this.baoZhaGaoDu = par1NBTTagCompound.getInteger("baoZhaGaoDu");
		this.haoMa = par1NBTTagCompound.getInteger("haoMa");
		this.feiXingTick = par1NBTTagCompound.getInteger("feiXingTick");
		this.xingShi = XingShi.values()[par1NBTTagCompound.getInteger("xingShi")];
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
	{
		this.kaiShi.writeToNBT("kaiShi", par1NBTTagCompound);

		if (this.muBiao != null)
		{
			this.muBiao.writeToNBT("muBiao", par1NBTTagCompound);
		}

		if (this.faSheQi != null)
		{
			this.faSheQi.writeToNBT("faSheQi", par1NBTTagCompound);
		}

		par1NBTTagCompound.setFloat("jiaSu", this.jiaSu);
		par1NBTTagCompound.setInteger("haoMa", this.haoMa);
		par1NBTTagCompound.setInteger("baoZhaGaoDu", this.baoZhaGaoDu);
		par1NBTTagCompound.setInteger("feiXingTick", this.feiXingTick);
		par1NBTTagCompound.setInteger("xingShi", this.xingShi.ordinal());
	}

	@Override
	public float getShadowSize()
	{
		return 1.0F;
	}
}
