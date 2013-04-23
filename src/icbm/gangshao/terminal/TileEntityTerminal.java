package icbm.gangshao.terminal;

import icbm.gangshao.ISpecialAccess;
import icbm.gangshao.ITerminal;
import icbm.gangshao.ZhuYaoGangShao;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import calclavia.lib.TileEntityUniversalRunnable;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class TileEntityTerminal extends TileEntityUniversalRunnable implements ISpecialAccess, IPacketReceiver, ITerminal
{
	public enum PacketType
	{
		GUI_EVENT, GUI_COMMAND, TERMINAL_OUTPUT, DESCRIPTION_DATA;
	}

	/**
	 * A list of everything typed inside the terminal.
	 */
	private final List<String> terminalOutput = new ArrayList<String>();

	/**
	 * A list of user access data.
	 */
	private final List<UserAccess> users = new ArrayList<UserAccess>();

	/**
	 * The amount of players using the console.
	 */
	public int playersUsing = 0;

	/**
	 * The amount of lines the terminal can store.
	 */
	public static final int SCROLL_SIZE = 15;

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
				if (this.ticks % 5 == 0)
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
	 * Sends all NBT data. Server -> Client
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, PacketType.DESCRIPTION_DATA.ordinal(), nbt);
	}

	/**
	 * Sends all Terminal data Server -> Client
	 */
	public void sendTerminalOutputToClients()
	{
		List data = new ArrayList();
		data.add(PacketType.TERMINAL_OUTPUT.ordinal());
		data.add(this.getTerminalOuput().size());
		data.addAll(this.getTerminalOuput());

		Packet packet = PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, data.toArray());
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
			Packet packet = PacketManager.getPacket(ZhuYaoGangShao.CHANNEL, this, PacketType.GUI_COMMAND.ordinal(), entityPlayer.username, cmdInput);
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
							this.playersUsing++;
							this.sendTerminalOutputToClients();
						}
						else
						{
							this.playersUsing--;
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
			FMLLog.severe("ICBM: Failed to receive packet for terminal.");
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
		return this.users.add(new UserAccess(player, lvl, save));
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

		this.wattsReceived = nbt.getDouble("wattsReceived");

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

		nbt.setDouble("wattsReceived", this.wattsReceived);

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
