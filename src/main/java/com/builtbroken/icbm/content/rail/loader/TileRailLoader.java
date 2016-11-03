package com.builtbroken.icbm.content.rail.loader;

import com.builtbroken.icbm.content.rail.entity.EntityCart;
import com.builtbroken.icbm.content.rail.entity.ItemCart;
import com.builtbroken.mc.api.tile.IRotation;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Handles loading and unloading of carts into varies systems
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/2/2016.
 */
public class TileRailLoader extends TileModuleMachine implements IRotation
{
    /** Side the block is attached towards */
    private ForgeDirection attachedSide;
    /** Side the cart will face when deployed */
    private ForgeDirection facingDirection = ForgeDirection.NORTH;

    private boolean deploy = false;

    public TileRailLoader()
    {
        super("railLoader", Material.iron);
        this.addInventoryModule(1);
    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 10 == 0 && deploy)
        {
            deploy = false;
            deployCart();
        }
    }

    /**
     * Pops the cart up onto the tracks
     */
    public void deployCart()
    {
        final ItemStack stack = getStackInSlot(0);
        if (stack != null && stack.getItem() instanceof ItemCart)
        {
            final EntityCart cart = ItemCart.placeCart(world(), xi() + attachedSide.offsetX, yi() + attachedSide.offsetY, zi() + attachedSide.offsetZ, stack.getItemDamage());
            if (cart != null)
            {
                cart.facingDirection = facingDirection;
                stack.stackSize--;
                if (stack.stackSize <= 0)
                {
                    setInventorySlotContents(0, null);
                }
                else
                {
                    setInventorySlotContents(0, stack);
                }
            }
        }
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (attachedSide == null)
        {
            attachedSide = ForgeDirection.getOrientation(getMetadata());
        }
        return attachedSide;
    }
}
