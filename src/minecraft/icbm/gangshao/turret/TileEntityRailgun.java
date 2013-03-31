package icbm.gangshao.turret;

import icbm.api.explosion.IExplosive;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/**
 * 
 * @author Calclavia
 * 
 */
public class TileEntityRailgun extends TileEntityBaseTurret implements IPacketReceiver, IRedstoneReceptor, IMultiBlock
{
	protected EntityPlayer mountedPlayer = null;

	private EntityFakeMountable entityFake = null;

	private int gunChargingTicks = 0;

	private boolean redstonePowerOn = false;

	private boolean isAntimatter;

	private float explosionSize;

	private int explosionDepth;

	private boolean packetGengXin = true;

	/**
	 * A counter used client side for the smoke and streaming effects of the Railgun after a shot.
	 */
	private int endTicks = 0;

	@Override
	public void onUpdate()
	{
		if (this.mountedPlayer != null)
		{
			if (this.mountedPlayer.rotationPitch > 30)
				this.mountedPlayer.rotationPitch = 30;
			if (this.mountedPlayer.rotationPitch < -45)
				this.mountedPlayer.rotationPitch = -45;

			this.targetRotationPitch = this.mountedPlayer.rotationPitch;
			this.targetRotationYaw = this.mountedPlayer.rotationYaw;

			this.rotationPitch = this.targetRotationPitch * 0.0175f;
			this.rotationYaw = this.targetRotationYaw * 0.0175f;
		}
		else if (this.entityFake != null)
		{
			this.entityFake.setDead();
			this.entityFake = null;
		}

		if (this.getPlatform() != null)
		{
			if (this.redstonePowerOn && this.canActivateWeapon() && this.gunChargingTicks == 0)
			{
				this.onWeaponActivated();
			}

			if (this.gunChargingTicks > 0)
			{
				this.gunChargingTicks++;

				if (this.gunChargingTicks >= 70)
				{
					while (this.explosionDepth > 0)
					{
						MovingObjectPosition objectMouseOver = this.rayTrace(2000);

						if (objectMouseOver != null)
						{
							if (!ZhuYaoGangShao.isProtected(this.worldObj, new Vector3(objectMouseOver), ZhuYaoGangShao.FLAG_RAILGUN) && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
							{
								if (this.isAntimatter)
								{
									/**
									 * Remove Redmatter Explosions.
									 */
									int radius = 50;
									AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(objectMouseOver.blockX - radius, objectMouseOver.blockY - radius, objectMouseOver.blockZ - radius, objectMouseOver.blockX + radius, objectMouseOver.blockY + radius, objectMouseOver.blockZ + radius);
									List<Entity> missilesNearby = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

									for (Entity entity : missilesNearby)
									{
										if (entity instanceof IExplosive)
										{
											entity.setDead();
										}
									}
								}

								if (this.worldObj.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ) != Block.bedrock.blockID)
								{
									this.worldObj.setBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, 0, 0, 2);
								}

								this.worldObj.newExplosion(this.mountedPlayer, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, explosionSize, true, true);
							}
						}

						this.explosionDepth--;
					}

					if (!this.worldObj.isRemote)
					{
						PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, 3), this.worldObj, new Vector3(this), 50);
					}

					this.gunChargingTicks = 0;
				}
			}

			if (!this.worldObj.isRemote)
			{
				if (this.ticks % 600 == 0)
				{
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}
			else if (this.endTicks > 0)
			{
				Vector3 muzzilePosition = this.getMuzzle();
				this.worldObj.spawnParticle("smoke", muzzilePosition.x, muzzilePosition.y, muzzilePosition.z, 0, 0, 0);
				this.worldObj.spawnParticle("flame", muzzilePosition.x, muzzilePosition.y, muzzilePosition.z, 0, 0, 0);

				MovingObjectPosition objectMouseOver = this.rayTrace(2000);

				if (objectMouseOver != null)
				{
					this.drawParticleStreamTo(Vector3.add(new Vector3(objectMouseOver), 0.5));
				}
				this.endTicks--;
			}
		}
	}

	public void drawParticleStreamTo(Vector3 endPosition)
	{
		if (this.worldObj.isRemote)
		{
			Vector3 startPosition = new Vector3(this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5);
			Vector3 direction = LookHelper.getDeltaPositionFromRotation(this.rotationYaw - 15, this.rotationPitch);
			double scale = 1.0;
			double xoffset = 1.3f;
			double yoffset = -.2;
			double zoffset = 0.3f;
			Vector3 horzdir = direction.normalize();
			horzdir.y = 0;
			horzdir = horzdir.normalize();
			double cx = startPosition.x + direction.x * xoffset - direction.y * horzdir.x * yoffset - horzdir.z * zoffset;
			double cy = startPosition.y + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
			double cz = startPosition.z + direction.x * xoffset - direction.y * horzdir.x * yoffset + horzdir.x * zoffset;
			double dx = endPosition.x - cx;
			double dy = endPosition.y - cy;
			double dz = endPosition.z - cz;
			double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

			while (Math.abs(cx - endPosition.x) > Math.abs(dx / ratio))
			{
				this.worldObj.spawnParticle("townaura", cx, cy, cz, 0.0D, 0.0D, 0.0D);
				cx += dx * 0.1 / ratio;
				cy += dy * 0.1 / ratio;
				cz += dz * 0.1 / ratio;
			}
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			int ID = dataStream.readInt();

			if (ID == 1)
			{
				this.rotationYaw = dataStream.readFloat();
				this.rotationPitch = dataStream.readFloat();
			}
			else if (ID == 2)
			{
				this.mount(player);
			}
			else if (ID == 3)
			{
				/**
				 * This packet is sent when a shot is fired by the Railgun, resulting in smoke.
				 */
				if (this.worldObj.isRemote)
				{
					this.endTicks = 20 * 3;
				}

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, 1, this.rotationYaw, this.rotationPitch);
	}

	@Override
	public double getVoltage()
	{
		return 220;
	}

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0, 0, 2);
		this.worldObj.setBlock(this.xCoord, this.yCoord + 1, this.zCoord, 0, 0, 2);
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (this.mountedPlayer != null && entityPlayer == this.mountedPlayer)
		{
			this.mountedPlayer = null;
			entityPlayer.unmountEntity(this.entityFake);

			if (this.entityFake != null)
			{
				this.entityFake.setDead();
				this.entityFake = null;
			}
		}
		else
		{
			this.mount(entityPlayer);
		}

		return true;
	}

	public void mount(EntityPlayer entityPlayer)
	{
		// Creates a fake entity to be mounted on
		if (this.mountedPlayer == null)
		{
			if (!this.worldObj.isRemote)
			{
				this.entityFake = new EntityFakeMountable(this.worldObj, new Vector3(this.xCoord + 0.5D, this.yCoord, this.zCoord + 0.5D), this, false);
				this.worldObj.spawnEntityInWorld(entityFake);
				entityPlayer.mountEntity(entityFake);
			}

			this.mountedPlayer = entityPlayer;
			entityPlayer.rotationYaw = 0;
			entityPlayer.rotationPitch = 0;
		}
	}

	@Override
	public void onCreate(Vector3 position)
	{
		this.worldObj.setBlock(position.intX(), position.intY() + 1, position.intZ(), ZhuYaoGangShao.blockFake.blockID, 0, 2);
		((TileEntityMulti) this.worldObj.getBlockTileEntity(position.intX(), position.intY() + 1, position.intZ())).setMainBlock(position);
	}

	/**
	 * Performs a ray trace for the distance specified and using the partial tick time. Args:
	 * distance, partialTickTime
	 */
	public MovingObjectPosition rayTrace(double distance)
	{
		Vector3 muzzlePosition = this.getMuzzle();
		Vector3 lookDistance = LookHelper.getDeltaPositionFromRotation(this.targetRotationYaw, this.targetRotationPitch);
		Vector3 var6 = Vector3.add(muzzlePosition, Vector3.multiply(lookDistance, distance));
		return this.worldObj.rayTraceBlocks(muzzlePosition.toVec3(), var6.toVec3());
	}

	@Override
	public Vector3 getMuzzle()
	{
		Vector3 position = new Vector3(this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5);
		return Vector3.add(position, Vector3.multiply(LookHelper.getDeltaPositionFromRotation(this.targetRotationYaw - 10, this.targetRotationPitch), 1.5));
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.targetRotationYaw = par1NBTTagCompound.getFloat("rotationYaw");
		this.targetRotationPitch = par1NBTTagCompound.getFloat("rotationPitch");

		this.rotationPitch = this.targetRotationPitch * 0.0175f;
		this.rotationYaw = this.targetRotationYaw * 0.0175f;
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setFloat("rotationYaw", this.targetRotationYaw);
		par1NBTTagCompound.setFloat("rotationPitch", this.targetRotationPitch);

		NBTTagList var2 = new NBTTagList();

		par1NBTTagCompound.setTag("Items", var2);
	}

	@Override
	public void onPowerOn()
	{
		this.redstonePowerOn = true;
	}

	@Override
	public void onPowerOff()
	{
		this.redstonePowerOn = false;
	}

	@Override
	public int getCooldown()
	{
		return 70;
	}

	@Override
	public double getRequest()
	{
		return 3000000;
	}

	@Override
	public void onWeaponActivated()
	{
		this.gunChargingTicks = 1;
		this.redstonePowerOn = false;
		this.isAntimatter = false;

		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.railgun", 5F, 1F);

		if (this.getPlatform().useAmmunition(ZhuYaoGangShao.antimatterBullet))
		{
			this.isAntimatter = true;
		}
		else
		{
			this.getPlatform().useAmmunition(ZhuYaoGangShao.railgunBullet);
		}

		this.getPlatform().wattsReceived = 0;

		this.explosionSize = 5f;
		this.explosionDepth = 5;

		if (isAntimatter)
		{
			this.explosionSize = 8f;
			this.explosionDepth = 10;
		}
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.getPlatform() != null)
		{
			if (this.getPlatform().hasAmmunition(ZhuYaoGangShao.railgunBullet) || this.getPlatform().hasAmmunition(ZhuYaoGangShao.antimatterBullet))
			{
				if (this.getPlatform().wattsReceived >= this.getRequest())
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public float addInformation(HashMap<String, Integer> map, EntityPlayer player)
	{
		super.addInformation(map, player);
		return 2;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
}