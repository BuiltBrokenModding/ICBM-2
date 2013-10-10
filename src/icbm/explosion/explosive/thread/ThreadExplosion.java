package icbm.explosion.explosive.thread;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public abstract class ThreadExplosion extends Thread
{
	public World world;
	public final Vector3 position;
	public int banJing;
	public float nengLiang;
	public Entity source;

	public boolean isComplete = false;

	public final HashSet<Vector3> deltaSet = new HashSet<Vector3>();
	public final HashSet<Vector3> results = new HashSet<Vector3>();

	public ThreadExplosion(World world, Vector3 position, int banJing, float nengLiang, Entity source)
	{
		this.world = world;
		this.position = position;
		this.banJing = banJing;
		this.nengLiang = nengLiang;
		this.source = source;
		this.setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void run()
	{
		this.isComplete = true;
	}
}
