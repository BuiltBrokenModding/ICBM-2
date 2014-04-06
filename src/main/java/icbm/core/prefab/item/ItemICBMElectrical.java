package icbm.core.prefab.item;

import calclavia.lib.render.EnumColor;
import calclavia.lib.utility.LanguageUtility;
import calclavia.lib.utility.TooltipUtility;
import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import universalelectricity.api.UniversalClass;
import universalelectricity.api.item.IEnergyItem;
import universalelectricity.api.item.IVoltageItem;
import universalelectricity.api.item.ItemElectric;

import java.util.List;

@UniversalClass
public abstract class ItemICBMElectrical extends ItemElectric implements IEnergyItem, IVoltageItem
{
    public ItemICBMElectrical(int id, String name)
    {
        super(Settings.CONFIGURATION.getItem(name, id).getInt(id));
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
