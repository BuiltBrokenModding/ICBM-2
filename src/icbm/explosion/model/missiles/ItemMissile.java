package icbm.explosion.model.missiles;

import icbm.core.base.ItemICBMBase;
import icbm.explosion.missile.Explosive;
import icbm.explosion.missile.ExplosiveRegistry;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemMissile extends ItemICBMBase
{
    public ItemMissile(int id, String name)
    {
        super(id, name);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return this.getUnlocalizedName() + "." + ExplosiveRegistry.get(itemStack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.missile";
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (Explosive zhaPin : ExplosiveRegistry.getAllDaoDan())
        {
            if (zhaPin.hasMissileForm())
            {
                par3List.add(new ItemStack(par1, 1, zhaPin.getID()));
            }
        }
    }
}
