package com.builtbroken.icbm.content.launcher.launcher.standard;

import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.launcher.launcher.TileAbstractLauncherPad;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.Tile;
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
                    //TODO form rocket
                    isCrafting = false;
                }
                else
                {
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
                            //TODO send client packet to update renderer
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
