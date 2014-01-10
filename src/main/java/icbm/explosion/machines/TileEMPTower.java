package icbm.explosion.machines;

import icbm.Reference;
import icbm.api.RadarRegistry;
import icbm.core.ICBMCore;
import icbm.core.prefab.TileICBM;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.explosive.blast.BlastEmp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.energy.EnergyStorageHandler;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.multiblock.link.IBlockActivate;
import calclavia.lib.multiblock.link.IMultiBlock;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.prefab.tile.IRedstoneReceptor;

import com.google.common.io.ByteArrayDataInput;

public class TileEMPTower extends TileICBM implements IPacketReceiver, IMultiBlock, IRedstoneReceptor, IBlockActivate
{
	// The maximum possible radius for the EMP to strike
	public static final int MAX_RADIUS = 150;

	public float xuanZhuan = 0;
	private float xuanZhuanLu, prevXuanZhuanLu = 0;

	// The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
	public byte empMode = 0;

	// The EMP explosion radius
	public int empRadius = 60;

	public TileEMPTower()
	{
		RadarRegistry.register(this);
		this.energy = new EnergyStorageHandler(Math.max(3000000 * (this.empRadius / MAX_RADIUS), 1000000));
	}

	@Override
	public void invalidate()
	{
		RadarRegistry.unregister(this);
		super.invalidate();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.ticks % 20 == 0 && this.energy.getEnergy() > 0)
		{
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "machinehum", 0.5F, 0.85F * this.energy.getEnergy() / this.energy.getEnergyCapacity());
		}

		this.xuanZhuanLu = (float) (Math.pow(this.energy.getEnergy() / this.energy.getEnergyCapacity(), 2) * 0.5);
		this.xuanZhuan += xuanZhuanLu;
		if (this.xuanZhuan > 360)
			this.xuanZhuan = 0;

		this.prevXuanZhuanLu = this.xuanZhuanLu;
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		try
		{
			switch (data.readInt())
			{
				case 0:
				{
					this.energy.setEnergy(data.readLong());
					this.empRadius = data.readInt();
					this.empMode = data.readByte();
					break;
				}
				case 1:
				{
					this.empRadius = data.readInt();
					this.energy.setCapacity(Math.max(3000000 * (this.empRadius / MAX_RADIUS), 1000000));
					break;
				}
				case 2:
				{
					this.empMode = data.readByte();
					break;
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
		return ICBMCore.PACKET_TILE.getPacket(this, this.energy.getEnergy(), this.empRadius, this.empMode);
	}

	/** Reads a tile entity from NBT. */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.empRadius = par1NBTTagCompound.getInteger("banJing");
		this.empMode = par1NBTTagCompound.getByte("muoShi");
	}

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setInteger("banJing", this.empRadius);
		par1NBTTagCompound.setByte("muoShi", this.empMode);
	}

	@Override
	public void onPowerOn()
	{
		if (this.energy.isFull())
		{
			switch (this.empMode)
			{
				default:
					new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
					break;
				case 1:
					new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
					break;
				case 2:
					new BlastEmp(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
					break;
			}

			this.setEnergy(ForgeDirection.UNKNOWN, 0);
		}
	}

	@Override
	public void onPowerOff()
	{
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public Vector3[] getMultiBlockVectors()
	{
		return new Vector3[] { new Vector3(0, 1, 0) };
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

}
