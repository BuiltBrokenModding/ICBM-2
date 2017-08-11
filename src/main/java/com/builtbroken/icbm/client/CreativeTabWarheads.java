package com.builtbroken.icbm.client;

import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.mc.framework.mod.ModCreativeTab;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/29/2016.
 */
public class CreativeTabWarheads extends ModCreativeTab
{
    public CreativeTabWarheads()
    {
        super("icbm.warheads");
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        add(list, ICBM_API.blockWarhead);
    }
}
