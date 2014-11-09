package icbm.core.prefab;

import java.io.IOException;
import java.util.HashSet;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import com.google.common.io.ByteArrayDataInput;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.engine.ResonantEngine;
import resonant.lib.network.IPlayerUsing;
import resonant.lib.network.discriminator.PacketTile;
import resonant.lib.network.discriminator.PacketType;
import resonant.lib.network.handle.IPacketIDReceiver;
import resonant.lib.network.handle.IPacketReceiver;
import resonant.lib.content.prefab.java.TileElectric;
import resonant.lib.network.netty.AbstractPacket;

/** @author Calclavia */
public abstract class TileICBM extends TileElectric implements IPlayerUsing
{
    public final HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public TileICBM(Material material)
    {
        super(material);
    }

    @Override
    public void update()
    {
        super.update();

        if (!worldObj.isRemote && ticks() % 3 == 0)
        {
            for (EntityPlayer player : playersUsing)
            {
                ResonantEngine.instance.packetHandler.sendToPlayer(getGuiPacket(), (EntityPlayerMP) player);
            }
        }
    }

    /** Packet that will be send if the player has the GUI open */
    public AbstractPacket getGuiPacket()
    {
        return getDescPacket();
    }

    @Override
    public HashSet<EntityPlayer> getPlayersUsing()
    {
        return this.playersUsing;
    }
}
