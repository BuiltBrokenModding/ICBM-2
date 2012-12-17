package icbm.common.jiqi;

import icbm.common.ZhuYao;
import icbm.common.dianqi.ItHuoLuanQi;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;

import com.google.common.io.ByteArrayDataInput;

public class TYinGanQi extends TileEntityElectricityReceiver implements IRedstoneProvider, IPacketReceiver
{
	// Watts Per Tick
	public static final float YAO_DIAN = 5;

	// The electricity stored
	public double prevDian, dian = 0;

	public short frequency = 0;

	public boolean isDetect = false;

	public Vector3 minCoord = new Vector3(9, 9, 9);
	public Vector3 maxCoord = new Vector3(9, 9, 9);

	public byte mode = 0;

	private int yongZhe = 0;

	public TYinGanQi()
	{
		super();
	}

	@Override
	public void initiate()
	{
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
	}

	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < 6; i++)
			{
				Vector3 diDian = new Vector3(this);
				diDian.modifyPositionFromSide(ForgeDirection.getOrientation(i));
				TileEntity tileEntity = diDian.getTileEntity(this.worldObj);

				ElectricityNetwork network = ElectricityNetwork.getNetworkFromTileEntity(tileEntity, ForgeDirection.getOrientation(i));

				if (network != null)
				{
					if (!this.isDisabled())
					{
						network.startRequesting(this, this.YAO_DIAN / this.getVoltage(), this.getVoltage());
						this.dian += network.consumeElectricity(this).getWatts();
					}
					else
					{
						network.stopRequesting(this);
					}

				}
			}
		
			if (this.ticks % 5 == 0 && this.yongZhe > 0)
			{
				PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
			}

			if (!this.isDisabled())
			{
				if (this.dian >= this.YAO_DIAN)
				{
					boolean isDetectThisCheck = false;

					AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x, this.yCoord - minCoord.y, this.zCoord - minCoord.z, this.xCoord + maxCoord.x + 1D, this.yCoord + maxCoord.y + 1D, this.zCoord + maxCoord.z + 1D);
					List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

					for (EntityLiving entity : entitiesNearby)
					{
						if (entity instanceof EntityPlayer && (this.mode == 0 || this.mode == 1))
						{
							boolean gotDisrupter = false;

							for (ItemStack inventory : ((EntityPlayer) entity).inventory.mainInventory)
							{
								if (inventory != null)
								{
									if (inventory.getItem() instanceof ItHuoLuanQi)
									{
										if (((ItHuoLuanQi) inventory.getItem()).getFrequency(inventory) == this.frequency)
										{
											gotDisrupter = true;
											break;
										}
									}
								}
							}

							if (gotDisrupter)
							{
								continue;
							}

							isDetectThisCheck = true;
						}
						else if (!(entity instanceof EntityPlayer) && (this.mode == 0 || this.mode == 2))
						{
							isDetectThisCheck = true;
							break;
						}
					}

					if (isDetectThisCheck != this.isDetect)
					{
						this.isDetect = isDetectThisCheck;
						this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
					}

					this.dian = 0;
				}
				else
				{
					this.isDetect = false;
				}
			}

			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
		}
		
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int) 1, this.dian, this.frequency, this.mode, this.minCoord.x, this.minCoord.y, this.minCoord.z, this.maxCoord.x, this.maxCoord.y, this.maxCoord.z);
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
					this.yongZhe++;
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 15);
				}
				else
				{
					this.yongZhe--;
				}
			}
			else if (ID == 1)
			{
				this.dian = dataStream.readDouble();
				this.frequency = dataStream.readShort();
				this.mode = dataStream.readByte();
				this.minCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				this.maxCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if (ID == 2)
			{
				this.mode = dataStream.readByte();
			}
			else if (ID == 3)
			{
				this.frequency = dataStream.readShort();
			}
			else if (ID == 4)
			{
				this.minCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if (ID == 5)
			{
				this.maxCoord = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.mode = par1NBTTagCompound.getByte("mode");
		this.frequency = par1NBTTagCompound.getShort("frequency");
		this.minCoord = Vector3.readFromNBT("minCoord", par1NBTTagCompound);
		this.maxCoord = Vector3.readFromNBT("maxCoord", par1NBTTagCompound);
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setShort("frequency", this.frequency);
		par1NBTTagCompound.setByte("mode", this.mode);
		this.minCoord.writeToNBT("minCoord", par1NBTTagCompound);
		this.maxCoord.writeToNBT("maxCoord", par1NBTTagCompound);
	}

	@Override
	public boolean isPoweringTo(ForgeDirection side)
	{
		return isDetect;
	}

	@Override
	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return isDetect;
	}
}
