package icbm.contraption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/**
 * This class is used for sending and receiving packets between the server and the client. You can
 * directly use this by registering this packet manager with NetworkMod. Example:
 * 
 * @NetworkMod(channels = { "BasicComponents" }, clientSideRequired = true, serverSideRequired =
 * false, packetHandler = PacketManager.class)
 * 
 * Check out {@link #BasicComponents} for better reference.
 * 
 * @author Calclavia
 */
public class ContraptionPacketHandler extends PacketManager
{
	public enum WanYiPacketType
	{
		UNSPECIFIED, TILEENTITY, HUO_LUAN;

		public static WanYiPacketType get(int id)
		{
			if (id >= 0 && id < WanYiPacketType.values().length)
			{
				return WanYiPacketType.values()[id];
			}
			return UNSPECIFIED;
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			WanYiPacketType icbmPacketType = WanYiPacketType.get(packetType);

			if (icbmPacketType == WanYiPacketType.HUO_LUAN)
			{
				if (player.inventory.getCurrentItem().getItem() instanceof ItemSignalDisrupter)
				{
					ItemStack itemStack = player.inventory.getCurrentItem();

					((ItemSignalDisrupter) itemStack.getItem()).setFrequency(dataStream.readShort(), itemStack);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
