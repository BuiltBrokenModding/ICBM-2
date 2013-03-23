package icbm.wanyi;

import icbm.core.ItGenZongQi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureGenZhongQi extends TextureStitched
{
	public double currentAngle;
	public double angleDelta;

	public TextureGenZhongQi()
	{
		super(ZhuYaoWanYi.itGenZongQi.getUnlocalizedName().replace("item.", ""));
	}

	@Override
	public void updateAnimation()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		World world = minecraft.theWorld;
		EntityPlayer player = minecraft.thePlayer;

		double angel = 0;

		if (world != null)
		{
			double xDifference = 0;
			double zDifference = 0;

			if (player.getCurrentEquippedItem() != null)
			{
				if (player.getCurrentEquippedItem().itemID == ZhuYaoWanYi.itGenZongQi.itemID)
				{
					ItemStack itemStack = player.getCurrentEquippedItem();

					Entity trackingEntity = ItGenZongQi.getTrackingEntity(FMLClientHandler.instance().getClient().theWorld, itemStack);

					if (trackingEntity != null)
					{
						xDifference = (double) trackingEntity.posX - player.posX;
						zDifference = (double) trackingEntity.posZ - player.posZ;
					}
				}
			}

			player.rotationYaw %= 360.0D;
			angel = -((player.rotationYaw - 90.0D) * Math.PI / 180.0D - Math.atan2(zDifference, xDifference));
		}

		double d6;

		for (d6 = angel - this.currentAngle; d6 < -Math.PI; d6 += (Math.PI * 2D))
		{
			;
		}

		while (d6 >= Math.PI)
		{
			d6 -= (Math.PI * 2D);
		}

		if (d6 < -1.0D)
		{
			d6 = -1.0D;
		}

		if (d6 > 1.0D)
		{
			d6 = 1.0D;
		}

		this.angleDelta += d6 * 0.1D;
		this.angleDelta *= 0.8D;
		this.currentAngle += this.angleDelta;

		int i;

		for (i = (int) ((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double) this.textureList.size()) % this.textureList.size(); i < 0; i = (i + this.textureList.size()) % this.textureList.size())
		{
			;
		}

		if (i != this.frameCounter)
		{
			this.frameCounter = i;
			this.textureSheet.copyFrom(this.originX, this.originY, (Texture) this.textureList.get(this.frameCounter), this.rotated);
		}
	}
}
