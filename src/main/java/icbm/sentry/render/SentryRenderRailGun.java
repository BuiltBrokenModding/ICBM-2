package icbm.sentry.render;

import icbm.Reference;
import icbm.sentry.models.ModelRailgun;
import icbm.sentry.turret.block.TileSentry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SentryRenderRailGun extends SentryRenderer
{
    public static final ModelRailgun MODEL = new ModelRailgun();

    public SentryRenderRailGun()
    {
        super(new ResourceLocation(Reference.DOMAIN, Reference.MODEL_PATH + "railgun.png"));
    }

    @Override
    public void render(ForgeDirection side, TileSentry tile, float yaw, float pitch)
    {
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(180F, 0F, 1F, 0F);
        MODEL.render((float) Math.toRadians(yaw), (float) Math.toRadians(pitch), 0.0625F);
    }

    @Override
    public void renderItem()
    {
        // TODO Auto-generated method stub

    }
}