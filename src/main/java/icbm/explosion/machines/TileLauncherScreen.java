package icbm.explosion.machines;

import icbm.api.IMissile;
import icbm.api.ITier;
import icbm.api.LauncherType;
import icbm.core.ICBMCore;
import icbm.explosion.ICBMExplosion;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.link.IBlockActivate;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.prefab.tile.IRotatable;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia
 */
public class TileLauncherScreen extends TileLauncherPrefab implements IBlockActivate, ITier, IRotatable, IPacketReceiver
{
	// Is the block powered by redstone?
	private boolean isPowered = false;

	// The rotation of this missile component
	private byte fangXiang = 3;

	// The tier of this screen
	private int tier = 0;

	// The missile launcher base in which this
	// screen is connected with
	public TileLauncherBase laucherBase = null;

	public short gaoDu = 3;

	private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	public TileLauncherScreen()
	{
		this.energy = new EnergyStorageHandler(getEnergyCapacity());
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.laucherBase == null)
		{
			for (byte i = 2; i < 6; i++)
			{
				Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
				position.modifyPositionFromSide(ForgeDirection.getOrientation(i));

				TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());

				if (tileEntity != null)
				{
					if (tileEntity instanceof TileLauncherBase)
					{
						this.laucherBase = (TileLauncherBase) tileEntity;
						this.fangXiang = i;
					}
				}
			}
		}
		else
		{
			if (this.laucherBase.isInvalid())
			{
				this.laucherBase = null;
			}
		}

		if (isPowered)
		{
			isPowered = false;
			this.launch();
		}

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 3 == 0)
			{
				if (this.targetPos == null)
				{
					this.targetPos = new Vector3(this.xCoord, 0, this.zCoord);
				}

				for (EntityPlayer players : this.playersUsing)
				{
					PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket2(), (Player) players);
				}
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
		return ICBMCore.PACKET_TILE.getPacket(this, 0, this.fangXiang, this.tier, this.getFrequency(), this.gaoDu);
	}

	public Packet getDescriptionPacket2()
	{
		return ICBMCore.PACKET_TILE.getPacket(this, 4, this.energy.getEnergy(), this.targetPos.intX(), this.targetPos.intY(), this.targetPos.intZ());
	}

	@Override
	public void placeMissile(ItemStack itemStack)
	{
		if (this.laucherBase != null)
		{
			if (!this.laucherBase.isInvalid())
			{
				this.laucherBase.setInventorySlotContents(0, itemStack);
			}
		}
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		try
		{
			switch (data.readInt())
			{
				case -1:
				{
					if (data.readBoolean())
						this.playersUsing.add(player);
					else
						this.playersUsing.remove(player);
					break;
				}
				case 0:
				{
					this.fangXiang = data.readByte();
					this.tier = data.readInt();
					this.setFrequency(data.readInt());
					this.gaoDu = data.readShort();
					break;
				}
				case 1:
				{
					this.setFrequency(data.readInt());
					break;
				}
				case 2:
				{
					this.targetPos = new Vector3(data.readDouble(), data.readDouble(), data.readDouble());

					if (this.getTier() < 2)
					{
						this.targetPos.y = 0;
					}
					break;
				}
				case 3:
				{
					this.gaoDu = (short) Math.max(Math.min(data.readShort(), Short.MAX_VALUE), 3);
					break;
				}
				case 4:
				{
					this.energy.setEnergy(data.readLong());
					this.targetPos = new Vector3(data.readInt(), data.readInt(), data.readInt());
					break;
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Checks if the missile is launchable
	@Override
	public boolean canLaunch()
	{
		if (this.laucherBase != null)
		{
			if (this.laucherBase.missile != null)
			{
				if (this.energy.isFull())
				{
					if (this.laucherBase.isInRange(this.targetPos))
					{
						return true;
					}

				}
			}
		}

		return false;
	}

	/** Calls the missile launcher base to launch it's missile towards a targeted location */
	@Override
	public void launch()
	{
		if (this.canLaunch())
		{
			this.energy.setEnergy(0);
			this.laucherBase.launchMissile(this.targetPos.clone(), this.gaoDu);
		}
	}

	/**
	 * Gets the display status of the missile launcher
	 * 
	 * @return The string to be displayed
	 */
	@Override
	public String getStatus()
	{
		String color = "\u00a74";
		String status = "Idle";

		if (this.laucherBase == null)
		{
			status = "Not connected!";
		}
		else if (!this.energy.isFull())
		{
			status = "Insufficient electricity!";
		}
		else if (this.laucherBase.missile == null)
		{
			status = "Missile silo is empty!";
		}
		else if (this.targetPos == null)
		{
			status = "Target is invalid!";
		}
		else if (this.laucherBase.shiTaiJin(this.targetPos))
		{
			status = "Target too close!";
		}
		else if (this.laucherBase.shiTaiYuan(this.targetPos))
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

	/** Reads a tile entity from NBT. */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.tier = par1NBTTagCompound.getInteger("tier");
		this.fangXiang = par1NBTTagCompound.getByte("facingDirection");
		this.gaoDu = par1NBTTagCompound.getShort("gaoDu");
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("tier", this.tier);
		par1NBTTagCompound.setByte("facingDirection", this.fangXiang);
		par1NBTTagCompound.setShort("gaoDu", this.gaoDu);
	}

	@Override
	public long getVoltageInput(ForgeDirection dir)
	{
		return super.getVoltageInput(dir) * (this.getTier() + 1);
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
		this.energy.setCapacity(getEnergyCapacity());
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.fangXiang);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.fangXiang = (byte) facingDirection.ordinal();
	}

	public long getEnergyCapacity()
	{
		switch (this.getTier())
		{
			case 0:
				return 5000000;
			case 1:
				return 8000000;
		}

		return 10000000;
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public LauncherType getLauncherType()
	{
		return LauncherType.TRADITIONAL;
	}

	@Override
	public IMissile getMissile()
	{
		if (this.laucherBase != null)
		{
			return this.laucherBase.getContainingMissile();
		}

		return null;
	}
}
