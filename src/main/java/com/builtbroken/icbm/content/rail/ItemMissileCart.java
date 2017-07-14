package com.builtbroken.icbm.content.rail;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.api.rails.ITransportRail;
import com.builtbroken.mc.api.rails.ITransportRailBlock;
import com.builtbroken.mc.core.registry.implement.IRecipeContainer;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Handles placement and inventory movement of the missile rail cart
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/29/2016.
 */
public class ItemMissileCart extends Item implements IRecipeContainer
{
    public ItemMissileCart()
    {
        this.setMaxStackSize(5);
        this.setUnlocalizedName(ICBM.PREFIX + "missileRailCart");
        this.setTextureName(ICBM.PREFIX + "missileCartItem");
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        list.add("Size: " + MissileCartTypes.values()[stack.getItemDamage()]);
        list.add("Works with AssemblyLine mod rails only");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (placeCart(world, x, y, z, stack.getItemDamage()) != null)
            {
                if (!player.capabilities.isCreativeMode)
                {
                    stack.stackSize--;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xf, float yf, float zf)
    {
        return world.getBlock(x, y, z) instanceof ITransportRailBlock || world.getTileEntity(x, y, z) instanceof ITransportRail;
    }

    /**
     * Places the cart on top of the rail
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param type  - type of the cart @see {@link MissileCartTypes}
     * @return true if the entity was placed into the world
     */
    public static EntityMissileCart placeCart(World world, int x, int y, int z, int type)
    {
        final Block block = world.getBlock(x, y, z);
        final TileEntity tile = world.getTileEntity(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);

        if (block instanceof ITransportRailBlock)
        {
            EntityMissileCart cart = getCart(world, type);
            cart.setPosition(x, y, z);
            mountEntity(cart, ((ITransportRailBlock) block).getAttachedDirection(world, x, y, z, meta), ((ITransportRailBlock) block).getFacingDirection(world, x, y, z, meta), ((ITransportRailBlock) block).getRailHeight(world, x, y, z, meta));
            return cart;
        }
        else if (tile instanceof ITransportRail)
        {
            EntityMissileCart cart = getCart(world, type);
            cart.setPosition(x, y, z);
            mountEntity(cart, ((ITransportRail) tile).getAttachedDirection(), ((ITransportRail) tile).getFacingDirection(), ((ITransportRail) tile).getRailHeight());
            return cart;
        }
        return null;
    }


    /**
     * Sets the cart onto the rail and spawns it into the world
     *
     * @param cart
     * @param side
     * @param facing
     * @param railHeight
     */
    public static void mountEntity(final EntityMissileCart cart, final ForgeDirection side, final ForgeDirection facing, double railHeight)
    {
        cart.railSide = side;
        cart.recenterCartOnRail(side, facing, railHeight, true);
        cart.worldObj.spawnEntityInWorld(cart);
    }

    /**
     * Gets the cart for the damage of the item
     *
     * @param world
     * @param meta
     * @return
     */
    public static EntityMissileCart getCart(final World world, int meta)
    {
        //More types will be added later
        final EntityMissileCart cart = new EntityMissileCart(world);
        cart.setType(MissileCartTypes.values()[meta]);
        return cart;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_)
    {
        for (int i = 0; i < MissileCartTypes.values().length; i++)
        {
            p_150895_3_.add(new ItemStack(p_150895_1_, 1, i));
        }
    }

    @Override
    public void genRecipes(List<IRecipe> recipes)
    {
        if(OreDictionary.doesOreNameExist("alTransportCart"))
        {
            recipes.add(newShapedRecipe(new ItemStack(this, 1, 0), "SCS", "RTR", 'S', OreNames.ROD_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'R', OreNames.REDSTONE, 'T', "alTransportCart"));
        }
        else
        {
            recipes.add(newShapedRecipe(new ItemStack(this, 1, 0), "SCS", "RTR", 'S', OreNames.ROD_IRON, 'C', UniversalRecipe.CIRCUIT_T1.get(), 'R', OreNames.REDSTONE, 'T', Items.minecart));
        }
        recipes.add(newShapedRecipe(new ItemStack(this, 1, 1), "RTR", 'R', OreNames.PLATE_IRON, 'T', new ItemStack(this, 1, 0)));
        recipes.add(newShapedRecipe(new ItemStack(this, 1, 2), "RRR", "RTR", "RRR", 'R', OreNames.PLATE_IRON, 'T', new ItemStack(this, 1, 0)));
    }
}
