package icbm.content.prefab.item;

import icbm.Reference;
import icbm.TabICBM;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import resonant.api.items.IEnergyItem;
import resonant.lib.prefab.item.ItemElectric;
import resonant.lib.render.EnumColor;
import resonant.lib.utility.LanguageUtility;
import resonant.lib.utility.TooltipUtility;

public abstract class ItemICBMElectrical extends ItemElectric implements IEnergyItem
{
    public ItemICBMElectrical(String name)
    {
        this.setUnlocalizedName(Reference.PREFIX + name);
        this.setCreativeTab(TabICBM.INSTANCE);
        this.setTextureName(Reference.PREFIX + name);
    }

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
	{
		super.addInformation(itemStack, par2EntityPlayer, list, par4);
		String tooltip = LanguageUtility.getLocal(getUnlocalizedName(itemStack) + ".tooltip");

		if (tooltip != null && tooltip.length() > 0)
		{
			if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				list.add(LanguageUtility.getLocal("tooltip.noShift").replace("%0", EnumColor.AQUA.toString()).replace("%1", EnumColor.GREY.toString()));
			}
			else
			{
				list.addAll(LanguageUtility.splitStringPerWord(tooltip, 5));
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_J))
		{
			TooltipUtility.addTooltip(itemStack, list);
		}
		else
		{
			list.add(LanguageUtility.getLocal("info.recipes.tooltip").replace("%0", EnumColor.AQUA.toString()).replace("%1", EnumColor.GREY.toString()));
		}
	}

}
