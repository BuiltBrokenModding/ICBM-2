package icbm.core.items;

import icbm.Reference;
import icbm.core.prefab.item.ItemICBMBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAntidote extends ItemICBMBase
{
    public ItemAntidote()
    {
        super("antidote");
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par1ItemStack.stackSize--;

        if (!par2World.isRemote)
        {
            par3EntityPlayer.clearActivePotions();
        }

        return par1ItemStack;
    }

    /** How long it takes to use or consume an item */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }

    /** returns the action that specifies what animation to play when the items is being used */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.eat;
    }
    
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        
        // Icon for base item.
        this.itemIcon = iconRegister.registerIcon(Reference.PREFIX + "antidote");
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }
}
