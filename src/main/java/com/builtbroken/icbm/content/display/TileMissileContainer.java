package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;

/**
 * Created by robert on 1/18/2015.
 */
public class TileMissileContainer extends TileModuleMachine
{
    public TileMissileContainer(String name, Material material)
    {
        this(name, material, 1);
    }

    public TileMissileContainer(String name, Material material, int slots)
    {
        super(name, material);
        this.addInventoryModule(slots);
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, getSaveData());
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (!player.isSneaking())
        {
            ItemStack heldItem = player.getHeldItem();

            if (heldItem == null)
            {
                if (getMissile() != null)
                {
                    if (isServer())
                    {
                        //TODO add translation
                        player.addChatComponentMessage(new ChatComponentText("*Removed Missile*"));
                        player.inventory.mainInventory[player.inventory.currentItem] = getMissile().toStack();
                        setInventorySlotContents(0, null);
                        player.inventoryContainer.detectAndSendChanges();
                        sendDescPacket();
                    }
                    return true;
                }
            }
            else if (heldItem.getItem() instanceof ItemMissile)
            {
                if (isServer())
                {
                    Missile missile = MissileModuleBuilder.INSTANCE.buildMissile(heldItem);
                    if (canAcceptMissile(missile))
                    {
                        //TODO add translation
                        player.addChatComponentMessage(new ChatComponentText("*Added Missile*"));
                        ItemStack stack = heldItem.copy();
                        stack.stackSize = 1;
                        setInventorySlotContents(0, stack);
                        if (!player.capabilities.isCreativeMode)
                        {
                            heldItem.stackSize--;
                            if (heldItem.stackSize <= 0)
                            {
                                player.inventory.mainInventory[player.inventory.currentItem] = null;
                            }
                            player.inventoryContainer.detectAndSendChanges();
                        }
                        sendDescPacket();
                    }
                    else
                    {
                        //TODO add translation
                        player.addChatComponentMessage(new ChatComponentText("*Missile will not fit*"));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean canAcceptMissile(Missile missile)
    {
        return missile != null;
    }

    public Missile getMissile()
    {
        return getMissileItem() != null ? MissileModuleBuilder.INSTANCE.buildMissile(getMissileItem()) : null;
    }

    public ItemStack getMissileItem()
    {
        return getStackInSlot(0);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }
}
