package icbm.jiqi;

import icbm.ZhuYao;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRotatable;
import universalelectricity.prefab.implement.ITier;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia
 * 
 */
public class TFaSheJia extends TileEntity implements IPacketReceiver, ITier, IMultiBlock, IRotatable
{
	// The tier of this screen
	private int tier = 0;

	private byte orientation = 3;

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			this.orientation = dataStream.readByte();
			this.tier = dataStream.readInt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, this.orientation, this.getTier());
	}

	/**
	 * Gets the inaccuracy of the missile based on the launcher support frame's tier
	 */
	public int getInaccuracy()
	{
		switch (this.tier)
		{
			default:
				return 15;
			case 1:
				return 7;
			case 2:
				return 0;
		}
	}

	/**
	 * Determines if this TileEntity requires update calls.
	 * 
	 * @return True if you want updateEntity() to be called, false if not
	 */
	public boolean canUpdate()
	{
		return false;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		this.tier = par1NBTTagCompound.getInteger("tier");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("tier", this.tier);
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
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord + 1, this.zCoord, 0);
		this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord + 2, this.zCoord, 0);
	}

	@Override
	public boolean onActivated(EntityPlayer par5EntityPlayer)
	{
		return false;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		this.worldObj.setBlockWithNotify(position.intX(), position.intY() + 1, position.intZ(), ZhuYao.bJia.blockID);
		((TileEntityMulti) this.worldObj.getBlockTileEntity(position.intX(), position.intY() + 1, position.intZ())).setMainBlock(position);
		this.worldObj.setBlockWithNotify(position.intX(), position.intY() + 2, position.intZ(), ZhuYao.bJia.blockID);
		((TileEntityMulti) this.worldObj.getBlockTileEntity(position.intX(), position.intY() + 2, position.intZ())).setMainBlock(position);
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.orientation);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.orientation = (byte) facingDirection.ordinal();
	}
}
