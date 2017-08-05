package com.builtbroken.icbm.client;

import com.builtbroken.icbm.CommonProxy;
import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.api.missile.IMissileEntity;
import com.builtbroken.icbm.api.modules.IMissile;
import com.builtbroken.icbm.client.ec.*;
import com.builtbroken.icbm.content.blast.entity.slime.EntitySlimeRain;
import com.builtbroken.icbm.content.blast.entity.slime.RenderSlimeRain;
import com.builtbroken.icbm.content.blast.explosive.BlastPathTester;
import com.builtbroken.icbm.content.blast.explosive.ExMicroQuake;
import com.builtbroken.icbm.content.blast.potion.ExFlash;
import com.builtbroken.icbm.content.blast.troll.BlastMidasOre;
import com.builtbroken.icbm.content.blast.util.ExOrePuller;
import com.builtbroken.icbm.content.crafting.station.small.TileSmallMissileWorkstationClient;
import com.builtbroken.icbm.content.crafting.station.small.auto.TileSMAutoRenderListener;
import com.builtbroken.icbm.content.fragments.EntityFragment;
import com.builtbroken.icbm.content.fragments.RenderFragment;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.ItemRendererAntennaFrame;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.TESRAntenna;
import com.builtbroken.icbm.content.launcher.controller.remote.antenna.TileAntennaPart;
import com.builtbroken.icbm.content.launcher.controller.remote.display.TileSiloInterfaceClient;
import com.builtbroken.icbm.content.launcher.launcher.StandardLauncherRenderListener;
import com.builtbroken.icbm.content.launcher.listeners.TileMissileRenderListener;
import com.builtbroken.icbm.content.missile.client.RenderMissile;
import com.builtbroken.icbm.content.missile.entity.EntityMissile;
import com.builtbroken.icbm.content.rail.EntityMissileCart;
import com.builtbroken.icbm.content.rail.RenderMissileCart;
import com.builtbroken.icbm.content.rocketlauncher.RenderRocketLauncher;
import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.client.json.imp.IEffectData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.json.imp.IJsonGenObject;
import com.builtbroken.mc.lib.json.processors.block.JsonBlockListenerProcessor;
import com.builtbroken.mc.lib.render.fx.FXElectricBolt;
import com.builtbroken.mc.lib.render.fx.FXElectricBoltSpawner;
import com.builtbroken.mc.lib.render.fx.FXEnderPortalPartical;
import com.builtbroken.mc.lib.render.fx.FXSmoke;
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
import net.minecraft.nbt.NBTTagCompound;
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
    public void loadJsonContentHandlers()
    {
        super.loadJsonContentHandlers();
        JsonBlockListenerProcessor.addBuilder(new TileMissileRenderListener.Builder());
        JsonBlockListenerProcessor.addBuilder(new StandardLauncherRenderListener.Builder());
        JsonBlockListenerProcessor.addBuilder(new TileSMAutoRenderListener.Builder());
    }

    @Override
    public void preInit()
    {
        super.preInit();
        ICBM_API.blockMissileWorkstation = ICBM.INSTANCE.getManager().newBlock("SmallMissileWorkStation", TileSmallMissileWorkstationClient.class);
        ICBM_API.blockCommandSiloDisplay = ICBM.INSTANCE.getManager().newBlock("icbmCommandSiloDisplay", TileSiloInterfaceClient.class);

        //ICBM.blockStandardLauncher.setCreativeTab(null);
        //NEIProxy.hideItem(ICBM.blockStandardLauncher);
    }

    @Override
    public void init()
    {
        super.init();
        SharedAssets.loadResources();
        ClientRegistry.bindTileEntitySpecialRenderer(TileAntennaPart.class, new TESRAntenna());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ICBM_API.blockAntenna), new ItemRendererAntennaFrame());
        MinecraftForgeClient.registerItemRenderer(ICBM_API.itemRocketLauncher, new RenderRocketLauncher());

        //MinecraftForgeClient.registerItemRenderer(ICBM.itemMissile, RenderMissile.INSTANCE);
        RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, RenderMissile.INSTANCE);

        RenderingRegistry.registerEntityRenderingHandler(EntityMissileCart.class, new RenderMissileCart());

        RenderingRegistry.registerEntityRenderingHandler(EntityFragment.class, new RenderFragment());

        RenderingRegistry.registerEntityRenderingHandler(EntitySlimeRain.class, new RenderSlimeRain());

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
            //fx = new FXShockWave(world, position, red, green, blue, scale, distance);
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

            if (entity instanceof EntityMissile)
            {
                IMissile missile = ((EntityMissile) entity).getMissile();
                if (missile.getEngine() != null)
                {
                    Color fireColor = missile.getEngine().getEngineFireColor(entity instanceof IMissileEntity ? (IMissileEntity) entity : null, missile);
                    Color smokeColor = missile.getEngine().getEngineSmokeColor(entity instanceof IMissileEntity ? (IMissileEntity) entity : null, missile);

                    NBTTagCompound nbt = new NBTTagCompound();

                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setInteger("color", fireColor.getRGB());
                    nbt.setTag("fireColor", tag);

                    tag = new NBTTagCompound();
                    tag.setInteger("color", smokeColor.getRGB());
                    nbt.setTag("smokeColor", tag);

                    if(missile instanceof IJsonGenObject)
                    {
                        String contentID = ((IJsonGenObject) missile).getContentID(); //TODO change to use engine content ID
                        IEffectData data = ClientDataHandler.INSTANCE.getEffect(contentID + ".engine.trail");
                        if (data != null)
                        {
                            data.trigger(
                                    ((EntityMissile) entity).world(), ((EntityMissile) entity).x(), ((EntityMissile) entity).y(), ((EntityMissile) entity).z(), //TODO move spawn point to rear of missile
                                    vel.x(), vel.y(), vel.z(),
                                    false, nbt);
                        }
                    }
                }
            }
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
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "EntitySpawn", new ECSpawn());
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
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Regen", new ECRegen());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "RegenLocal", new ECRegenLocal());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "MicroQuake", new ExMicroQuake());//TODO Add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Cake", new ECCake());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "BiomeChange", new ECBiomeChange());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "OrePuller", new ExOrePuller()); //TODO add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "SlimeRain", new ECSlimeRain());
        //ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Emp", new ECEmp());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Gravity", new ECGravity());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Microwave", new ECMicrowave());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Nuke", new ECNuke());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Flash", new ExFlash()); //TODO Add texture
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "Radiation", new ECRadiation());
        ExplosiveRegistry.registerOrGetExplosive(ICBM.DOMAIN, "MidasOre", new BlastMidasOre.ExMidasOre()); //TODO Add Texture
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
