package com.builtbroken.icbm;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.RenderMissile;
import com.builtbroken.icbm.content.rocketlauncher.RenderRocketLauncher;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import com.builtbroken.lib.render.fx.*;
import com.builtbroken.lib.transform.vector.Vector3;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private boolean disableReflectionFX = false;

    @Override
    public void init()
    {
        super.init();

        MinecraftForgeClient.registerItemRenderer(ICBM.itemRocketLauncher, new RenderRocketLauncher());

        RenderMissile render = new RenderMissile(0.5F);
        MinecraftForgeClient.registerItemRenderer(ICBM.itemMissile, render);
        RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, render);

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public int getParticleSetting()
    {
        return Minecraft.getMinecraft().gameSettings.particleSetting;
    }

    @Override
    public void spawnParticle(String name, World world, Vector3 position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
    {
        EntityFX fx = null;

        if (name.equals("smoke"))
        {
            fx = new FXSmoke(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("missile_smoke"))
        {
            fx = (new FXSmoke(world, position, red, green, blue, scale, distance)).setAge(100);
        }
        else if (name.equals("portal"))
        {
            fx = new FXEnderPortalPartical(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("antimatter"))
        {
            //fx = new FXAntimatterPartical(world, position, red, green, blue, scale, distance);
        }
        else if (name.equals("digging"))
        {
            fx = new EntityDiggingFX(world, position.x(), position.y(), position.z(), motionX, motionY, motionZ, Block.getBlockById((int) red), 0, (int) green);
            fx.multipleParticleScaleBy(blue);
        }
        else if (name.equals("shockwave"))
        {
            fx = new FXShockWave(world, position, red, green, blue, scale, distance);
        }

        if (fx != null)
        {
            fx.motionX = motionX;
            fx.motionY = motionY;
            fx.motionZ = motionZ;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

    @Override
    public void spawnShock(World world, Vector3 startVec, Vector3 targetVec)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXElectricBolt(world, startVec, targetVec, 0));
    }

    @Override
    public void spawnShock(World world, Vector3 startVec, Vector3 targetVec, int duration)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXElectricBoltSpawner(world, startVec, targetVec, 0, duration));
    }

    @Override
    public List<Entity> getEntityFXs()
    {
        if (!this.disableReflectionFX)
        {
            try
            {
                EffectRenderer renderer = Minecraft.getMinecraft().effectRenderer;
                List[] fxLayers = ReflectionHelper.getPrivateValue(EffectRenderer.class, renderer, 2);

                return fxLayers[0];
            } catch (Exception e)
            {
                ICBM.LOGGER.log(Level.ERROR, "Failed to use reflection on entity effects.");
                e.printStackTrace();
                this.disableReflectionFX = true;
            }
        }
        return null;
    }
}
