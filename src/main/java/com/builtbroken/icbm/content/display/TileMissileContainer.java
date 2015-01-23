package com.builtbroken.icbm.content.display;

import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.missile.ItemMissile;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.AbstractPacket;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by robert on 1/18/2015.
 */
public class TileMissileContainer extends TileModuleMachine implements IPacketReceiver
{
    private Missile missile = null;

    public TileMissileContainer(String name, Material material)
    {
        super(name, material);
        this.addInventoryModule(1);
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
        return new PacketTile(this, 0, tag);
    }

    public void updateClient()
    {
        getDescPacket().send();
    }

    @Override
    public boolean read(EntityPlayer player, AbstractPacket packet)
    {
        readFromNBT(ByteBufUtils.readTag(packet.data));
        return true;
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
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

    public void setMissile(Missile missile)
    {
        this.missile = missile;
    }

    public Missile getMissile()
    {
        return missile;
    }
}
