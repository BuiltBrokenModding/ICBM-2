package icbm.core.prefab.item;

import icbm.Reference;
import icbm.Settings;
import icbm.TabICBM;
import universalelectricity.api.UniversalClass;
import universalelectricity.api.item.IEnergyItem;
import universalelectricity.api.item.IVoltageItem;
import universalelectricity.api.item.ItemElectric;

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

}
