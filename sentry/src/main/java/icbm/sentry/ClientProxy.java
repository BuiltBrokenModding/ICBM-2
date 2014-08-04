package icbm.sentry;

import icbm.core.prefab.EmptyRenderer;
import icbm.sentry.interfaces.ITurret;
import icbm.sentry.platform.TileTurretPlatform;
import icbm.sentry.platform.gui.GuiTurretPlatform;
import icbm.sentry.platform.gui.user.GuiUserAccess;
import icbm.sentry.render.RenderAATurret;
import icbm.sentry.render.RenderCrossBowTurret;
import icbm.sentry.render.RenderGunTurret;
import icbm.sentry.render.RenderLaserTurret;
import icbm.sentry.render.RenderRailgun;
import icbm.sentry.turret.EntityMountableDummy;
import icbm.sentry.turret.Turret;
import icbm.sentry.turret.TurretRegistry;
import icbm.sentry.turret.auto.TurretAntiAir;
import icbm.sentry.turret.auto.TurretAutoBow;
import icbm.sentry.turret.auto.TurretGun;
import icbm.sentry.turret.auto.TurretLaser;
import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.mounted.MountedRailgun;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import resonant.api.items.ISimpleItemRenderer;
import resonant.lib.render.fx.FxLaser;
import resonant.lib.render.item.GlobalItemRenderer;
import resonant.lib.utility.nbt.NBTUtility;
import universalelectricity.api.vector.IVector3;
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
        TurretRegistry.registerSentryRenderer(TurretAutoBow.class, new RenderCrossBowTurret());

        GlobalItemRenderer.register(ICBMSentry.blockTurret.blockID, new ISimpleItemRenderer()
        {
            @Override
            public void renderInventoryItem(ItemStack itemStack)
            {
                Class<? extends ITurret> sentry = TurretRegistry.getSentry(NBTUtility.getNBTTagCompound(itemStack).getString("unlocalizedName"));
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
            if (ID == 0)
                return new GuiTurretPlatform(player, (TileTurretPlatform) tile);
        }
        if (tile instanceof TileTurret)
        {
            if (ID == 1)
                return new GuiUserAccess(player, tile);
        }
        return null;
    }

    public void renderBeam(World world, IVector3 position, IVector3 hit, Color color, int age)
    {
        renderBeam(world, position, hit, color.getRed(), color.getGreen(), color.getBlue(), age);
    }

    @Override
    public void renderBeam(World world, IVector3 position, IVector3 target, float red, float green, float blue, int age)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FxLaser(world, position, target, red, green, blue, age));
    }
}
