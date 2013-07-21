package icbm.gangshao.platform;

import icbm.gangshao.IAmmunition;
import icbm.gangshao.ITurretUpgrade;
import icbm.gangshao.ProjectileType;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.damage.IHealthTile;
import icbm.gangshao.terminal.TileEntityTerminal;
import icbm.gangshao.turret.ItemAmmo.AmmoType;
import icbm.gangshao.turret.TPaoTaiBase;
import icbm.gangshao.turret.upgrades.ItPaoTaiUpgrades.TurretUpgradeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.electricity.ElectricityPack;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.CustomDamageSource;

/**
 * Turret Platform
 * 
 * @author Calclavia
 * 
 */
public class TPaoTaiZhan extends TileEntityTerminal implements IInventory
{
	/** The turret linked to this platform. */
	private TPaoTaiBase cachedTurret = null;
	/** The start index of the upgrade slots for the turret. */
	public static final int UPGRADE_START_INDEX = 12;
	private static final int TURRET_UPGADE_SLOTS = 3;

	/**
	 * The first 12 slots are for ammunition. The last 4 slots are for upgrades.
	 */
	public ItemStack[] containingItems = new ItemStack[UPGRADE_START_INDEX + 4];

	private float prevWatts;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.prevWatts != this.getEnergyStored())
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}

		/**
		 * Consume electrical items.
		 */
		if (!this.worldObj.isRemote)
		{
			for (int i = 0; i < UPGRADE_START_INDEX; i++)
			{
				if (this.getEnergyStored() >= this.getRequest(null))
				{
					break;
				}

				this.receiveElectricity(ElectricItemHelper.dischargeItem(this.getStackInSlot(i), this.getRequest(null)), true);
			}
		}
	}

	@Override
	public float receiveElectricity(ElectricityPack receive, boolean doReceive)
	{
		this.prevWatts = this.getEnergyStored();

		if (doReceive)
		{
			/** Creates an explosion if the voltage is too high. */
			if (UniversalElectricity.isVoltageSensitive)
			{
				if (receive.voltage > this.getVoltage())
				{
					TPaoTaiBase turret = this.getTurret();

					if (turret != null && turret instanceof IHealthTile)
					{
						((IHealthTile) this.cachedTurret).onDamageTaken(CustomDamageSource.electrocution, Integer.MAX_VALUE);
					}

					return 0;
				}
			}
		}

		float returnValue = super.receiveElectricity(receive, doReceive);

		if ((this.prevWatts <= this.getRequest(null) && this.getEnergyStored() >= this.getRequest(null)) && !(this.prevWatts == this.getEnergyStored()))
		{
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		}

		return returnValue;
	}

	@Override
	public float getRequest(ForgeDirection direction)
	{
		if (this.getTurret() != null)
		{
			if (this.getEnergyStored() < this.getTurret().getFiringRequest())
			{
				return Math.max(this.getTurret().getFiringRequest(), 0);
			}
		}

		return 0;
	}

	/** Gets the turret instance linked to this platform */
	public TPaoTaiBase getTurret()
	{
		if (this.cachedTurret == null || this.cachedTurret.isInvalid() || !(new Vector3(this.cachedTurret).equals(new Vector3(this).modifyPositionFromSide(this.getTurretDirection()))))
		{
			TileEntity tileEntity = new Vector3(this).modifyPositionFromSide(this.getTurretDirection()).getTileEntity(this.worldObj);

			if (tileEntity instanceof TPaoTaiBase)
			{
				this.cachedTurret = (TPaoTaiBase) tileEntity;
			}
			else
			{
				this.cachedTurret = null;
			}
		}

		return this.cachedTurret;
	}

	/**
	 * if a sentry is spawned above the stand it is removed
	 * 
	 * @return
	 */
	public boolean destroyTurret()
	{
		TileEntity ent = this.worldObj.getBlockTileEntity(this.xCoord + this.getTurretDirection().offsetX, this.yCoord + this.getTurretDirection().offsetY, this.zCoord + this.getTurretDirection().offsetZ);

		if (ent instanceof TPaoTaiBase)
		{
			this.cachedTurret = null;
			((TPaoTaiBase) ent).destroy(false);
			return true;
		}

		return false;
	}

	public boolean destroy(boolean doExplosion)
	{
		if (doExplosion)
		{
			this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 2f, true);
		}

		if (!this.worldObj.isRemote)
		{
			this.getBlockType().dropBlockAsItem(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), 0);
		}

		return this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
	}

	@Override
	public String getInvName()
	{
		return this.getBlockType().getLocalizedName();
	}

	public boolean isRunning()
	{
		return (this.getTurret() != null && this.getEnergyStored() >= this.getTurret().getFiringRequest());
	}

	public ItemStack hasAmmunition(ProjectileType projectileType)
	{
		for (int i = 0; i < TPaoTaiZhan.UPGRADE_START_INDEX; i++)
		{
			ItemStack itemStack = this.containingItems[i];

			if (itemStack != null)
			{
				Item item = Item.itemsList[itemStack.itemID];

				if (item instanceof IAmmunition && ((IAmmunition) item).getType(itemStack) == projectileType)
				{
					return itemStack;
				}
			}
		}

		return null;
	}

	public boolean useAmmunition(ItemStack ammoStack)
	{
		if (ammoStack != null)
		{
			if (ammoStack.getItemDamage() == AmmoType.BULLETINF.ordinal())
			{
				return true;
			}

			for (int i = 0; i < TPaoTaiZhan.UPGRADE_START_INDEX; i++)
			{
				ItemStack itemStack = this.containingItems[i];

				if (itemStack != null)
				{
					if (itemStack.isItemEqual(ammoStack))
					{
						this.decrStackSize(i, 1);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Gets the change for the upgrade type 100% = 1.0 */
	public int getUpgradeCount(TurretUpgradeType type)
	{
		int count = 0;

		for (int i = UPGRADE_START_INDEX; i < UPGRADE_START_INDEX + TURRET_UPGADE_SLOTS; i++)
		{
			ItemStack itemStack = this.getStackInSlot(i);

			if (itemStack != null)
			{
				if (itemStack.getItem() instanceof ITurretUpgrade)
				{
					if (((ITurretUpgrade) itemStack.getItem()).getType(itemStack) == type)
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

	/** Returns the stack in slot i */
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
		return true;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	@Override
	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
	{
		if (slotID < UPGRADE_START_INDEX && itemStack.getItem() instanceof IAmmunition)
		{
			return true;
		}

		return false;
	}

	@Override
	public void onInventoryChanged()
	{
		super.onInventoryChanged();
	}

	@Override
	public String getChannel()
	{
		return ZhuYaoGangShao.CHANNEL;
	}

	/**
	 * @return True on successful drop.
	 */
	public boolean addStackToInventory(ItemStack itemStack)
	{
		for (int i = 0; i < UPGRADE_START_INDEX; i++)
		{
			ItemStack checkStack = this.getStackInSlot(i);

			if (itemStack.stackSize <= 0)
			{
				return true;
			}

			if (checkStack == null)
			{
				this.setInventorySlotContents(i, itemStack);
				return true;
			}
			else if (checkStack.isItemEqual(itemStack))
			{
				int inputStack = Math.min(checkStack.stackSize + itemStack.stackSize, checkStack.getMaxStackSize()) - checkStack.stackSize;
				itemStack.stackSize -= inputStack;
				checkStack.stackSize += inputStack;
				this.setInventorySlotContents(i, checkStack);
			}
		}

		return false;
	}

	@Override
	public float getProvide(ForgeDirection direction)
	{
		return 0;
	}

	@Override
	public float getMaxEnergyStored()
	{
		if (this.getTurret() != null)
		{
			return Math.max(this.getTurret().getFiringRequest(), 0) * 2;
		}

		return 0;
	}

	/**
	 * Deploy direction of the sentry
	 */
	public ForgeDirection getTurretDirection()
	{
		return ForgeDirection.UP;
	}
}