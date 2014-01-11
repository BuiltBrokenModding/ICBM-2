package icbm.explosion.missile.modular;

import icbm.api.explosion.IMissileModule.IMissileBody;
import net.minecraft.nbt.NBTTagCompound;

/** @author DarkGuardsman */
public class ModuleMissileCasing extends ModuleMissileBase implements IMissileBody
{

	/** Starting HP of the casing */
	protected float health;
	/** Resistance to damage */
	protected float armor;
	/** Chance for the missile to be damaged when attacked */
	protected float damageChance;
	/** Chance for the missile to catch fire when damaged */
	protected float fireChance;

	public ModuleMissileCasing(String name, int tier)
	{
		super(name, tier);
	}

	@Override
	public float getHealthBonus()
	{
		return 0;
	}

	@Override
	public float getArmorBonus()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDamageChance()
	{
		return this.damageChance;
	}

	@Override
	public float getFireChance()
	{
		return this.fireChance;
	}

	public ModuleMissileCasing setDamageChance(float chance)
	{
		this.damageChance = chance;
		return this;
	}

	public ModuleMissileCasing setFireChance(float chance)
	{
		this.fireChance = chance;
		return this;
	}

	public ModuleMissileCasing setHealth(float health)
	{
		this.health = health;
		return this;
	}

	public ModuleMissileCasing setArmor(float armor)
	{
		this.armor = armor;
		return this;
	}

	@Override
	public NBTTagCompound save(NBTTagCompound nbt)
	{
		super.save(nbt);
		nbt.setFloat("health", this.health);
		nbt.setFloat("armor", this.armor);
		nbt.setFloat("damageChance", this.damageChance);
		nbt.setFloat("fireChance", this.fireChance);
		return nbt;
	}

	@Override
	public void load(NBTTagCompound nbt)
	{
		super.load(nbt);
		this.health = nbt.getFloat("health");
		this.armor = nbt.getFloat("armor");
		this.damageChance = nbt.getFloat("damageChance");
		this.fireChance = nbt.getFloat("fireChance");
	}

	@Override
	public String getOreName()
	{
		return "MissileCasing";
	}

}
