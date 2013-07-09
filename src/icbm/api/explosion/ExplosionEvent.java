package icbm.api.explosion;

import net.minecraft.world.Explosion;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Use ForgeSubscribe to subscribe to this event. This event is called every single time when an
 * ICBM explosion happens.
 * 
 * @author Calclavia
 * 
 */
@Cancelable
public class ExplosionEvent extends Event
{
	/**
	 * The explosion object. Can be cast into {@link Explosion}. This event can be canceled to
	 * prevent a specific part of an explosion from being executed.
	 */
	public IExplosion explosion;

	public ExplosionEvent(IExplosion explosion)
	{
		this.explosion = explosion;
	}

	/**
	 * Called when an explosion is constructed. You may cancel and explosion here if needed. After
	 * this it will be a bit too late to prevent destruction.
	 * 
	 * @author Calclavia
	 * 
	 */
	public static class ExplosionConstructionEvent extends ExplosionEvent
	{
		public ExplosionConstructionEvent(IExplosion explosion)
		{
			super(explosion);
		}
	}

	/**
	 * Called before an explosion happens.
	 * 
	 * @author Calclavia
	 * 
	 */
	public static class PreExplosionEvent extends ExplosionEvent
	{
		public PreExplosionEvent(IExplosion explosion)
		{
			super(explosion);
		}
	}

	/**
	 * Called while an explosion happens. May be called every single tick if explosion is
	 * procedural. (E.g: Red matter explosive)
	 * 
	 * @author Calclavia
	 * 
	 */
	public static class DoExplosionEvent extends ExplosionEvent
	{
		public DoExplosionEvent(IExplosion explosion)
		{
			super(explosion);
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
		public PostExplosionEvent(IExplosion explosion)
		{
			super(explosion);
		}
	}
}
