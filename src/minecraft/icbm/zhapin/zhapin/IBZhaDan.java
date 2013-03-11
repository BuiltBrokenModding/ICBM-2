package icbm.zhapin.zhapin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IBZhaDan extends ItemBlock
{
	public IBZhaDan(int id)
	{
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (!world.setBlockAndMetadataWithNotify(x, y, z, this.getBlockID(), 0, 3))
		{
			return false;
		}

		if (world.getBlockId(x, y, z) == this.getBlockID())
		{
			((TZhaDan) world.getBlockTileEntity(x, y, z)).haoMa = itemStack.getItemDamage();
			Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player, itemStack);
			Block.blocksList[this.getBlockID()].onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return this.getUnlocalizedName() + "." + ZhaPin.list[itemstack.getItemDamage()].getUnlocalizedName();
	}

	@Override
	public String getUnlocalizedName()
	{
		return "icbm.explosive";
	}
}
