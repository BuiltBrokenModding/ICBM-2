package icbm.explosion.jiqi;

import icbm.api.ITier;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class IBJiQi extends ItemBlock
{
	public IBJiQi(int id)
	{
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return this.getUnlocalizedName() + "." + itemstack.getItemDamage();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.machine";
	}

	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		int jiQiMetadata;

		if (itemStack.getItemDamage() < 3)
		{
			jiQiMetadata = 0;
		}
		else if (itemStack.getItemDamage() < 6)
		{
			jiQiMetadata = 1;
		}
		else if (itemStack.getItemDamage() < 9)
		{
			jiQiMetadata = 2;
		}
		else
		{
			jiQiMetadata = itemStack.getItemDamage() - 6;
		}

		int direction = MathHelper.floor_double((entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (BJiQi.canBePlacedAt(world, x, y, z, jiQiMetadata, direction))
		{
			Block var9 = Block.blocksList[this.getBlockID()];

			if (world.setBlock(x, y, z, this.getBlockID(), jiQiMetadata, 3))
			{
				if (world.getBlockId(x, y, z) == this.getBlockID())
				{
					if (itemStack.getItemDamage() < 9)
					{
						ITier tileEntity = (ITier) world.getBlockTileEntity(x, y, z);

						if (tileEntity != null)
						{
							if (itemStack.getItemDamage() < 3)
							{
								tileEntity.setTier(itemStack.getItemDamage());
							}
							else if (itemStack.getItemDamage() < 6)
							{
								tileEntity.setTier(itemStack.getItemDamage() - 3);
							}
							else if (itemStack.getItemDamage() < 9)
							{
								tileEntity.setTier(itemStack.getItemDamage() - 6);
							}
						}
					}

					Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
				}

				return true;
			}
		}

		return false;
	}
}
