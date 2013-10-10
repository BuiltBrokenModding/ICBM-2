package icbm.explosion;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler
{
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (type.equals(EnumSet.of(TickType.PLAYER)))
		{
			try
			{
				EntityPlayer player = (EntityPlayer) tickData[0];

				ItemStack currentItem = player.getCurrentEquippedItem();

				if (currentItem != null && (player != Minecraft.getMinecraft().renderViewEntity || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0))
				{
					if (currentItem.itemID == ZhuYaoZhaPin.itFaSheQi.itemID)
					{
						if (player.getItemInUseCount() <= 0)
						{
							player.setItemInUse(currentItem, Integer.MAX_VALUE);
						}
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(this.getLabel() + " failed to tick properly.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{

	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "ICBM|Explosion";
	}

}
