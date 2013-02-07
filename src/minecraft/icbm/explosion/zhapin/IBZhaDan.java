package icbm.explosion.zhapin;

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
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		if (!world.setBlockWithNotify(x, y, z, this.getBlockID())) { return false; }

		if (world.getBlockId(x, y, z) == this.getBlockID())
		{
			((TZhaDan) world.getBlockTileEntity(x, y, z)).haoMa = stack.getItemDamage();
			Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player);
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
	public String getItemNameIS(ItemStack itemstack)
	{
		return this.getItemName() + "." + ZhaPin.list[itemstack.getItemDamage()].getName();
	}

	@Override
	public String getItemName()
	{
		return "icbm.explosive";
	}
}
