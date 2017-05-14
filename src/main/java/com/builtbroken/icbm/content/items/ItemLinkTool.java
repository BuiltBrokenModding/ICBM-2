package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.jlib.lang.TextColor;
import com.builtbroken.mc.api.items.listeners.IItemActivationListener;
import com.builtbroken.mc.api.items.tools.IPassCodeItem;
import com.builtbroken.mc.api.items.tools.IWorldPosItem;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.codegen.annotations.ItemWrapped;
import com.builtbroken.mc.framework.item.logic.ItemNode;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * Used to link to tiles together
 * Created by robert on 4/15/2015.
 */
@ItemWrapped(className = ".gen.ItemWrapperLinkTool", wrappers = "IWorldPos;EnergyUE")
public class ItemLinkTool extends ItemNode implements IPassCodeItem, IItemActivationListener
{
    public ItemLinkTool()
    {
        super(ICBM.DOMAIN, "siloLinker");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote && player != null && player.isSneaking())
        {
            stack.setTagCompound(null);
            stack.setItemDamage(0);
            LanguageUtility.addChatToPlayer(player, "link.cleared");
            player.inventoryContainer.detectAndSendChanges();
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hit_x, float hit_y, float hit_z)
    {
        if (player != null && stack != null && stack.getItem() instanceof IWorldPosItem)
        {
            if (world.isRemote)
            {
                return true;
            }

            final Location location = new Location(world, x, y, z);
            TileEntity tile = location.getTileEntity();

            if (tile instanceof IMultiTile)
            {
                IMultiTileHost host = ((IMultiTile) tile).getHost();
                if (host instanceof TileEntity)
                {
                    tile = (TileEntity) host;
                }
            }

            if (player.isSneaking())
            {
                Location storedLocation = ((IWorldPosItem) stack.getItem()).getLocation(stack);
                if (storedLocation != null && storedLocation.equals(location))
                {
                    LanguageUtility.addChatToPlayer(player, "link.error.data.stored");
                    return true;
                }
                ((IWorldPosItem) stack.getItem()).setLocation(stack, location);
                LanguageUtility.addChatToPlayer(player, "link.pos.set");
                if (tile instanceof IPassCode)
                {
                    setCode(stack, ((IPassCode) tile).getCode());
                }
                else if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof IPassCode)
                {
                    setCode(stack, ((IPassCode) ((ITileNodeHost) tile).getTileNode()).getCode());
                }
                stack.setItemDamage(1);
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }
            else
            {
                Location storedLocation = ((IWorldPosItem) stack.getItem()).getLocation(stack);
                if (storedLocation != null)
                {
                    if (!storedLocation.equals(location))
                    {
                        if (!storedLocation.isAboveBedrock())
                        {
                            LanguageUtility.addChatToPlayer(player, "link.error.pos.invalid");
                            return true;
                        }
                        else if (tile instanceof ILinkable || tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof ILinkable)
                        {
                            ILinkable linkable = tile instanceof ILinkable ? (ILinkable) tile : (ILinkable) ((ITileNodeHost) tile).getTileNode();
                            String result = linkable.link(storedLocation, getCode(stack));
                            if (result != null && !result.isEmpty())
                            {
                                if (result.contains("error"))
                                {
                                    String translation = LanguageUtility.getLocalName(result);
                                    if (translation == null || translation.isEmpty())
                                    {
                                        translation = "Error";
                                    }
                                    player.addChatComponentMessage(new ChatComponentText(TextColor.RED.getColorString() + translation));
                                }
                                else
                                {
                                    LanguageUtility.addChatToPlayer(player, result);
                                }
                            }
                            else
                            {
                                LanguageUtility.addChatToPlayer(player, "link.completed");
                            }
                            return true;
                        }
                        else
                        {
                            LanguageUtility.addChatToPlayer(player, "link.error.pos.invalid");
                            return true;
                        }
                    }
                    else
                    {
                        LanguageUtility.addChatToPlayer(player, "link.error.pos.loop");
                        return true;
                    }
                }
                else
                {
                    LanguageUtility.addChatToPlayer(player, "link.error.pos.invalid");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public short getCode(ItemStack stack)
    {
        if (stack != null && stack.getItem() instanceof IWorldPosItem && stack.hasTagCompound() && stack.getTagCompound().hasKey("passShort"))
        {
            return stack.getTagCompound().getShort("passShort");
        }
        return 0;
    }

    @Override
    public void setCode(ItemStack stack, short code)
    {
        if (stack != null && stack.getItem() instanceof IWorldPosItem)
        {
            if (stack.getTagCompound() == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setShort("passShort", code);
        }
    }
}
