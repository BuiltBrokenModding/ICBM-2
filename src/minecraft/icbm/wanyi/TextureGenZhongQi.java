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
	public double field_94244_i;
	public double field_94242_j;

	public TextureGenZhongQi()
	{
		super(ZhuYaoWanYi.itGenZongQi.getUnlocalizedName().replace("item.", ""));
	}

	@Override
	public void func_94219_l()
	{
		// TODO: REGISTER TEXTURE FX

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

		for (d6 = angel - this.field_94244_i; d6 < -Math.PI; d6 += (Math.PI * 2D))
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

		this.field_94242_j += d6 * 0.1D;
		this.field_94242_j *= 0.8D;
		this.field_94244_i += this.field_94242_j;

		int i;

		for (i = (int) ((this.field_94244_i / (Math.PI * 2D) + 1.0D) * (double) this.field_94226_b.size()) % this.field_94226_b.size(); i < 0; i = (i + this.field_94226_b.size()) % this.field_94226_b.size())
		{
			;
		}

		if (i != this.field_94222_f)
		{
			this.field_94222_f = i;
			this.field_94228_a.func_94281_a(this.field_94224_d, this.field_94225_e, (Texture) this.field_94226_b.get(this.field_94222_f), this.field_94227_c);
		}
	}
}
