package icbm.explosion;

import icbm.explosion.potion.PChuanRanDu;
import icbm.explosion.potion.PDaDu;

import java.util.EnumSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import universalelectricity.api.vector.Vector3;
import atomicscience.api.poison.Poison;
import calclavia.lib.prefab.potion.CustomPotionEffect;

public class ContagiousPoison extends Poison
{
    private boolean isContagious;

    public ContagiousPoison(String name, int id, boolean isContagious)
    {
        super(name, id);
        this.isContagious = isContagious;
    }

    @Override
    protected void doPoisonEntity(Vector3 emitPosition, EntityLivingBase entity, EnumSet<ArmorType> armorWorn, int amplifier)
    {
        if (this.isContagious)
        {
            entity.addPotionEffect(new CustomPotionEffect(PChuanRanDu.INSTANCE.getId(), 45 * 20, amplifier, null));
            entity.addPotionEffect(new CustomPotionEffect(Potion.blindness.id, 15 * 20, amplifier));
        }
        else
        {
            entity.addPotionEffect(new CustomPotionEffect(PDaDu.INSTANCE.getId(), 30 * 20, amplifier, null));
            entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 30 * 20, amplifier));
        }

        entity.addPotionEffect(new CustomPotionEffect(Potion.hunger.id, 30 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(Potion.weakness.id, 35 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(Potion.digSlowdown.id, 60 * 20, amplifier));
    }

}
