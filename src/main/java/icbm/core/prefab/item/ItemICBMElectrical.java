package icbm.core.prefab.item;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.ICBMConfiguration;
import universalelectricity.api.UniversalClass;
import universalelectricity.api.item.IEnergyItem;
import universalelectricity.api.item.IVoltageItem;
import universalelectricity.api.item.ItemElectric;

@UniversalClass
public abstract class ItemICBMElectrical extends ItemElectric implements IEnergyItem, IVoltageItem
{
	public ItemICBMElectrical(int id, String name)
	{
		super(ICBMConfiguration.CONFIGURATION.getItem("name", id).getInt(id));
		this.setUnlocalizedName(Reference.PREFIX + name);
		this.setCreativeTab(CreativeTabICBM.INSTANCE);
		this.setTextureName(Reference.PREFIX + name);
	}

}
