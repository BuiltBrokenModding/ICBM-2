package icbm.api.explosion;

import net.minecraft.world.World;
import net.minecraftforge.event.Event;

/**
 * Use ForgeSubscribe to subscribe to this event. This event is called every single time when an
 * ICBM explosion happens.
 * 
 * @author Calclavia
 * 
 */
public class ExplosionEvent extends Event
{
	/**
	 * The world in which the explosion happened.
	 */
	public final World world;

	/**
	 * The position of the explosion.
	 */
	public final double x, y, z;

	/**
	 * The explosive type of the explosion.
	 */
	public IExplosive explosive;

	public ExplosionEvent(World world, double x, double y, double z, IExplosive explosive)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.explosive = explosive;
	}

	/**
	 * Called before an explosion happens.
	 * 
	 * @author Calclavia
	 * 
	 */
	public static class PreExplosionEvent extends ExplosionEvent
	{
		public PreExplosionEvent(World world, double x, double y, double z, IExplosive explosive)
		{
			super(world, x, y, z, explosive);
		}
	}

	/**
	 * Called after an explosion happens.
	 * 
	 * @author Calclavia
	 * 
	 */
	public static class PostExplosionEvent extends ExplosionEvent
	{
		public PostExplosionEvent(World world, double x, double y, double z, IExplosive explosive)
		{
			super(world, x, y, z, explosive);
		}
	}
}
