package icbm;

import icbm.zhapin.EShouLiuDan;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItShouLiuDan extends ICBMItem
{
	public ItShouLiuDan(String name, int par1, int par2) 
	{
		super(name, par1, par2, CreativeTabs.tabCombat);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (!par3EntityPlayer.capabilities.isCreativeMode)
        {
            --par1ItemStack.stackSize;
        }

        par2World.playSoundAtEntity(par3EntityPlayer, "random.fuse", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!par2World.isRemote)
        {
            par2World.spawnEntityInWorld(new EShouLiuDan(par2World, par3EntityPlayer, ZhaPin.list[par1ItemStack.getItemDamage()].getID()));
        }

        return par1ItemStack;
    }
    
    @Override
	public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
	public String getItemNameIS(ItemStack itemstack)
    {
        return ZhaPin.list[itemstack.getItemDamage()].getGrenadeName();
    }

    @Override
	public int getIconFromDamage(int i)
    {
        return this.iconIndex+i;
    }
    
    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < 4; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
}
