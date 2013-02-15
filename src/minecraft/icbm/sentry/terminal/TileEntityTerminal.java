package icbm.sentry.terminal;

import icbm.sentry.ICBMSentry;
import icbm.sentry.TileEntityIC2Runnable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class TileEntityTerminal extends TileEntityIC2Runnable implements IInventory, ISpecialAccess, ISidedInventory, IPacketReceiver, ITerminal
{
	public enum PacketType
	{
		GUI_EVENT, GUI_COMMAND, TERMINAL_OUTPUT, DESCRIPTION_DATA;
	}

	/**
	 * The start index of the upgrade slots for the turret.
	 */
	public static final int UPGRADE_START_INDEX = 12;

	/**
	 * The first 12 slots are for ammunition. The last 4 slots are for upgrades.
	 */
	public ItemStack[] containingItems = new ItemStack[UPGRADE_START_INDEX + 4];

	/**
	 * A list of everything typed inside the terminal.
	 */
	private final List<String> terminalOutput = new ArrayList<String>();

	/**
	 * A list of user access data.
	 */
	public final List<UserAccess> users = new ArrayList<UserAccess>();

	/**
	 * The amount of players using the console.
	 */
	public int playersUsing = 0;

	/**
	 * The amount of lines the terminal can store.
	 */
	public static final int SCROLL_SIZE = 14;

	/**
	 * Used on client side to determine the scroll of the terminal.
	 */
	private int scroll = 0;

	@Override
	public void initiate()
	{
		super.initiate();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (this.playersUsing > 0)
			{
				if (this.ticks % 3 == 0)
				{
					PacketManager.sendPacketToClients(this.getDescriptionPacket(), this.worldObj, new Vector3(this), 12);
				}
			}
		}
	}

	/**
	 * Packet Methods
	 */
	/**
	 * Server -> Client
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return PacketManager.getPacket(ICBMSentry.CHANNEL, this, PacketType.DESCRIPTION_DATA.ordinal(), nbt);
	}

	/**
	 * Server -> Client
	 */
	public void sendTerminalOutputToClients()
	{
		List data = new ArrayList();
		data.add(PacketType.TERMINAL_OUTPUT.ordinal());
		data.add(this.getTerminalOuput().size());
		data.addAll(this.getTerminalOuput());

		Packet packet = PacketManager.getPacket(ICBMSentry.CHANNEL, this, data.toArray());
		PacketManager.sendPacketToClients(packet, worldObj, new Vector3(this), 10);
	}

	/**
	 * Client -> Server
	 * 
	 * @param entityPlayer
	 * @param cmdInput
	 */
	public void sendCommandToServer(EntityPlayer entityPlayer, String cmdInput)
	{
		if (this.worldObj.isRemote)
		{
			Packet packet = PacketManager.getPacket(ICBMSentry.CHANNEL, this, PacketType.GUI_COMMAND.ordinal(), entityPlayer.username, cmdInput);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetID, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			PacketType packetType = PacketType.values()[dataStream.readInt()];

			switch (packetType)
			{
				case DESCRIPTION_DATA:
				{
					short size = dataStream.readShort();

					if (size > 0)
					{
						byte[] byteCode = new byte[size];
						dataStream.readFully(byteCode);
						this.readFromNBT(CompressedStreamTools.decompress(byteCode));
					}

					break;
				}
				case GUI_COMMAND:
				{
					CommandRegistry.onCommand(this.worldObj.getPlayerEntityByName(dataStream.readUTF()), this, dataStream.readUTF());
					this.sendTerminalOutputToClients();
					break;
				}
				case GUI_EVENT:
				{
					if (dataStream.readBoolean())
					{
						this.playersUsing++;
						this.sendTerminalOutputToClients();
					}
					else
					{
						this.playersUsing--;
					}
					break;
				}
				case TERMINAL_OUTPUT:
				{
					int size = dataStream.readInt();

					List<String> oldTerminalOutput = new ArrayList(this.terminalOutput);
					this.terminalOutput.clear();

					for (int i = 0; i < size; i++)
					{
						this.terminalOutput.add(dataStream.readUTF());
					}

					if (!this.terminalOutput.equals(oldTerminalOutput) && this.terminalOutput.size() != oldTerminalOutput.size())
					{
						this.scroll = Math.max(this.getTerminalOuput().size() - SCROLL_SIZE, 0);
					}

					break;
				}
				default:
					break;
			}
		}
		catch (Exception e)
		{
			FMLLog.severe("ICBM: Failed to receive packet for terminal.");
			e.printStackTrace();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		this.wattsReceived = nbt.getDouble("wattsReceived");

		// Read user list
		this.users.clear();

		NBTTagList userList = nbt.getTagList("UserList");

		for (int i = 0; i < userList.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) userList.tagAt(i);
			this.users.add(UserAccess.loadFromNBT(var4));
		}

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

		nbt.setDouble("wattsReceived", this.wattsReceived);
		// write user list
		NBTTagList usersTag = new NBTTagList();
		for (int player = 0; player < this.users.size(); ++player)
		{
			UserAccess access = this.users.get(player);
			if (access != null && access.shouldSave)
			{
				NBTTagCompound accessData = new NBTTagCompound();
				access.writeToNBT(accessData);
				usersTag.appendTag(accessData);
			}
		}

		nbt.setTag("UserList", usersTag);

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

	public int getSizeInventory()
	{
		return this.containingItems.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 0;
	}

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

	@Override
	public AccessLevel getPlayerAccess(EntityPlayer player)
	{
		for (int i = 0; i < this.users.size(); i++)
		{
			if (this.users.get(i).username.equalsIgnoreCase(player.username))
			{
				return this.users.get(i).access;
			}
		}
		return AccessLevel.NONE;
	}

	@Override
	public String getInvName()
	{
		return "console";
	}

	@Override
	public List<UserAccess> getUsers()
	{

		return this.users;
	}

	public List<UserAccess> getUsersWithAcess(AccessLevel lvl)
	{
		List<UserAccess> players = new ArrayList<UserAccess>();
		for (int i = 0; i < this.users.size(); i++)
		{
			UserAccess ref = this.users.get(i);
			if (ref.access == lvl)
			{
				players.add(ref);
			}
		}
		return players;

	}

	@Override
	public boolean setAccess(String player, AccessLevel lvl, boolean save)
	{
		this.removeUser(player);
		return this.users.add(new UserAccess(player, lvl, save));
	}

	@Override
	public boolean removeUser(String player)
	{
		List<UserAccess> removeList = new ArrayList<UserAccess>();
		for (int i = 0; i < this.users.size(); i++)
		{
			UserAccess ref = this.users.get(i);
			if (ref.username.equalsIgnoreCase(player))
			{
				removeList.add(ref);
			}
		}
		if (removeList != null && removeList.size() > 0)
		{
			return this.users.removeAll(removeList);
		}
		return false;
	}

	@Override
	public List<String> getTerminalOuput()
	{
		return this.terminalOutput;
	}

	@Override
	public boolean addToConsole(String msg)
	{
		if (!this.worldObj.isRemote)
		{
			int usedLines = 0;

			msg.trim();
			if (msg.length() > 23)
			{
				msg = msg.substring(0, 22);
			}

			this.getTerminalOuput().add(msg);
			this.sendTerminalOutputToClients();
			return true;
		}

		return false;
	}

	@Override
	public void scrollUp(int amount)
	{
		this.scroll = Math.max(this.scroll - amount, 0);
	}

	@Override
	public void scrollDown(int amount)
	{
		this.scroll = Math.min(this.scroll + amount, this.getTerminalOuput().size());
	}

	@Override
	public int getScroll()
	{
		return this.scroll;
	}
}
