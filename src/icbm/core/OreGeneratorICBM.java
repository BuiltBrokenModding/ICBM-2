package icbm.core;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.ore.OreGenReplace;

/**
 * @author CovertJaguar, Modified by Calclavia
 */

public class OreGeneratorICBM extends OreGenReplace
{
	public OreGeneratorICBM(String name, String oreDiectionaryName, ItemStack stack, int replaceID, int minGenerateLevel, int maxGenerateLevel, int amountPerChunk, int amountPerBranch, String harvestTool, int harvestLevel)
	{
		super(name, oreDiectionaryName, stack, 0, replaceID, maxGenerateLevel, amountPerChunk, amountPerBranch, "pickaxe", 1);
	}

	public OreGeneratorICBM(String name, String oreDiectionaryName, ItemStack stack, int replaceID, int maxGenerateLevel, int amountPerChunk, int amountPerBranch)
	{
		this(name, oreDiectionaryName, stack, 0, replaceID, maxGenerateLevel, amountPerChunk, amountPerBranch, "pickaxe", 1);
	}

	@Override
	public void generate(World world, Random random, int varX, int varZ)
	{
		for (int y = this.minGenerateLevel; y < this.maxGenerateLevel; y++)
		{
			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					this.generateReplace(world, random, varX + x, y, varZ + z);
				}
			}
		}
	}

	@Override
	public boolean generateReplace(World world, Random rand, int x, int y, int z)
	{
		if (nearLava(world, x, y, z))
		{
			placeOre(world, rand, x, y, z);
			return true;
		}

		return false;
	}

	private void placeOre(World world, Random rand, int x, int y, int z)
	{
		Vector3 position = new Vector3(x, y, z);

		for (int amount = 0; amount < this.amountPerBranch; amount++)
		{
			Block block = Block.blocksList[world.getBlockId(x, y, z)];

			if (block != null && block.isGenMineableReplaceable(world, x, y, z, Block.stone.blockID))
			{
				world.setBlock(x, y, z, this.oreID, this.oreMeta, 2);
			}

			ForgeDirection dir = ForgeDirection.values()[rand.nextInt(6)];

			position.modifyPositionFromSide(dir);
		}
	}

	private boolean nearLava(World world, int x, int y, int z)
	{
		for (int side = 2; side < 6; side++)
		{
			Vector3 position = new Vector3(x, y, z);

			ForgeDirection s = ForgeDirection.values()[side];

			position.modifyPositionFromSide(s);

			if (world.blockExists(position.intX(), position.intY(), position.intZ()))
			{
				int id = world.getBlockId(position.intX(), position.intY(), position.intZ());

				if (id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
				{
					return true;
				}
			}
		}

		for (int j = 0; j < 4; j++)
		{
			int id = world.getBlockId(x, y - j, z);

			if (id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID)
			{
				return true;
			}
			else if (id != 0)
			{
				return false;
			}
		}

		return false;
	}
}