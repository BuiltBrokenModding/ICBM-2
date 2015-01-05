package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.region.Cuboid;
import com.builtbroken.mc.lib.transform.vector.Vector3;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Simple display table to test to make sure missiles are rendering correctly
 * Later will be changed to only render micro and small missiles
 * Created by robert on 12/31/2014.
 */
public class TileMissileDisplay extends Tile implements IPacketReceiver
{
    private Missile missile = null;

    public TileMissileDisplay()
    {
        super(Material.circuits);
        this.renderTileEntity = true;
        this.isOpaque = true;
        this.bounds = new Cuboid(0, 0, 0, 1, .4, 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("missileItem"))
        {
            setMissile(MissileModuleBuilder.INSTANCE.buildMissile(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("missileItem"))));
        }
        else
        {
            setMissile(null);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (getMissile() != null)
        {
            nbt.setTag("missileItem", getMissile().toStack().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public PacketTile getDescPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new PacketTile(this, tag);
    }

    public void updateClient()
    {
        Engine.instance.packetHandler.sendToAllAround(getDescPacket(), (TileEntity) this);
    }

    @Override
    public void read(ByteBuf buf, EntityPlayer player, PacketType packet)
    {
        System.out.println("Packet Received");
        readFromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vector3 pos, float frame, int pass)
    {
        if (this.getWorldObj() != null && getMissile() != null)
        {
            RenderItemOverlayUtility.renderItem(getWorldObj(), ForgeDirection.UNKNOWN, missile.toStack(), pos.add(0.5), 0, 0);
        }
    }

    public void setMissile(Missile missile)
    {
        this.missile = missile;
    }

    @Override
    public boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        if (isServer())
        {
            ItemStack stack = player.getHeldItem();
            if (getMissile() != null)
            {
                if (stack == null)
                {
                    player.addChatComponentMessage(new ChatComponentText("Removed Missile"));
                    player.inventory.mainInventory[player.inventory.currentItem] = getMissile().toStack();
                    setMissile(null);
                    player.inventoryContainer.detectAndSendChanges();
                    updateClient();
                    return true;
                }
            }
            else if (stack.getItem() instanceof ItemMissile)
            {
                player.addChatComponentMessage(new ChatComponentText("Added Missile"));
                setMissile(MissileModuleBuilder.INSTANCE.buildMissile(stack));
                if (!player.capabilities.isCreativeMode)
                {
                    stack.stackSize--;
                    if (stack.stackSize <= 0)
                    {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                    player.inventoryContainer.detectAndSendChanges();
                    updateClient();
                }
                return true;
            }
        }
        return false;
    }

    public Missile getMissile()
    {
        return missile;
    }


}
