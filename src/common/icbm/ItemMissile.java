package icbm;

import icbm.explosives.Explosive;

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
        return Explosive.list[itemstack.getItemDamage()].getMissileName();
    }

    @Override
	public int getIconFromDamage(int i)
    {
        return this.iconIndex;
    }

    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < Explosive.MAX_EXPLOSIVE_ID; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
}
