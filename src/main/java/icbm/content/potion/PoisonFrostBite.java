package icbm.content.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import resonant.lib.prefab.potion.CustomPotion;

public class PoisonFrostBite extends CustomPotion
{
    public static PoisonFrostBite INSTANCE;

    public PoisonFrostBite(int id, boolean isBadEffect, int color, String name)
    {
        super(id, isBadEffect, color, name);
        this.setIconIndex(6, 0);
    }

    @Override
    public void performEffect(EntityLivingBase par1EntityLiving, int amplifier)
    {
        if (par1EntityLiving instanceof EntityPlayer)
        {
            ((EntityPlayer) par1EntityLiving).addExhaustion(3F * (amplifier + 1));
        }

        if (par1EntityLiving.isBurning())
        {
            par1EntityLiving.extinguish();
            par1EntityLiving.removePotionEffect(this.id);
        }

        // Check to see if it's on ice
        if (par1EntityLiving.worldObj.getBlock(MathHelper.floor_double(par1EntityLiving.posX), MathHelper.floor_double(par1EntityLiving.posY) - 1, MathHelper.floor_double(par1EntityLiving.posZ)) == Blocks.ice)
        {
            par1EntityLiving.attackEntityFrom(DamageSource.magic, 2);
        }

        // Shatter enemy if health is too low
        if (par1EntityLiving.getHealth() < 6)
        {
            par1EntityLiving.attackEntityFrom(DamageSource.magic, 999999999);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        if (duration % 20 == 0)
        {
            return true;
        }

        return false;
    }
}
