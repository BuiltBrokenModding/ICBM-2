package com.builtbroken.icbm.client;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.lib.mod.ModCreativeTab;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/31/2016.
 */
public class CreativeTabExplosives extends ModCreativeTab
{
    public CreativeTabExplosives()
    {
        super("icbm.explosives");
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        add(list, ICBM_API.itemExplosivePart);
        add(list, ICBM_API.itemExplosive);
    }
}
