package icbm.explosion;

import icbm.core.ICBMCore;
import icbm.explosion.items.ItemLaserDesignator;
import icbm.explosion.items.ItemRadarGun;
import icbm.explosion.items.ItemRemoteDetonator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

import com.google.common.io.ByteArrayDataInput;

/** This class is used for sending and receiving packets between the server and the client. You can
 * directly use this by registering this packet manager with NetworkMod. Example:
 * 
 * @NetworkMod(channels = { "BasicComponents" }, clientSideRequired = true, serverSideRequired =
 * false, packetHandler = PacketManager.class)
 * 
 * Check out {@link #BasicComponents} for better reference.
 * 
 * @author Calclavia */
public class ICBMPacketHandler extends PacketManager
{
    public enum ZhaPinPacketType
    {
        UNSPECIFIED,
        TILEENTITY,
        RADAR_GUN,
        LASER_DESIGNATOR,
        REMOTE;

        public static ZhaPinPacketType get(int id)
        {
            if (id >= 0 && id < ZhaPinPacketType.values().length)
            {
                return ZhaPinPacketType.values()[id];
            }
            return UNSPECIFIED;
        }
    }

    @Override
    public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
    {
        try
        {
            ZhaPinPacketType icbmPacketType = ZhaPinPacketType.get(packetType);

            if (icbmPacketType == ZhaPinPacketType.RADAR_GUN)
            {
                if (player.inventory.getCurrentItem().getItem() instanceof ItemRadarGun)
                {
                    ItemStack itemStack = player.inventory.getCurrentItem();
                    ((ItemRadarGun) player.inventory.getCurrentItem().getItem()).setLink(itemStack, new Vector3(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()));
                    ICBMExplosion.itemRadarGun.discharge(itemStack, ItemRadarGun.YONG_DIAN_LIANG, true);
                }
            }
            else if (icbmPacketType == ZhaPinPacketType.LASER_DESIGNATOR)
            {
                if (player.inventory.getCurrentItem().getItem() instanceof ItemLaserDesignator)
                {
                    ItemStack itemStack = player.inventory.getCurrentItem();
                    Vector3 position = new Vector3(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

                    ((ItemLaserDesignator) ICBMExplosion.itemLaserDesignator).setLauncherCountDown(itemStack, 119);

                    player.worldObj.playSoundEffect(position.intX(), player.worldObj.getHeightValue(position.intX(), position.intZ()), position.intZ(), ICBMCore.PREFIX + "airstrike", 5.0F, (1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

                    player.worldObj.spawnEntityInWorld(new EntityLightBeam(player.worldObj, position, 5 * 20, 0F, 1F, 0F));

                    ICBMExplosion.itemRadarGun.discharge(itemStack, ItemLaserDesignator.YONG_DIAN_LIANG, true);
                }
            }
            else if (icbmPacketType == ZhaPinPacketType.REMOTE)
            {
                ItemStack itemStack = player.inventory.getCurrentItem();
                ICBMExplosion.itemRemoteDetonator.discharge(itemStack, ItemRemoteDetonator.YONG_DIAN_LIANG, true);
            }
        }
        catch (Exception e)
        {
            ICBMCore.LOGGER.severe("ICBM item packet receive failed!");
            e.printStackTrace();
        }
    }
}
