package icbm;

import icbm.explosions.Explosive;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemMissile extends ICBMItem
{
    public ItemMissile(String name, int id, int texture)
    {
        super(name, id, texture, CreativeTabs.tabCombat);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.maxStackSize = 1;
    }

    @Override
	public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
	public String getItemNameIS(ItemStack itemstack)
    {
    	if(itemstack.getItemDamage() == 0)
    	{	
    		return "Conventional Missile";
    	}
    	
        return Explosive.list[itemstack.getItemDamage()].getName() + " Missile";
    }

    @Override
	public int getIconFromDamage(int i)
    {
        return this.iconIndex;
    }

    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < Explosive.maxExplosives; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
}
