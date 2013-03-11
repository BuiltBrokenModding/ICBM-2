package icbm.zhapin.jiqi;

import icbm.api.LauncherType;
import icbm.core.ZhuYao;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.daodan.EDaoDan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.implement.ITier;
import universalelectricity.prefab.multiblock.IBlockActivate;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia
 * 
 */
public class TFaSheShiMuo extends TFaSheQi implements IBlockActivate, IPacketReceiver, ITier, IRotatable
{
	// Is the block powered by redstone?
	private boolean isPowered = false;

	// The rotation of this missile component
	private byte fangXiang = 3;

	// The tier of this screen
	private int tier = 0;

	// The missile launcher base in which this
	// screen is connected with
	public TFaSheDi connectedBase = null;

	public short gaoDu = 3;

	private int yongZhe = 0;

	public TFaSheShiMuo()
	{
		super();
	}

	public void updateEntity()
	{
		super.updateEntity();

		if (!this.isDisabled())
		{
			if (this.connectedBase == null)
			{
				for (byte i = 2; i < 6; i++)
				{
					Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
					position.modifyPositionFromSide(ForgeDirection.getOrientation(i));

					TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());

					if (tileEntity != null)
					{
						if (tileEntity instanceof TFaSheDi)
						{
							this.connectedBase = (TFaSheDi) tileEntity;
							this.fangXiang = i;
						}
					}
				}
			}
			else
			{
				if (this.connectedBase.isInvalid())
				{
					this.connectedBase = null;
				}
			}

			if (isPowered)
			{
				isPowered = false;
				this.launch();
			}
		}

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 3 == 0 && this.yongZhe > 0)
			{
				if (this.muBiao == null)
				{
					this.muBiao = new Vector3(this.xCoord, 0, this.zCoord);
				}

				PacketManager.sendPacketToClients(PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, (int) 3, this.getJoules(), this.disabledTicks, this.muBiao.x, this.muBiao.y, this.muBiao.z), this.worldObj, new Vector3(this), 15);
			}

			if (this.ticks % 600 == 0)
			{
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYaoZhaPin.CHANNEL, this, (int) 0, this.fangXiang, this.tier, this.getFrequency(), this.gaoDu);
	}

	@Override
	public void placeMissile(ItemStack itemStack)
	{
		if (this.connectedBase != null)
		{
			if (!this.connectedBase.isInvalid())
			{
				this.connectedBase.setInventorySlotContents(0, itemStack);
			}
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final int ID = dataStream.readInt();

			if (ID == -1)
			{
				if (dataStream.readBoolean())
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket());
					this.yongZhe++;
				}
				else
				{
					this.yongZhe--;
				}
			}
			else if (ID == 0)
			{
				this.fangXiang = dataStream.readByte();
				this.tier = dataStream.readInt();
				this.setFrequency(dataStream.readInt());
				this.gaoDu = dataStream.readShort();
			}
			else if (!this.worldObj.isRemote)
			{
				if (ID == 1)
				{
					this.setFrequency(dataStream.readInt());
				}
				else if (ID == 2)
				{
					this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());

					if (this.getTier() < 2)
					{
						this.muBiao.y = 0;
					}
				}
				else if (ID == 3)
				{
					this.gaoDu = (short) Math.max(Math.min(dataStream.readShort(), 99), 3);
				}
			}
			else if (ID == 3)
			{
				if (this.worldObj.isRemote)
				{
					this.setJoules(dataStream.readDouble());
					this.disabledTicks = dataStream.readInt();
					this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Checks if the missile is launchable
	public boolean canLaunch()
	{
		if (this.connectedBase != null && !this.isDisabled())
		{
			if (this.connectedBase.eDaoDan != null)
			{
				if (this.getJoules() >= this.getMaxJoules())
				{
					if (this.connectedBase.isInRange(this.muBiao))
					{
						return true;
					}

				}
			}
		}

		return false;
	}

	/**
	 * Calls the missile launcher base to launch it's missile towards a targeted location
	 */
	public void launch()
	{
		if (this.canLaunch())
		{
			this.setJoules(0);
			this.connectedBase.launchMissile(this.muBiao.clone(), this.gaoDu);
		}
	}

	/**
	 * Gets the display status of the missile launcher
	 * 
	 * @return The string to be displayed
	 */
	public String getStatus()
	{
		String color = "\u00a74";
		String status = "Idle";

		if (this.isDisabled())
		{
			status = "Disabled";
		}
		else if (this.connectedBase == null)
		{
			status = "Not connected!";
		}
		else if (this.getJoules() < this.getMaxJoules())
		{
			status = "Insufficient electricity!";
		}
		else if (this.connectedBase.eDaoDan == null)
		{
			status = "Missile silo is empty!";
		}
		else if (this.muBiao == null)
		{
			status = "Target is invalid!";
		}
		else if (this.connectedBase.shiTaiJin(this.muBiao))
		{
			status = "Target too close!";
		}
		else if (this.connectedBase.shiTaiYuan(this.muBiao))
		{
			status = "Target too far!";
		}
		else
		{
			color = "\u00a72";
			status = "Ready to launch!";
		}

		return color + status;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.tier = par1NBTTagCompound.getInteger("tier");
		this.fangXiang = par1NBTTagCompound.getByte("facingDirection");
		this.gaoDu = par1NBTTagCompound.getShort("gaoDu");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("tier", this.tier);
		par1NBTTagCompound.setByte("facingDirection", this.fangXiang);
		par1NBTTagCompound.setShort("gaoDu", this.gaoDu);
	}

	@Override
	public double getVoltage()
	{
		switch (this.getTier())
		{
			default:
				return 120;
			case 1:
				return 240;
			case 2:
				return 480;
		}
	}

	@Override
	public void onPowerOn()
	{
		this.isPowered = true;
	}

	@Override
	public void onPowerOff()
	{
		this.isPowered = false;
	}

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public void setTier(int tier)
	{
		this.tier = tier;
	}

	@Override
	public ForgeDirection getDirection(World world, int x, int y, int z)
	{
		return ForgeDirection.getOrientation(this.fangXiang);
	}

	@Override
	public void setDirection(World world, int x, int y, int z, ForgeDirection facingDirection)
	{
		this.fangXiang = (byte) facingDirection.ordinal();
	}

	@Override
	public double getMaxJoules()
	{
		switch (this.getTier())
		{
			case 0:
				return 400000;
			case 1:
				return 60000;
		}

		return 800000;
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ZhuYaoZhaPin.instance, ZhuYao.GUI_FA_SHE_SHI_MUO, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public LauncherType getLauncherType()
	{
		return LauncherType.TRADITIONAL;
	}

	@Override
	public EDaoDan getMissile()
	{
		if (this.connectedBase != null)
		{
			return this.connectedBase.eDaoDan;
		}

		return null;
	}
}
