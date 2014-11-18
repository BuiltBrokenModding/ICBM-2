package icbm.content.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.DamageSource;
import resonant.lib.prefab.potion.CustomPotion;

public class PoisonToxin extends CustomPotion
{
    public static PoisonToxin INSTANCE;

    public PoisonToxin(int id, boolean isBadEffect, int color, String name)
    {
        super(id, isBadEffect, color, name);
        this.setIconIndex(6, 0);
    }

    @Override
    public void performEffect(EntityLivingBase par1EntityLiving, int amplifier)
    {
        if (!(par1EntityLiving instanceof EntityZombie) && !(par1EntityLiving instanceof EntityPigZombie))
        {
            par1EntityLiving.attackEntityFrom(DamageSource.magic, 1);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        if (duration % (20 * 2) == 0)
        {
            return true;
        }

        return false;
    }
}
