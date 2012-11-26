package icbm.jiqi;

import icbm.BaoHu;
import icbm.ICBMCommon;
import icbm.ZhuYao;
import icbm.api.Launcher.LauncherType;
import icbm.daodan.DaoDan;
import icbm.daodan.EDaoDan;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.electricity.ElectricInfo;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.multiblock.IBlockActivate;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

public class TXiaoFaSheQi extends TFaSheQi implements IBlockActivate, IPacketReceiver, IInventory, ISidedInventory
{
	// The missile that this launcher is holding
	public EDaoDan eDaoDan = null;

	public float rotationYaw = 0;

	public float rotationPitch = 0;

	/**
	 * The ItemStacks that hold the items currently being used in the missileLauncher
	 */
	private ItemStack[] containingItems = new ItemStack[1];

	private boolean isPowered = false;

	private int yongZhe;

	public TXiaoFaSheQi()
	{
		super();
		this.muBiao = new Vector3();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg.
	 * Returns the new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as
	 * an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor
	 * sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
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
		else if (this.dian < this.getMaxJoules())
		{
			status = "No Power!";
		}
		else if (this.eDaoDan == null)
		{
			status = "Silo Empty!";
		}
		else if (this.muBiao == null)
		{
			status = "Invalid Target!";
		}
		else
		{
			color = "\u00a72";
			status = "Ready!";
		}

		return color + status;
	}

	/**
	 * Returns the name of the inventory.
	 */
	@Override
	public String getInvName()
	{
		return "Cruise Launcher";
	}

	@Override
	public void onReceive(Object sender, double amps, double voltage, ForgeDirection side)
	{
		if (!this.isDisabled())
		{
			this.setJoules(this.dian + ElectricInfo.getJoules(amps, voltage, 1));
		}
	}

	public void updateEntity()
	{
		super.updateEntity();

		if (!this.isDisabled())
		{
			// Rotate the yaw
			if (this.getYawFromTarget() - this.rotationYaw != 0)
			{
				this.rotationYaw += (this.getYawFromTarget() - this.rotationYaw) * 0.1;
			}
			if (this.getPitchFromTarget() - this.rotationPitch != 0)
			{
				this.rotationPitch += (this.getPitchFromTarget() - this.rotationPitch) * 0.1;
			}

			if (!this.worldObj.isRemote)
			{
				this.setMissile();

				if (this.isPowered)
				{
					this.launch();
					this.isPowered = false;
				}
			}

			if (!this.worldObj.isRemote && this.ticks % 3 == 0 && this.yongZhe > 0)
			{
				if (this.muBiao == null)
					this.muBiao = new Vector3(this.xCoord, this.yCoord, this.zCoord);
				this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(ZhuYao.CHANNEL, this, (int) 0, this.dian, this.shengBuo, this.disabledTicks, this.muBiao.x, this.muBiao.y, this.muBiao.z);
	}

	@Override
	public void placeMissile(ItemStack itemStack)
	{
		this.containingItems[0] = itemStack;
	}

	public void setMissile()
	{
		if (this.containingItems[0] != null && BaoHu.nengDaoDanBaoHu(this.worldObj, Vector3.get(this).toVector2()))
		{
			if (this.containingItems[0].itemID == ZhuYao.itDaoDan.shiftedIndex)
			{
				int missileId = this.containingItems[0].getItemDamage();

				if (this.eDaoDan == null)
				{
					if (DaoDan.list[missileId].isCruise() && DaoDan.list[missileId].getTier() <= 3)
					{
						Vector3 startingPosition = new Vector3((this.xCoord + 0.5f), (this.yCoord + 0.2f), (this.zCoord + 0.5f));
						this.eDaoDan = new EDaoDan(this.worldObj, startingPosition, Vector3.get(this), missileId);
						this.worldObj.spawnEntityInWorld(this.eDaoDan);
						return;
					}
				}

				if (this.eDaoDan != null)
				{
					if (this.eDaoDan.haoMa == missileId)
					{
						this.eDaoDan.posY = this.yCoord + 0.2f;
						return;
					}
				}
			}
		}

		if (this.eDaoDan != null)
		{
			this.eDaoDan.setDead();
		}

		this.eDaoDan = null;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			final int ID = dataStream.readInt();

			if (ID == 0)
			{
				this.dian = dataStream.readDouble();
				this.shengBuo = dataStream.readShort();
				this.disabledTicks = dataStream.readInt();
				this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
			else if (ID == 1)
			{
				if (!this.worldObj.isRemote)
				{
					this.shengBuo = dataStream.readShort();
				}
			}
			else if (ID == 2)
			{
				if (!this.worldObj.isRemote)
				{
					this.muBiao = new Vector3(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void openChest()
	{
		if (!this.worldObj.isRemote)
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}

		this.yongZhe++;
	}

	@Override
	public void closeChest()
	{
		this.yongZhe--;
	}

	private float getPitchFromTarget()
	{
		double distance = Math.sqrt((this.muBiao.x - this.xCoord) * (this.muBiao.x - this.xCoord) + (this.muBiao.z - this.zCoord) * (this.muBiao.z - this.zCoord));
		return (float) Math.toDegrees(Math.atan((this.muBiao.y - (this.yCoord + 0.5F)) / distance));
	}

	private float getYawFromTarget()
	{
		double xDifference = this.muBiao.x - (double) ((float) this.xCoord + 0.5F);
		double yDifference = this.muBiao.z - (double) ((float) this.zCoord + 0.5F);
		return (float) Math.toDegrees(Math.atan2(yDifference, xDifference));
	}

	@Override
	public boolean canLaunch()
	{
		if (this.eDaoDan != null && this.containingItems[0] != null)
		{
			DaoDan missile = DaoDan.list[this.containingItems[0].getItemDamage()];

			if (missile != null)
			{
				if (this.containingItems[0].itemID == ZhuYao.itDaoDan.shiftedIndex && missile.isCruise() && missile.getTier() <= 3)
				{
					if (this.dian >= this.getMaxJoules())
					{
						if (!this.isTooClose(this.muBiao)) { return true; }
					}
				}
			}
		}

		return false;
	}

	/**
	 * Launches the missile
	 * 
	 * @param muBiao
	 *            - The target in which the missile will land in
	 */
	public void launch()
	{
		if (canLaunch())
		{
			this.decrStackSize(0, 1);
			this.setJoules(0);
			this.eDaoDan.launchMissile(this.muBiao);
			this.eDaoDan = null;
		}
	}

	// Is the target too close?
	public boolean isTooClose(Vector3 target)
	{
		// Check if it is greater than the minimum
		// range
		if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 8) { return true; }

		return false;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.shengBuo = par1NBTTagCompound.getShort("frequency");
		this.muBiao = Vector3.readFromNBT("target", par1NBTTagCompound);

		NBTTagList var2 = par1NBTTagCompound.getTagList("Items");

		this.containingItems = new ItemStack[this.getSizeInventory()];
		this.dian = par1NBTTagCompound.getDouble("electricityStored");

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		if (this.muBiao != null)
		{
			this.muBiao.writeToNBT("target", par1NBTTagCompound);
		}

		par1NBTTagCompound.setShort("frequency", this.shengBuo);
		par1NBTTagCompound.setDouble("electricityStored", this.dian);

		NBTTagList var2 = new NBTTagList();

		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}

		par1NBTTagCompound.setTag("Items", var2);
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be
	 * extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 1;
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
	public boolean canReceiveFromSide(ForgeDirection side)
	{
		return true;
	}

	@Override
	public double wattRequest()
	{
		if (!this.isDisabled()) { return ElectricInfo.getWatts(this.getMaxJoules()) - ElectricInfo.getWatts(this.dian); }

		return 0;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 100000;
	}

	@Override
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer.inventory.getCurrentItem() != null && this.getStackInSlot(0) == null)
		{
			if (entityPlayer.inventory.getCurrentItem().itemID == ZhuYao.itDaoDan.shiftedIndex && DaoDan.list[entityPlayer.inventory.getCurrentItem().getItemDamage()].isCruise() && DaoDan.list[entityPlayer.inventory.getCurrentItem().getItemDamage()].getTier() <= 3)
			{
				this.setInventorySlotContents(0, entityPlayer.inventory.getCurrentItem());
				entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
				return true;
			}
		}

		entityPlayer.openGui(ZhuYao.instance, ICBMCommon.GUI_CRUISE_LAUNCHER, this.worldObj, this.xCoord, this.yCoord, this.zCoord);

		return true;
	}

	@Override
	public LauncherType getLauncherType()
	{
		return LauncherType.CRUISE;
	}

	@Override
	public EDaoDan getMissile()
	{
		return this.eDaoDan;
	}
}
