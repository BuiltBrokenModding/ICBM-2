package com.builtbroken.icbm.content.ams;

import com.builtbroken.icbm.client.Assets;
import com.builtbroken.jlib.helpers.MathHelper;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.render.fx.RocketFx;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.obj.GroupObject;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/5/2016.
 */
public class TileAMSClient extends TileAMS
{
    double render_yaw = 0;
    double render_pitch = 0;

    long lastRenderFrame = System.nanoTime();

    GroupObject turret;
    GroupObject base;

    @Override
    public Tile newTile()
    {
        return new TileAMSClient();
    }

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Lerp rotation values so not to have snapping effects(looks bad)
        long delta = System.nanoTime() - lastRenderFrame;
        render_yaw = MathHelper.lerp(render_yaw % 360, aim.yaw(), (double) delta / 50000000.0);
        render_pitch = MathHelper.lerp(render_pitch % 360, aim.pitch(), (double) delta / 50000000.0);
        lastRenderFrame = System.nanoTime();

        if (turret == null || base == null)
        {
            for (GroupObject object : Assets.AMS_MODEL.groupObjects)
            {
                if (object.name.equals("TurretBase_Cylinder"))
                {
                    base = object;
                }
                else if (object.name.equals("Gun_Cube.002"))
                {
                    turret = object;
                }
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf(), pos.zf() + 0.5f);

        GL11.glRotatef((float) ((render_yaw + 90f) % 360), 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.AMS_TEXTURE);
        base.render();

        GL11.glTranslatef(0, 0.5f, 0);
        GL11.glRotatef((float) render_pitch, 0, 0, 1);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.AMS_TOP_TEXTURE);
        turret.render();

        GL11.glPopMatrix();
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(buf, id, player, type))
        {
            if (id == 2)
            {
                fireWeaponEffect();
                return true;
            }
            return false;
        }
        return true;
    }

    protected void fireWeaponEffect()
    {
        Pos aimVector = aim.toVector();
        Pos pos = new Pos(x(), y(), z()).add(0.5).add(aimVector);
        RocketFx fx = new RocketFx(world(), pos.x(), pos.y(), pos.z(), aimVector.x() * 0.2, aimVector.y() * 0.2, aimVector.z() * 0.2);
        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }

    @Override
    public void readDescPacket(ByteBuf buf)
    {
        super.readDescPacket(buf);
        aim.yaw_$eq(buf.readDouble());
        aim.pitch_$eq(buf.readDouble());
    }
}
