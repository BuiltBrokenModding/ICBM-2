package icbm.core.prefab;

import java.io.IOException;
import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.UniversalElectricity;
import universalelectricity.api.electricity.IVoltageInput;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.prefab.tile.IPlayerUsing;
import calclavia.lib.prefab.tile.TileElectrical;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/** @author Calclavia */
public abstract class TileICBM extends TileElectrical implements IPlayerUsing, IVoltageInput, IPacketReceiver
{
	public final HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

	@Override
	public long getVoltageInput(ForgeDirection direction)
	{
		return UniversalElectricity.DEFAULT_VOLTAGE * 2;
	}

	@Override
	public void onWrongVoltage(ForgeDirection direction, long voltage)
	{

	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!worldObj.isRemote)
		{
			for (EntityPlayer player : playersUsing)
			{
				PacketDispatcher.sendPacketToPlayer(getGUIPacket(), (Player) player);
			}
		}
	}

	protected Packet getGUIPacket()
	{
		return getDescriptionPacket();
	}

	@Override
	public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
	{
		try
		{
			this.onReceivePacket(data.readInt(), data, player, extra);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void onReceivePacket(int id, ByteArrayDataInput data, EntityPlayer player, Object... extra) throws IOException
	{
		if (id == -1)
		{
			if (data.readBoolean())
				this.playersUsing.add(player);
			else
				this.playersUsing.remove(player);
		}
	}

	@Override
	public HashSet<EntityPlayer> getPlayersUsing()
	{
		return this.playersUsing;
	}

}
