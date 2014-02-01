package icbm.explosion.potion;

import calclavia.lib.prefab.potion.CustomPotion;
import icbm.core.Settings;

public abstract class ICBMPotion extends CustomPotion
{
    public ICBMPotion(int id, boolean isBadEffect, int color, String name)
    {
        super(Settings.CONFIGURATION.get("Potion", name, id).getInt(id), isBadEffect, color, name);
    }
}
