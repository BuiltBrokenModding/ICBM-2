package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.platform.gui.GuiTurretPlatform;
import icbm.sentry.render.RenderAATurret;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.render.RenderLaserTurret;
import icbm.sentry.render.RenderRailgun;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.TurretGun;
import icbm.sentry.turret.auto.TurretLaser;
import icbm.sentry.turret.mounted.MountedRailgun;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import calclavia.lib.render.fx.FxLaser;
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
		RenderingRegistry.registerEntityRenderingHandler(EntityMountableDummy.class, new EmptyRenderer());

		// Sentry render registry TODO find a way to automate
		TurretRegistry.registerSentryRenderer(TurretAntiAir.class, new RenderAATurret());
		TurretRegistry.registerSentryRenderer(TurretGun.class, new RenderGunTurret());
		TurretRegistry.registerSentryRenderer(TurretLaser.class, new RenderLaserTurret());
		TurretRegistry.registerSentryRenderer(MountedRailgun.class, new RenderRailgun());

		GlobalItemRenderer.register(ICBMSentry.blockTurret.blockID, new ISimpleItemRenderer()
		{
			@Override
			public void renderInventoryItem(ItemStack itemStack)
			{
				Class<? extends Turret> sentry = TurretRegistry.getSentry(NBTUtility.getNBTTagCompound(itemStack).getString("unlocalizedName"));
				if (sentry != null)
					TurretRegistry.getRenderFor(sentry).renderInventoryItem(itemStack);
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
