package com.builtbroken.icbm.client;

import com.builtbroken.icbm.CommonProxy;
import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.blast.*;
import com.builtbroken.icbm.content.ams.TileAMSClient;
import com.builtbroken.icbm.content.blast.entity.ExSlimeRain;
import com.builtbroken.icbm.content.blast.entity.ExplosiveHandlerSpawn;
import com.builtbroken.icbm.content.blast.explosive.BlastPathTester;
import com.builtbroken.icbm.content.blast.explosive.ExMicroQuake;
import com.builtbroken.icbm.content.blast.util.ExOrePuller;
import com.builtbroken.icbm.content.blast.util.ExRegen;
import com.builtbroken.icbm.content.blast.util.ExRegenLocal;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.icbm.content.crafting.station.warhead.TileWarheadStationClient;
import com.builtbroken.icbm.content.fof.TileFoFClient;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.ItemRendererAntennaFrame;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.TESRAntenna;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.TileAntennaPart;
import com.builtbroken.icbm.content.launcher.controller.remote.display.TileSiloInterfaceClient;
import com.builtbroken.icbm.content.launcher.launcher.standard.TileStandardLauncherClient;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.icbm.content.missile.RenderMissile;
import com.builtbroken.icbm.content.rail.entity.EntityCart;
import com.builtbroken.icbm.content.rail.entity.RenderCart;
import com.builtbroken.icbm.content.rail.powered.TilePowerRailClient;
import com.builtbroken.icbm.content.rocketlauncher.RenderRocketLauncher;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.mod.compat.nei.NEIProxy;
import com.builtbroken.mc.lib.render.fx.*;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ExplosiveHandlerGeneric;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private boolean disableReflectionFX = false;

    @Override
    public void preInit()
    {
        super.preInit();
        ICBM.blockMissileWorkstation = ICBM.INSTANCE.getManager().newBlock("SmallMissileWorkStation", TileSmallMissileWorkstationClient.class);
        ICBM.blockStandardLauncher = ICBM.INSTANCE.getManager().newBlock("StandardMissileLauncher", TileStandardLauncherClient.class);
        ICBM.blockAMS = ICBM.INSTANCE.getManager().newBlock("ICBMxAMS", TileAMSClient.class);
        ICBM.blockFoFStation = ICBM.INSTANCE.getManager().newBlock("ICBMxFoF", TileFoFClient.class);
        ICBM.blockCommandSiloDisplay = ICBM.INSTANCE.getManager().newBlock("icbmCommandSiloDisplay", TileSiloInterfaceClient.class);
        ICBM.blockWarheadWorkstation = ICBM.INSTANCE.getManager().newBlock("icbmWarheadWorkstation", TileWarheadStationClient.class);
        ICBM.blockMissileCartRotator =  ICBM.INSTANCE.getManager().newBlock("icbmCartPowerRail", TilePowerRailClient.class);

        ICBM.blockStandardLauncher.setCreativeTab(null);
        NEIProxy.hideItem(ICBM.blockStandardLauncher);
    }

    @Override
    public void init()
    {
        super.init();
        SharedAssets.loadModels();
        ClientRegistry.bindTileEntitySpecialRenderer(TileAntennaPart.class, new TESRAntenna());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ICBM.blockAntenna), new ItemRendererAntennaFrame());
        MinecraftForgeClient.registerItemRenderer(ICBM.itemRocketLauncher, new RenderRocketLauncher());

        MinecraftForgeClient.registerItemRenderer(ICBM.itemMissile, RenderMissile.INSTANCE);
        RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, RenderMissile.INSTANCE);

        RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new RenderCart());

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public int getParticleSetting()
    {
        return Minecraft.getMinecraft().gameSettings.particleSetting;
    }

    @Override
    public void spawnParticle(String name, World world, Pos position, double motionX, double motionY, double motionZ, float red, float green, float blue, float scale, double distance)
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
    public void spawnRocketTail(Entity entity)
    {
        //TODO add interface to allow rocket to control effects

        //Don't render at all if particles are on min
        if (Minecraft.getMinecraft().gameSettings.particleSetting != 2)
        {
            Pos motion = new Pos(entity.motionX, entity.motionY, entity.motionZ).normalize();
            Pos vel = new Pos((entity.worldObj.rand.nextFloat() - 0.5f) / 8f, (entity.worldObj.rand.nextFloat() - 0.5f) / 8f, (entity.worldObj.rand.nextFloat() - 0.5f) / 8f);
            vel = vel.multiply(motion);

            doRocketFire(entity, vel);
            doRocketSmoke(entity, vel);
        }
    }

    @Override
    public void doRocketFire(Entity entity, Pos vel)
    {
        EntityFX fx = new FxRocketFire(entity.worldObj, entity.posX, entity.posY, entity.posZ, vel.x(), vel.y(), vel.z());

        //Gen and add smoke effect
        if (entity instanceof EntityMissile)
        {
            IMissile missile = ((EntityMissile) entity).getMissile();
            if (missile.getEngine() != null)
            {
                Color color = missile.getEngine().getEngineFireColor(entity instanceof IMissileEntity ? (IMissileEntity) entity : null, missile);
                if(color != null)
                {
                    fx = new FxRocketFire(entity.worldObj, color, entity.posX, entity.posY, entity.posZ, vel.x(), vel.y(), vel.z());
                }
            }
        }

        //Gen and add fire effect
        Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }

    @Override
    public void doRocketSmoke(Entity entity, Pos vel)
    {
        //Only render massive smoke on fancy and with good particle settings
        if (Minecraft.getMinecraft().gameSettings.fancyGraphics && Minecraft.getMinecraft().gameSettings.particleSetting != 1)
        {
            EntityFX fx = new FxRocketSmokeTrail(entity.worldObj, entity.posX, entity.posY, entity.posZ, vel.x(), vel.y(), vel.z(), 200);
            //Gen and add smoke effect
            if (entity instanceof EntityMissile)
            {
                IMissile missile = ((EntityMissile) entity).getMissile();
                if (missile.getEngine() != null)
                {
                    Color color = missile.getEngine().getEngineSmokeColor(entity instanceof IMissileEntity ? (IMissileEntity) entity : null, missile);
                    if(color != null)
                    {
                        fx = new FxRocketSmokeTrail(entity.worldObj, color, entity.posX, entity.posY, entity.posZ, vel.x(), vel.y(), vel.z(), 200);
                    }
                }
            }
            Minecraft.getMinecraft().effectRenderer.addEffect(fx);
        }
    }

    @Override
    public void playRocketAudio(Entity entity)
    {
        entity.worldObj.playSoundAtEntity(entity, "icbm:engine.main", ICBM.missile_engine_volume, 1.0F);
    }

    @Override
    public void spawnShock(World world, Pos startVec, Pos targetVec)
    {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXElectricBolt(world, startVec, targetVec, 0));
    }

    @Override
    public void spawnShock(World world, Pos startVec, Pos targetVec, int duration)
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
            }
            catch (Exception e)
            {
                ICBM.INSTANCE.logger().log(Level.ERROR, "Failed to use reflection on entity effects.");
                e.printStackTrace();
                this.disableReflectionFX = true;
            }
        }
        return null;
    }

    @Override
    public void registerExplosives()
    {
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "EntitySpawn", new ExplosiveHandlerSpawn());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ExoThermic", new ECExo());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "EndoThermic", new ECEndo());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Fragment", new ECFragment());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "ArrowFragment", ExplosiveRegistry.get("Fragment"));
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Antimatter", new ECAntimatter());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "FireBomb", new ECFireBomb());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "FlashFire", new ECFlashFire());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "EnderBlocks", new ECEnderBlocks());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "TorchEater", new ECTorchEater());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "PlantLife", new ECPlantLife());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "AntiPlant", new ECAntiPlant());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Regen", new ExRegen());//TODO Add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "RegenLocal", new ExRegenLocal());//TODO Add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "MicroQuake", new ExMicroQuake());//TODO Add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Cake", new ECCake());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "BiomeChange", new ECBiomeChange());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "OrePuller", new ExOrePuller());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SlimeRain", new ExSlimeRain()); //TODO add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Emp", new ECEmp());
        if (Engine.runningAsDev)
        {
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SimplePathTest1", new ExplosiveHandlerGeneric("SimplePathTest1", BlastPathTester.class, 1));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SimplePathTest2", new ExplosiveHandlerGeneric("SimplePathTest2", BlastPathTester.class, 2));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SimplePathTest3", new ExplosiveHandlerGeneric("SimplePathTest3", BlastPathTester.class, 3));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SimplePathTest10", new ExplosiveHandlerGeneric("SimplePathTest10", BlastPathTester.class, 10));
            ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SimplePathTest20", new ExplosiveHandlerGeneric("SimplePathTest20", BlastPathTester.class, 20));
        }
    }
}
