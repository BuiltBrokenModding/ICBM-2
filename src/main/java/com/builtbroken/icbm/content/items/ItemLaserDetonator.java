package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.launcher.ILauncher;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.map.radio.RadioRegistry;
import com.builtbroken.mc.prefab.hz.FakeRadioSender;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Extended version of {@link ItemRemoteDetonator} that can target blocks in a line of sight.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
@ItemWrapped(className = ".gen.ItemWrapperLaserDet", wrappers = "EnergyUE")
public class ItemLaserDetonator extends ItemRemoteDetonator
{
    public ItemLaserDetonator()
    {
        super(ICBM.DOMAIN, "laserDet");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            MovingObjectPosition objectMouseOver = player.rayTrace(200, 1);
            TileEntity tileEntity = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
            if (!(tileEntity instanceof ILauncher))
            {
                Engine.packetHandler.sendToServer(new PacketPlayerItem(player, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ));
            }
        }
        return stack;
    }

    @Override
    public void readPacketData(ByteBuf buf, EntityPlayer player, ItemStack stack)
    {
        if (!player.worldObj.isRemote)
        {
            Pos pos = new Pos(buf.readInt(), buf.readInt(), buf.readInt());
            int mode = getMode(stack);
            if (mode > 0)
            {
                float hz = getBroadCastHz(stack);
                if (mode == 1)
                {
                    RadioRegistry.popMessage(player.worldObj, new FakeRadioSender(player, stack, 2000), hz, "fireMissileAtTarget", getPassKey(stack), getGroupID(stack), getSiloName(stack), pos);
                }
                else if (mode == 2)
                {
                    RadioRegistry.popMessage(player.worldObj, new FakeRadioSender(player, stack, 2000), hz, "fireMissileGroupAtTarget", getPassKey(stack), getGroupID(stack), pos);
                    //TODO add selective first missile firing
                }
                //TODO add mode for chain firing
                //TODO add mode for group firing
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText("Not encoded for launch data! Use Command Silo Interface to encode with launch data..."));
            }
        }
    }
}
