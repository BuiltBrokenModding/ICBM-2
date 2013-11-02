package icbm.sentry.terminal;

import icbm.core.ICBMCore;
import icbm.sentry.ISpecialAccess;
import icbm.sentry.access.AccessLevel;
import icbm.sentry.access.UserAccess;
import icbm.sentry.terminal.command.CommandRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.compatibility.TileEntityUniversalElectrical;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/** @author Calclavia, DarkGuardsman */
public abstract class TileEntityTerminal extends TileEntityUniversalElectrical implements ISpecialAccess, IPacketReceiver, ITerminal
{
	public enum TerminalPacketType
	{
		GUI_EVENT, GUI_COMMAND, TERMINAL_OUTPUT, DESCRIPTION_DATA;
	}

	/** A list of everything typed inside the terminal */
	private final List<String> terminalOutput = new ArrayList<String>();

	/** A list of user access data. */
	private final List<UserAccess> users = new ArrayList<UserAccess>();

	/** The amount of lines the terminal can store. */
	public static final int SCROLL_SIZE = 15;

	/** Used on client side to determine the scroll of the terminal. */
	private int scroll = 0;

	public final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!this.worldObj.isRemote)
		{
			if (this.ticks % 3 == 0)
			{
				for (EntityPlayer player : this.playersUsing)
				{
					PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) player);
				}
			}
		}
	}

	/** Channel to be used for packets */
	public abstract String getChannel();

	/** Sends all NBT data. Server -> Client */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return PacketManager.getPacket(this.getChannel(), this, TerminalPacketType.DESCRIPTION_DATA.ordinal(), nbt);
	}

	/** Sends all Terminal data Server -> Client */
	public void sendTerminalOutputToClients()
	{
		List data = new ArrayList();
		data.add(TerminalPacketType.TERMINAL_OUTPUT.ordinal());
		data.add(this.getTerminalOuput().size());
		data.addAll(this.getTerminalOuput());

		Packet packet = PacketManager.getPacket(this.getChannel(), this, data.toArray());

		for (EntityPlayer player : this.playersUsing)
		{
			PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
		}
	}

	/** Send a terminal command Client -> server */
	public void sendCommandToServer(EntityPlayer entityPlayer, String cmdInput)
	{
		if (this.worldObj.isRemote)
		{
			Packet packet = PacketManager.getPacket(this.getChannel(), this, TerminalPacketType.GUI_COMMAND.ordinal(), entityPlayer.username, cmdInput);
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetID, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			TerminalPacketType packetType = TerminalPacketType.values()[dataStream.readInt()];

			switch (packetType)
			{
				case DESCRIPTION_DATA:
				{
					if (this.worldObj.isRemote)
					{
						short size = dataStream.readShort();

						if (size > 0)
						{
							byte[] byteCode = new byte[size];
							dataStream.readFully(byteCode);
							this.readFromNBT(CompressedStreamTools.decompress(byteCode));
						}
					}

					break;
				}
				case GUI_COMMAND:
				{
					if (!this.worldObj.isRemote)
					{
						CommandRegistry.onCommand(this.worldObj.getPlayerEntityByName(dataStream.readUTF()), this, dataStream.readUTF());
						this.sendTerminalOutputToClients();
					}
					break;
				}
				case GUI_EVENT:
				{
					if (!this.worldObj.isRemote)
					{
						if (dataStream.readBoolean())
						{
							this.playersUsing.add(player);
							this.sendTerminalOutputToClients();
						}
						else
						{
							this.playersUsing.remove(player);
						}
					}
					break;
				}
				case TERMINAL_OUTPUT:
				{
					if (this.worldObj.isRemote)
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
							this.setScroll(this.getTerminalOuput().size() - SCROLL_SIZE);
						}
					}

					break;
				}
				default:
					break;
			}
		}
		catch (Exception e)
		{
			ICBMCore.LOGGER.severe("Terminal error: " + this.toString());
			e.printStackTrace();
		}
	}

	@Override
	public AccessLevel getUserAccess(String username)
	{
		for (int i = 0; i < this.users.size(); i++)
		{
			if (this.users.get(i).username.equalsIgnoreCase(username))
			{
				return this.users.get(i).level;
			}
		}
		return AccessLevel.NONE;
	}

	public boolean canUserAccess(String username)
	{
		return (this.getUserAccess(username).ordinal() >= AccessLevel.USER.ordinal());
	}

	@Override
	public List<UserAccess> getUsers()
	{
		return this.users;
	}

	@Override
	public List<UserAccess> getUsersWithAcess(AccessLevel level)
	{
		List<UserAccess> players = new ArrayList<UserAccess>();

		for (int i = 0; i < this.users.size(); i++)
		{
			UserAccess ref = this.users.get(i);

			if (ref.level == level)
			{
				players.add(ref);
			}
		}
		return players;

	}

	@Override
	public boolean addUserAccess(String player, AccessLevel lvl, boolean save)
	{
		this.removeUserAccess(player);
		boolean bool = this.users.add(new UserAccess(player, lvl, save));
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		return bool;
	}

	@Override
	public boolean removeUserAccess(String player)
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

			boolean bool = this.users.removeAll(removeList);
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			return bool;
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
	public void scroll(int amount)
	{
		this.setScroll(this.scroll + amount);
	}

	@Override
	public void setScroll(int length)
	{
		this.scroll = Math.max(Math.min(length, this.getTerminalOuput().size()), 0);
	}

	@Override
	public int getScroll()
	{
		return this.scroll;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		// Read user list
		this.users.clear();

		NBTTagList userList = nbt.getTagList("Users");

		for (int i = 0; i < userList.tagCount(); ++i)
		{
			NBTTagCompound var4 = (NBTTagCompound) userList.tagAt(i);
			this.users.add(UserAccess.loadFromNBT(var4));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		// Write user list
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

		nbt.setTag("Users", usersTag);
	}
}
