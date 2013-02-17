package icbm.sentry.platform;

import icbm.sentry.api.IAmmunition;
import icbm.sentry.terminal.TileEntityTerminal;
import icbm.sentry.terminal.UserAccess;
import icbm.sentry.turret.TileEntityBaseTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.modifier.IModifier;

public class TileEntityTurretPlatform extends TileEntityTerminal implements IAmmunition, ISidedInventory
{
	/**
	 * The turret linked to this platform.
	 */
	private TileEntityBaseTurret turret = null;
	public ForgeDirection deployDirection = ForgeDirection.UP;

	/**
	 * The start index of the upgrade slots for the turret.
	 */
	public static final int UPGRADE_START_INDEX = 12;

	/**
	 * The first 12 slots are for ammunition. The last 4 slots are for upgrades.
	 */
	public ItemStack[] containingItems = new ItemStack[UPGRADE_START_INDEX + 4];

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.isRunning())
		{
			this.getTurret();
		}
	}

	@Override
	public void onReceive(ElectricityPack electricityPack)
	{
		/**
		 * Creates an explosion if the voltage is too high.
		 */
		if (UniversalElectricity.isVoltageSensitive)
		{
			if (electricityPack.voltage > this.getVoltage())
			{
				/**
				 * Since this block is indestructable, we have to delete it.
				 */
				this.worldObj.setBlockWithNotify(this.xCoord, this.yCoord, this.zCoord, 0);
				this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 1.5f, true);
				return;
			}
		}

		this.wattsReceived = Math.min(this.wattsReceived + electricityPack.getWatts(), this.getWattBuffer());
	}

	@Override
	public ElectricityPack getRequest()
	{
		if (this.getTurret() != null)
		{
			if (this.wattsReceived < this.getTurret().getRequest())
			{
				return new ElectricityPack(this.getTurret().getRequest() / this.getTurret().getVoltage(), this.getTurret().getVoltage());
			}
		}

		return new ElectricityPack();
	}

	@Override
	public double getWattBuffer()
	{
		if (this.getTurret() != null)
		{
			return this.getTurret().getRequest();
		}
		return 0;
	}

	public TileEntityBaseTurret getTurret()
	{
		TileEntity tileEntity = this.worldObj.getBlockTileEntity(this.xCoord + this.deployDirection.offsetX, this.yCoord + this.deployDirection.offsetY, this.zCoord + this.deployDirection.offsetZ);

		if (tileEntity instanceof TileEntityBaseTurret)
		{
			this.turret = (TileEntityBaseTurret) tileEntity;
		}
		else
		{
			this.turret = null;
		}

		return this.turret;
	}

	/**
	 * if a sentry is spawned above the stand it is removed
	 * 
	 * @return
	 */
	public boolean removeTurret()
	{
		TileEntity ent = worldObj.getBlockTileEntity(xCoord + deployDirection.offsetX, yCoord + deployDirection.offsetY, zCoord + deployDirection.offsetZ);
		if (ent instanceof TileEntityBaseTurret)
		{
			this.turret = null;
			return ((TileEntityBaseTurret) ent).destroy(false);
		}
		return false;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		if (side != ForgeDirection.DOWN && side != ForgeDirection.UP)
		{
			return 0;
		}
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		if (side != ForgeDirection.DOWN && side != ForgeDirection.UP)
		{
			return 15;
		}
		return 0;
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.turretPlatform.name");
	}

	public boolean isRunning()
	{
		return !this.isDisabled();
	}

	@Override
	public boolean hasAmmunition(ItemStack ammunitionStack)
	{
		for (int i = 0; i < this.UPGRADE_START_INDEX; i++)
		{
			ItemStack itemStack = this.containingItems[i];

			if (itemStack != null)
			{
				if (itemStack.isItemEqual(ammunitionStack))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean useAmmunition(ItemStack ammunitionStack)
	{
		for (int i = 0; i < this.UPGRADE_START_INDEX; i++)
		{
			ItemStack itemStack = this.containingItems[i];

			if (itemStack != null)
			{
				if (itemStack.isItemEqual(ammunitionStack))
				{
					this.decrStackSize(i, 1);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the amount of upgrades.
	 */
	public int getUpgrades(String name)
	{
		int count = 0;

		for (int i = UPGRADE_START_INDEX; i < 4; i++)
		{
			ItemStack itemStack = this.getStackInSlot(i);

			if (itemStack != null)
			{
				if (itemStack.getItem() instanceof IModifier)
				{
					if (name.equalsIgnoreCase(((IModifier) itemStack.getItem()).getName(itemStack)))
					{
						count++;
					}
				}
			}
		}

		return count;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		// Inventory
		NBTTagList var2 = nbt.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

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

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		// Inventory
		NBTTagList itemTag = new NBTTagList();
		for (int slots = 0; slots < this.containingItems.length; ++slots)
		{
			if (this.containingItems[slots] != null)
			{
				NBTTagCompound itemNbtData = new NBTTagCompound();
				itemNbtData.setByte("Slot", (byte) slots);
				this.containingItems[slots].writeToNBT(itemNbtData);
				itemTag.appendTag(itemNbtData);
			}
		}

		nbt.setTag("Items", itemTag);
	}

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

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{
		this.playersUsing++;
	}

	@Override
	public void closeChest()
	{
		this.playersUsing--;
	}
}