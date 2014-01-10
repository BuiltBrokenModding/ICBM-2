package icbm.explosion.machines;

import icbm.api.ILauncherContainer;
import icbm.api.ILauncherController;
import icbm.api.IMissile;
import icbm.api.ITier;
import icbm.api.explosion.ExplosionEvent.ExplosivePreDetonationEvent;
import icbm.api.explosion.ExplosiveType;
import icbm.core.ICBMCore;
import icbm.core.Settings;
import icbm.explosion.ICBMExplosion;
import icbm.explosion.missile.ExplosiveRegistry;
import icbm.explosion.missile.missile.EntityMissile;
import icbm.explosion.missile.missile.ItemMissile;
import icbm.explosion.missile.missile.Missile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.api.vector.Vector3;
import universalelectricity.api.vector.VectorHelper;
import calclavia.lib.multiblock.link.IBlockActivate;
import calclavia.lib.multiblock.link.IMultiBlock;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.tile.IRotatable;
import calclavia.lib.prefab.tile.TileAdvanced;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the base of the missile launcher
 * 
 * @author Calclavia
 */
public class TileLauncherBase extends TileAdvanced implements IPacketReceiver, ILauncherContainer, IRotatable, ITier, IMultiBlock, IInventory, IBlockActivate
{
	// The missile that this launcher is holding
	public IMissile missile = null;

	// The connected missile launcher frame
	public TileEntitySupportFrame supportFrame = null;

	private ItemStack[] containingItems = new ItemStack[1];

	// The tier of this launcher base
	private int tier = 0;

	private byte facingDirection = 3;

	private boolean packetGengXin = true;

	/** Returns the number of slots in the inventory. */
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

	/** Returns the name of the inventory. */
	@Override
	public String getInvName()
	{
		return "Launcher Platform";
	}

	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner
	 * uses this to count ticks and creates a new spawn inside its implementation.
	 */
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.supportFrame == null)
		{
			for (byte i = 2; i < 6; i++)
			{
				Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
				position.modifyPositionFromSide(ForgeDirection.getOrientation(i));

				TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());

				if (tileEntity instanceof TileEntitySupportFrame)
				{
					this.supportFrame = (TileEntitySupportFrame) tileEntity;
					this.supportFrame.setDirection(VectorHelper.getOrientationFromSide(ForgeDirection.getOrientation(i), ForgeDirection.NORTH));
				}
			}
		}
		else
		{
			if (this.supportFrame.isInvalid())
			{
				this.supportFrame = null;
			}
			else if (this.packetGengXin || this.ticks % (20 * 30) == 0 && this.supportFrame != null && !this.worldObj.isRemote)
			{
				PacketHandler.sendPacketToClients(this.supportFrame.getDescriptionPacket());
			}
		}

		if (!this.worldObj.isRemote)
		{
			this.setMissile();

			if (this.packetGengXin || this.ticks % (20 * 30) == 0)
			{
				PacketHandler.sendPacketToClients(this.getDescriptionPacket());
				this.packetGengXin = false;
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return ICBMCore.PACKET_TILE.getPacket(this, this.facingDirection, this.tier);
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		try
		{

			this.facingDirection = data.readByte();
			this.tier = data.readInt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setMissile()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.containingItems[0] != null)
			{
				if (this.containingItems[0].getItem() instanceof ItemMissile)
				{
					int haoMa = this.containingItems[0].getItemDamage();

					if (ExplosiveRegistry.get(haoMa) instanceof Missile)
					{
						Missile missile = (Missile) ExplosiveRegistry.get(haoMa);

						ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ExplosiveType.AIR, missile);
						MinecraftForge.EVENT_BUS.post(evt);

						if (!evt.isCanceled())
						{
							if (this.missile == null)
							{
								Vector3 startingPosition = new Vector3((this.xCoord + 0.5f), (this.yCoord + 1.8f), (this.zCoord + 0.5f));
								this.missile = new EntityMissile(this.worldObj, startingPosition, new Vector3(this), haoMa);
								this.worldObj.spawnEntityInWorld((Entity) this.missile);
								return;
							}

							if (this.missile != null)
							{
								if (this.missile.getExplosiveType().getID() == haoMa)
								{
									return;
								}
							}
						}
					}
				}
			}

			if (this.missile != null)
			{
				((Entity) this.missile).setDead();
			}

			this.missile = null;
		}
	}

	/**
	 * Launches the missile
	 * 
	 * @param target - The target in which the missile will land in
	 */
	public void launchMissile(Vector3 target, int gaoDu)
	{
		// Apply inaccuracy
		float inaccuracy;

		if (this.supportFrame != null)
		{
			inaccuracy = this.supportFrame.getInaccuracy();
		}
		else
		{
			inaccuracy = 30f;
		}

		inaccuracy *= (float) Math.random() * 2 - 1;

		target.x += inaccuracy;
		target.z += inaccuracy;

		this.decrStackSize(0, 1);
		this.missile.launch(target, gaoDu);
		this.missile = null;
	}

	// Checks if the missile target is in range
	public boolean isInRange(Vector3 target)
	{
		if (target != null)
			return !shiTaiYuan(target) && !shiTaiJin(target);

		return false;
	}

	/**
	 * Checks to see if the target is too close.
	 * 
	 * @param target
	 * @return
	 */
	public boolean shiTaiJin(Vector3 target)
	{
		// Check if it is greater than the minimum range
		if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 10)
		{
			return true;
		}

		return false;
	}

	// Is the target too far?
	public boolean shiTaiYuan(Vector3 target)
	{
		// Checks if it is greater than the maximum range for the launcher base
		if (this.tier == 0)
		{
			if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN / 10)
			{
				return false;
			}
		}
		else if (this.tier == 1)
		{
			if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN / 5)
			{
				return false;
			}
		}
		else if (this.tier == 2)
		{
			if (Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < Settings.DAO_DAN_ZUI_YUAN)
			{
				return false;
			}
		}

		return true;
	}

	/** Reads a tile entity from NBT. */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagList var2 = nbt.getTagList("Items");

		this.tier = nbt.getInteger("tier");
		this.facingDirection = nbt.getByte("facingDirection");

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

	/** Writes a tile entity to NBT. */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger("tier", this.tier);
		nbt.setByte("facingDirection", this.facingDirection);

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

		nbt.setTag("Items", var2);
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

	/** Do not make give this method the name canInteractWith because it clashes with Container */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
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
	public boolean onActivated(EntityPlayer entityPlayer)
	{
		if (entityPlayer.inventory.getCurrentItem() != null && this.getStackInSlot(0) == null)
		{
			if (entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemMissile)
			{
				this.setInventorySlotContents(0, entityPlayer.inventory.getCurrentItem());
				entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
				return true;
			}
		}

		entityPlayer.openGui(ICBMExplosion.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void invalidate()
	{
		if (this.missile != null)
		{
			((Entity) this.missile).setDead();
		}

		super.invalidate();
	}

	@Override
	public Vector3[] getMultiBlockVectors()
	{
		if (this.facingDirection == 3 || this.facingDirection == 2)
		{
			return new Vector3[] { new Vector3(1, 0, 0), new Vector3(1, 1, 0), new Vector3(1, 2, 0), new Vector3(-1, 0, 0), new Vector3(-1, 1, 0), new Vector3(-1, 2, 0) };
		}
		else
		{
			return new Vector3[] { new Vector3(0, 0, 1), new Vector3(0, 1, 1), new Vector3(0, 2, 1), new Vector3(0, 0, -1), new Vector3(0, 1, -1), new Vector3(0, 2, -1) };
		}
	}

	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.facingDirection);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection)
	{
		this.facingDirection = (byte) facingDirection.ordinal();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack)
	{
		return itemStack.getItem() instanceof ItemMissile;
	}

	@Override
	public IMissile getContainingMissile()
	{
		return this.missile;
	}

	@Override
	public void setContainingMissile(IMissile missile)
	{
		this.missile = missile;
	}

	@Override
	public ILauncherController getController()
	{
		for (byte i = 2; i < 6; i++)
		{
			Vector3 position = new Vector3(this).modifyPositionFromSide(ForgeDirection.getOrientation(i));

			TileEntity tileEntity = position.getTileEntity(this.worldObj);

			if (tileEntity instanceof ILauncherController)
			{
				return (ILauncherController) tileEntity;
			}
		}

		return null;
	}

}
