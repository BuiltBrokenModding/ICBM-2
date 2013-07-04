package icbm.gangshao.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.prefab.CustomDamageSource;

public class GangShaoDamageSource extends CustomDamageSource
{
	protected Object damageSource;

	public GangShaoDamageSource(String damageName, Object attacker)
	{
		super(damageName);
		this.damageSource = attacker;
	}

	@Override
	public Entity getEntity()
	{
		return damageSource instanceof Entity ? ((Entity) damageSource) : null;
	}

	@Override
	public boolean isDifficultyScaled()
	{
		return this.damageSource != null && this.damageSource instanceof EntityLiving && !(this.damageSource instanceof EntityPlayer);
	}

	public static GangShaoDamageSource doBulletDamage(Object object)
	{
		return (GangShaoDamageSource) ((CustomDamageSource) new GangShaoDamageSource("Bullets", object).setProjectile()).setDeathMessage("%1$s was filled with holes!");
	}

	public static GangShaoDamageSource doLaserDamage(Object object)
	{
		return (GangShaoDamageSource) ((CustomDamageSource) new GangShaoDamageSource("Laser", object).setProjectile()).setDeathMessage("%1$s was vaporized!");
	}
}
