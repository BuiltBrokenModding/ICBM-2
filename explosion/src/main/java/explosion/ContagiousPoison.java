package icbm.explosion;

import icbm.explosion.potion.PoisonContagion;
import icbm.explosion.potion.PoisonToxin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.prefab.poison.Poison;
import calclavia.lib.prefab.potion.CustomPotionEffect;

public class ContagiousPoison extends Poison
{
    private boolean isContagious;

    public ContagiousPoison(String name, int id, boolean isContagious)
    {
        super(name);
        this.isContagious = isContagious;
    }

    @Override
    protected void doPoisonEntity(Vector3 emitPosition, EntityLivingBase entity, int amplifier)
    {
        if (this.isContagious)
        {
            entity.addPotionEffect(new CustomPotionEffect(PoisonContagion.INSTANCE.getId(), 45 * 20, amplifier, null));
            entity.addPotionEffect(new CustomPotionEffect(Potion.blindness.id, 15 * 20, amplifier));
        }
        else
        {
            entity.addPotionEffect(new CustomPotionEffect(PoisonToxin.INSTANCE.getId(), 30 * 20, amplifier, null));
            entity.addPotionEffect(new CustomPotionEffect(Potion.confusion.id, 30 * 20, amplifier));
        }

        entity.addPotionEffect(new CustomPotionEffect(Potion.hunger.id, 30 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(Potion.weakness.id, 35 * 20, amplifier));
        entity.addPotionEffect(new CustomPotionEffect(Potion.digSlowdown.id, 60 * 20, amplifier));
    }

}
