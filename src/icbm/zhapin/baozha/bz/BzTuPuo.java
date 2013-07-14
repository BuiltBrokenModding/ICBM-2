package icbm.zhapin.baozha.bz;

import mffs.api.IForceFieldBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.IRotatable;

public class BzTuPuo extends BzYaSuo {
	private int depth;

	public BzTuPuo(World world, Entity entity, double x, double y, double z,
			float size, int depth) {
		this(world, entity, x, y, z, size);
		this.depth = depth;
	}

	public BzTuPuo(World world, Entity entity, double x, double y, double z,
			float size) {
		super(world, entity, x, y, z, size);
		this.nengLiang = 13;
	}

	@Override
	public void doExplode() {
		if (!this.worldObj.isRemote) {
			final Vector3 difference = new Vector3();

			if (this.exploder instanceof IRotatable) {
				difference.modifyPositionFromSide(((IRotatable) this.exploder)
						.getDirection());
			} else {
				difference.modifyPositionFromSide(ForgeDirection.DOWN);
			}

			this.worldObj.playSoundEffect(position.x, position.y, position.z,
					"random.explode", 5.0F,
					(1.0F + (worldObj.rand.nextFloat() - worldObj.rand
							.nextFloat()) * 0.2F) * 0.7F);

			for (int i = 0; i < this.depth; i++) {
				if (Block.blocksList[position.getBlockID(worldObj)] != null) {
					if (Block.blocksList[position.getBlockID(worldObj)]
							.getExplosionResistance(this.exploder, worldObj,
									position.intX(), position.intY(),
									position.intZ(), position.x, position.y,
									position.z) > Block.obsidian
							.getExplosionResistance(this.exploder)
							|| Block.blocksList[position.getBlockID(worldObj)] instanceof IForceFieldBlock) {
						break;
					}
				}

				super.doExplode();
				this.position.add(difference);
			}
		}
	}

	@Override
	public float getEnergy() {
		return (super.getEnergy() * this.depth) / 2;
	}
}
