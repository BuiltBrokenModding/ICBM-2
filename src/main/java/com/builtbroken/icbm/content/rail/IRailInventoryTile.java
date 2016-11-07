package com.builtbroken.icbm.content.rail;

import com.builtbroken.mc.api.tile.IInventoryProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Applied to tiles that can be accessed by inventory managers on the rail system.
 * <p>
 * An inventory manager is normally a loader/unloader tile for carts. However, the cart
 * itself could be the manager.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/4/2016.
 */
public interface IRailInventoryTile extends IInventoryProvider
{
    /**
     * Slots that will be accessed to load items into the cart.
     *
     * @param side - side of the block pointing away from the tile
     * @return array of slots, never null
     */
    int[] getSlotsToLoad(ForgeDirection side);

    /**
     * Slots that will be accessed to load items into the cart.
     *
     * @param side - side of the block pointing away from the tile
     * @return array of slots, never null
     */
    default int[] getSlotsToLoad(ItemStack stack, ForgeDirection side)
    {
        return getSlotsToLoad(side);
    }

    /**
     * Slots that will be accessed to unload items from the cart.
     *
     * @param side - side of the block pointing away from the tile
     * @return array of slots, never null
     */
    int[] getSlotsToUnload(ForgeDirection side);

    /**
     * Slots that will be accessed to unload items from the cart.
     *
     * @param side - side of the block pointing away from the tile
     * @return array of slots, never null
     */
    default int[] getSlotsToUnload(ItemStack stack, ForgeDirection side)
    {
        return getSlotsToUnload(side);
    }
}
