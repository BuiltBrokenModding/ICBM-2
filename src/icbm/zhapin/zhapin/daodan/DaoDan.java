package icbm.zhapin.zhapin.daodan;

import icbm.core.ZhuYaoICBM;
import icbm.core.di.MICBM;
import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class DaoDan extends ZhaPin
{
	@SideOnly(Side.CLIENT)
	private ResourceLocation resourceLocation;

	public DaoDan(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	/**
	 * Called when launched.
	 */
	public void launch(EDaoDan missileObj)
	{
	}

	/**
	 * Called every tick while flying.
	 */
	public void update(EDaoDan missileObj)
	{
	}

	public boolean onInteract(EDaoDan missileObj, EntityPlayer entityPlayer)
	{
		return false;
	}

	@Override
	public ItemStack getItemStack()
	{
		return new ItemStack(ZhuYaoZhaPin.itDaoDan, 1, this.getID());
	}

	/**
	 * Is this missile compatible with the cruise launcher?
	 * 
	 * @return
	 */
	public boolean isCruise()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public abstract MICBM getMuoXing();

	@SideOnly(Side.CLIENT)
	public ResourceLocation getMissileResource()
	{
		if (this.resourceLocation == null)
		{
			this.resourceLocation = new ResourceLocation(ZhuYaoICBM.MODEL_PATH + "missile_" + this.getUnlocalizedName() + ".png");
		}

		return this.resourceLocation;
	}
}
