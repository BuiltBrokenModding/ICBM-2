package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.launcher.TileAbstractLauncherPad;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/2/2015.
 */
public class TileStandardLauncher extends TileAbstractLauncherPad
{
    /** Is the silo in crafting mode. */
    protected boolean isCrafting = false;
    /** Current recipe and progress. */
    protected StandardMissileCrafting recipe;

    public TileStandardLauncher()
    {
        super("standardlauncher");
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isCrafting)
        {
            if (recipe != null && isServer())
            {
                if (recipe.isFinished())
                {
                    ItemStack stack = recipe.getMissileAsItem();
                    if (stack != null)
                    {
                        //Make sure to disable crafting before setting slot
                        isCrafting = false;
                        setInventorySlotContents(0, stack);
                        recipe = null;
                        sendDescPacket();
                    }
                    else
                    {
                        //TODO add more detailed error report and eject invalid parts
                        player.addChatComponentMessage(new ChatComponentText("Error missile stack is null"));
                    }
                }
                else
                {
                    //Output missing recipe items
                    player.addChatComponentMessage(recipe.getCurrentRecipeChat());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (getMissile() == null)
        {
            //TODO implement recipe insertion or selection system
            this.isCrafting = true;
            if (recipe == null)
            {
                this.recipe = new StandardMissileCrafting();
            }

            //Insert recipe items
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null)
            {
                heldItem = heldItem.copy();
                if (recipe.canAddItem(heldItem))
                {
                    if (isServer())
                    {
                        //Add item to recipe
                        if (!recipe.addItem(heldItem))
                        {
                            //TODO add translation key
                            if (!recipe.isFinished())
                            {
                                player.addChatComponentMessage(new ChatComponentText("Odd that should fit..."));
                            }
                            else
                            {
                                //TODO add random finish messages
                                player.addChatComponentMessage(new ChatComponentText("Recipe finished click with wrench, or screwdriver."));
                            }
                        }
                        //Check match to prevent ghost updates( updates when nothing happens)
                        else if (!InventoryUtility.stacksMatchExact(player.getHeldItem(), heldItem))
                        {
                            //Null item if stackSize is zero
                            if (heldItem.stackSize <= 0)
                            {
                                heldItem = null;
                            }
                            //Update inventory
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
                            player.inventoryContainer.detectAndSendChanges();
                            sendDescPacket();
                        }
                    }
                }
                else if (isServer())
                {
                    player.addChatComponentMessage(recipe.getCurrentRecipeChat());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        //If something sets the missile while we are crafting, eject all items
        if (slot == 0 && isCrafting)
        {
            //TODO drop all crafting items
        }
        super.setInventorySlotContents(slot, stack);
    }

    @Override
    public void writeDescPacket(ByteBuf buf)
    {
        if (getMissileItem() != null)
        {
            buf.writeByte(0);
            ByteBufUtils.writeItemStack(buf, getMissileItem());
        }
        else if (isCrafting)
        {
            if (recipe != null)
            {
                buf.writeByte(1);
                recipe.writeBytes(buf);
            }
            else
            {
                buf.writeByte(2);
            }
        }
        else
        {
            buf.writeByte(3);
        }
    }

    @Override
    public boolean canFireMissile()
    {
        return !isCrafting;
    }

    @Override
    public Tile newTile()
    {
        return new TileStandardLauncher();
    }

    @Override
    public boolean canAcceptMissile(Missile missile)
    {
        return super.canAcceptMissile(missile) && missile.casing == MissileCasings.STANDARD;
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:standardLauncher.container";
    }
}
