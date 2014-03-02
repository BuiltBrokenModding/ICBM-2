package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.platform.gui.GuiTurretPlatform;
import icbm.sentry.render.SentryRenderAATurret;
import icbm.sentry.render.SentryRenderGunTurret;
import icbm.sentry.render.SentryRenderLaserTurret;
import icbm.sentry.render.SentryRenderRailGun;
import icbm.sentry.turret.EntitySentryFake;
import icbm.sentry.turret.Sentry;
import icbm.sentry.turret.SentryRegistry;
import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.SentryGun;
import icbm.sentry.turret.auto.SentryLaser;
import icbm.sentry.turret.mount.MountedRailGun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.render.FxLaser;
import calclavia.lib.render.item.GlobalItemRenderer;
import calclavia.lib.render.item.ISimpleItemRenderer;
import calclavia.lib.utility.nbt.NBTUtility;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		super.preInit();
	}

	@Override
	public void init()
	{
		super.init();

		/** TileEntities */
		RenderingRegistry.registerEntityRenderingHandler(EntitySentryFake.class, new EmptyRenderer());

		// Sentry render registry TODO find a way to automate
		SentryRegistry.registerSentryRenderer(TurretAntiAir.class, new SentryRenderAATurret());
		SentryRegistry.registerSentryRenderer(SentryGun.class, new SentryRenderGunTurret());
		SentryRegistry.registerSentryRenderer(SentryLaser.class, new SentryRenderLaserTurret());
		SentryRegistry.registerSentryRenderer(MountedRailGun.class, new SentryRenderRailGun());

		GlobalItemRenderer.register(ICBMSentry.blockTurret.blockID, new ISimpleItemRenderer()
		{
			@Override
			public void renderInventoryItem(ItemStack itemStack)
			{
				Class<? extends Sentry> sentry = SentryRegistry.getSentry(NBTUtility.getNBTTagCompound(itemStack).getString("unlocalizedName"));
				if (sentry != null)
					SentryRegistry.getRenderFor(sentry).renderInventoryItem(itemStack);
			}
		});
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof TileTurretPlatform)
		{
			return new GuiTurretPlatform(player.inventory, (TileTurretPlatform) tile);
		}

		return null;
	}

	@Override
	public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age)
	{
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FxLaser(world, position, target, red, green, blue, age));
	}
}
