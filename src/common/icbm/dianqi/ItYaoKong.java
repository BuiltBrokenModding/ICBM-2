package icbm.dianqi;

import icbm.ZhuYao;
import icbm.api.ICBM;
import icbm.zhapin.TZhaDan;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.ItemElectric;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class ItYaoKong extends ItemElectric
{
	public static final int BAN_JING = 100;
	public static final int YONG_DIAN_LIANG = 1000;

	public ItYaoKong(int par1, int par2)
	{
		super(par1);
		this.iconIndex = par2;
		this.setItemName("remoteDetonator");
		this.setCreativeTab(ZhuYao.TAB);
	}

	@Override
	public String getTextureFile()
	{
		return ICBM.ITEM_TEXTURE_FILE;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
	 * world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (par2World.isRemote)
		{
			MovingObjectPosition objectMouseOver = par3EntityPlayer.rayTrace(BAN_JING, 1);

			if (objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
			{
				TileEntity tileEntity = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
				int blockID = par2World.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
				TileEntity tile = par2World.getBlockTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

				if (tile != null)
				{
					int explosiveID = ((TZhaDan) tile).explosiveID;

					if (blockID == ZhuYao.bJiQi.blockID)
					{
						return par1ItemStack;
					}
					else if (tileEntity instanceof TZhaDan && (explosiveID == ZhaPin.yaSuo.getID() || explosiveID == ZhaPin.tuPuo.getID()))
					{
						// Check for electricity
						if (this.getJoules(par1ItemStack) > YONG_DIAN_LIANG)
						{
							PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYao.CHANNEL, tileEntity, (byte) 2));
							return par1ItemStack;
						}
						else
						{
							par3EntityPlayer.addChatMessage("Remote out of electricity!");
						}
					}
				}
			}
		}

		return par1ItemStack;
	}

	@Override
	public double getVoltage()
	{
		return 20;
	}

	@Override
	public double getMaxJoules(Object... data)
	{
		return 50000;
	}
}
