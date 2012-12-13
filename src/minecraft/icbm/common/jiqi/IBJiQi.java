package icbm.common.jiqi;

import icbm.common.ZhuYao;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import universalelectricity.prefab.implement.ITier;

public class IBJiQi extends ItemBlock
{
	private static final int spawnID = ZhuYao.bJiQi.blockID;

	public IBJiQi(int id)
	{
		super(id);
		this.setIconIndex(48);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	public int getIconFromDamage(int par1)
	{
		return this.iconIndex + par1;
	}

	@Override
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + itemstack.getItemDamage();
	}

	@Override
	public String getItemName()
	{
		return "item.machine";
	}

	@Override
	public String getTextureFile()
	{
		return ZhuYao.ITEM_TEXTURE_FILE;
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

		if (BJiQi.canBePlacedAt(world, x, y, z, jiQiMetadata, entityPlayer))
		{
			Block var9 = Block.blocksList[IBJiQi.spawnID];

			if (world.setBlockAndMetadataWithNotify(x, y, z, this.spawnID, jiQiMetadata))
			{
				if (world.getBlockId(x, y, z) == this.spawnID)
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

					Block.blocksList[IBJiQi.spawnID].onBlockPlacedBy(world, x, y, z, entityPlayer);
				}

				return true;
			}
		}

		return false;
	}
}
