package icbm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.PotionEffect;

public class ICBMPotionEffect extends PotionEffect
{
    private List<ItemStack> ICBMCurativeItems = new ArrayList<ItemStack>();

	public ICBMPotionEffect(int par1, int par2, int par3)
	{
		super(par1, par2, par3);
		this.ICBMCurativeItems.add(new ItemStack(ICBM.itemYao));
		this.setCurativeItems(ICBMCurativeItems);
	}

}
