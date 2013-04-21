package icbm.zhapin.zhapin.ex;

import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ThrSheXian extends Thread
{
	public interface IThreadCallBack
	{
		/**
		 * Called when the thread finishes the calculation.
		 */
		public void onThreadComplete(ThrSheXian thread);
	}

	private IThreadCallBack callBack;
	public World world;
	public Vector3 position;
	public int banJing;
	public int nengLiang;
	public Entity source;

	public final HashSet<Vector3> deltaSet = new HashSet<Vector3>();
	public final HashSet<Vector3> destroyed = new HashSet<Vector3>();

	public ThrSheXian(World world, Vector3 position, int banJing, int nengLiang, IThreadCallBack callBack, Entity source)
	{
		this.world = world;
		this.position = position;
		this.banJing = banJing;
		this.nengLiang = nengLiang;
		this.callBack = callBack;
		this.source = source;

		this.setPriority(Thread.NORM_PRIORITY - 1);
	}

	@Override
	public void run()
	{
		int steps = (int) Math.ceil(Math.PI / Math.atan(1.0D / this.banJing));

		for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
		{
			for (int theta_n = 0; theta_n < steps; theta_n++)
			{
				double phi = Math.PI * 2 / steps * phi_n;
				double theta = Math.PI / steps * theta_n;

				Vector3 delta = new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi));
				float power = this.nengLiang - (this.nengLiang * this.world.rand.nextFloat() / 2);

				Vector3 targetPosition = position.clone();

				for (float var21 = 0.3F; power > 0f; power -= var21 * 0.75F * 10)
				{
					if (targetPosition.distanceTo(position) > this.banJing)
						break;

					int blockID = this.world.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());

					if (blockID > 0)
					{
						float resistance = 0;

						if (blockID == Block.bedrock.blockID)
						{
							break;
						}
						else if (Block.blocksList[blockID] instanceof BlockFluid)
						{
							resistance = 1f;
						}
						else
						{
							resistance = Block.blocksList[blockID].getExplosionResistance(this.source, this.world, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ()) * 4;
						}

						power -= resistance;

						if (power > 0f)
						{
							if (!this.destroyed.contains(targetPosition))
							{
								this.destroyed.add(targetPosition.clone());
							}
						}
					}

					targetPosition.x += delta.x;
					targetPosition.y += delta.y;
					targetPosition.z += delta.z;
				}
			}
		}

		this.callBack.onThreadComplete(this);
	}
}
