package icbm.api.sentry;

/**
 * Used by sentries to tell what type of projectile its using
 * 
 * @author DarkGuardsman
 * 
 */
public enum ProjectileTypes
{
	NEUTRIAL(), /* NOT A PROJECTILE */
	CONVENTIONAL(), /* Classic bullets that do impact damage */
	CRYSTAL(), /* Used by laser based weapons to focus the beam */
	RAILGUN(), /* Ammo that can only be used by railguns */
	MISSILE(), /* Ammo used by SAM sites or missile based sentries */
	EXPLOSIVE()/* Ammo that a mortar or grenade launcher uses */;
	private ProjectileTypes()
	{

	}
}
