package icbm.zhapin;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class IBZhaDan extends ItemBlock
{
	public IBZhaDan(int id)
	{
		super(id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.setBlockWithNotify(x, y, z, this.getBlockID())) { return false; }

		if (world.getBlockId(x, y, z) == this.getBlockID())
		{
			((TZhaDan) world.getBlockTileEntity(x, y, z)).explosiveID = stack.getItemDamage();
			Block.blocksList[this.getBlockID()].updateBlockMetadata(world, x, y, z, side, hitX, hitY, hitZ);
			Block.blocksList[this.getBlockID()].onBlockPlacedBy(world, x, y, z, player);
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
		return (new StringBuilder()).append(super.getItemName()).append(".").append(ZhaPin.list[itemstack.getItemDamage()] + " Explosives").toString();
	}
}
