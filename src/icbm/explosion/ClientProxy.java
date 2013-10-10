package icbm.explosion;

import icbm.core.AudioHandler;
import icbm.core.ICBMCore;
import icbm.explosion.cart.EntityBombCart;
import icbm.explosion.explosive.EntityExplosion;
import icbm.explosion.fx.FXElectricBolt;
import icbm.explosion.fx.FXElectricBoltSpawner;
import icbm.explosion.fx.FXAntimatterPartical;
import icbm.explosion.fx.FXEnderPortalPartical;
import icbm.explosion.fx.FXYan;
import icbm.explosion.fx.FXZhenBuo;
import icbm.explosion.gui.GDianCiQi;
import icbm.explosion.gui.GFaSheDi;
import icbm.explosion.gui.GFaSheShiMuo;
import icbm.explosion.gui.GLeiDaTai;
import icbm.explosion.gui.GXiaoFaSheQi;
import icbm.explosion.gui.GYinDaoQi;
import icbm.explosion.machines.TFaSheDi;
import icbm.explosion.machines.TFaSheJia;
import icbm.explosion.machines.TFaSheShiMuo;
import icbm.explosion.machines.TileEntityRadarStation;
import icbm.explosion.machines.TileEntityMissileCoordinator;
import icbm.explosion.machines.TileEntityEmpTower;
import icbm.explosion.machines.TileEntityCruiseLauncher;
import icbm.explosion.potion.PDongShang;
import icbm.explosion.render.entity.RBaoZha;
import icbm.explosion.render.entity.RDaoDan;
import icbm.explosion.render.entity.REZhaDan;
import icbm.explosion.render.entity.RFeiBlock;
import icbm.explosion.render.entity.RGuangBang;
import icbm.explosion.render.entity.RShouLiuDan;
import icbm.explosion.render.entity.RSuiPian;
import icbm.explosion.render.item.RItDaoDan;
import icbm.explosion.render.item.RItFaSheQi;
import icbm.explosion.render.tile.RenderEmpTower;
import icbm.explosion.render.tile.RFaSheDi;
import icbm.explosion.render.tile.RFaSheJia;
import icbm.explosion.render.tile.RFaSheShiMuo;
import icbm.explosion.render.tile.BlockRenderHandler;
import icbm.explosion.render.tile.RenderRadarStation;
import icbm.explosion.render.tile.RenderCruiseLauncher;
import icbm.explosion.render.tile.RenderMissileCoordinator;
import icbm.explosion.render.tile.RenderBombBlock;
import icbm.explosion.zhapin.EntityGrenade;
import icbm.explosion.zhapin.EntityExplosive;
import icbm.explosion.zhapin.TileEntityExplosive;
import icbm.explosion.zhapin.daodan.EDaoDan;
import icbm.explosion.zhapin.daodan.ShengYinDaoDan;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	private boolean disableReflectionFX = false;

	@Override
	public void preInit()
	{
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(AudioHandler.INSTANCE);
	}

	@Override
	public void init()
	{
		super.init();

		MinecraftForgeClient.registerItemRenderer(ICBMExplosion.itFaSheQi.itemID, new RItFaSheQi());
		MinecraftForgeClient.registerItemRenderer(ICBMExplosion.itDaoDan.itemID, new RItDaoDan());

		RenderingRegistry.registerBlockHandler(new RenderBombBlock());
		RenderingRegistry.registerBlockHandler(new BlockRenderHandler());

		RenderingRegistry.registerEntityRenderingHandler(EntityExplosive.class, new REZhaDan());
		RenderingRegistry.registerEntityRenderingHandler(EDaoDan.class, new RDaoDan(0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosion.class, new RBaoZha());
		RenderingRegistry.registerEntityRenderingHandler(EntityFlyingBlock.class, new RFeiBlock());
		RenderingRegistry.registerEntityRenderingHandler(EGuang.class, new RGuangBang());
		RenderingRegistry.registerEntityRenderingHandler(ESuiPian.class, new RSuiPian());
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RShouLiuDan());
		RenderingRegistry.registerEntityRenderingHandler(EntityBombCart.class, new RenderMinecart());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCruiseLauncher.class, new RenderCruiseLauncher());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheDi.class, new RFaSheDi());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheShiMuo.class, new RFaSheShiMuo());
		ClientRegistry.bindTileEntitySpecialRenderer(TFaSheJia.class, new RFaSheJia());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRadarStation.class, new RenderRadarStation());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEmpTower.class, new RenderEmpTower());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMissileCoordinator.class, new RenderMissileCoordinator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExplosive.class, new RenderBombBlock());
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer entityPlayer, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileEntityCruiseLauncher)
		{
			return new GXiaoFaSheQi(entityPlayer.inventory, (TileEntityCruiseLauncher) tileEntity);
		}
		else if (tileEntity instanceof TFaSheShiMuo)
		{
			return new GFaSheShiMuo(((TFaSheShiMuo) tileEntity));
		}
		else if (tileEntity instanceof TileEntityRadarStation)
		{
			return new GLeiDaTai(((TileEntityRadarStation) tileEntity));
		}
		else if (tileEntity instanceof TileEntityEmpTower)
		{
			return new GDianCiQi((TileEntityEmpTower) tileEntity);
		}
		else if (tileEntity instanceof TFaSheDi)
		{
			return new GFaSheDi(entityPlayer.inventory, (TFaSheDi) tileEntity);
		}
		else if (tileEntity instanceof TileEntityMissileCoordinator)
		{
			return new GYinDaoQi(entityPlayer.inventory, (TileEntityMissileCoordinator) tileEntity);
		}

		return null;
	}

	@Override
	public boolean isGaoQing()
	{
		return Minecraft.getMinecraft().gameSettings.fancyGraphics;
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

		if (name == "smoke")
		{
			fx = new FXYan(world, position, red, green, blue, scale, distance);
		}
		else if (name == "missile_smoke")
		{
			fx = (new FXYan(world, position, red, green, blue, scale, distance)).setAge(100);
		}
		else if (name == "portal")
		{
			fx = new FXEnderPortalPartical(world, position, red, green, blue, scale, distance);
		}
		else if (name == "antimatter")
		{
			fx = new FXAntimatterPartical(world, position, red, green, blue, scale, distance);
		}
		else if (name == "digging")
		{
			fx = new EntityDiggingFX(world, position.x, position.y, position.z, motionX, motionY, motionZ, Block.blocksList[(int) red], 0, (int) green);
			fx.multipleParticleScaleBy(blue);
		}
		else if (name == "shockwave")
		{
			fx = new FXZhenBuo(world, position, red, green, blue, scale, distance);
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
	public IUpdatePlayerListBox getDaoDanShengYin(EDaoDan eDaoDan)
	{
		return new ShengYinDaoDan(Minecraft.getMinecraft().sndManager, eDaoDan, Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public List<Entity> getEntityFXs()
	{
		if (!this.disableReflectionFX)
		{
			try
			{
				EffectRenderer renderer = Minecraft.getMinecraft().effectRenderer;
				List[] fxLayers = (List[]) ReflectionHelper.getPrivateValue(EffectRenderer.class, renderer, 2);

				return fxLayers[0];
			}
			catch (Exception e)
			{
				ICBMCore.LOGGER.severe("Failed to use relfection on entity effects.");
				e.printStackTrace();
				this.disableReflectionFX = true;
			}
		}
		return null;
	}

	// TODO: Work on this!
	// @ForgeSubscribe
	public void renderingLivingEvent(Pre evt)
	{
		if (evt.entity instanceof EntityLivingBase)
		{
			if (evt.entity.getActivePotionEffect(PDongShang.INSTANCE) != null)
			{
				try
				{
					ModelBase modelBase = (ModelBase) ReflectionHelper.getPrivateValue(RendererLivingEntity.class, evt.renderer, 2);

					if (modelBase != null)
					{
						if (evt.entity.isInvisible())
						{
							GL11.glDepthMask(false);
						}
						else
						{
							GL11.glDepthMask(true);
						}

						float f1 = evt.entity.ticksExisted;
						// this.bindTexture(evt.renderer.func_110829_a);
						CalclaviaRenderHelper.setTerrainTexture();
						GL11.glMatrixMode(GL11.GL_TEXTURE);
						GL11.glLoadIdentity();
						float f2 = f1 * 0.01F;
						float f3 = f1 * 0.01F;
						GL11.glTranslatef(f2, f3, 0.0F);
						GL11.glScalef(2, 2, 2);
						evt.renderer.setRenderPassModel(modelBase);
						GL11.glMatrixMode(GL11.GL_MODELVIEW);
						GL11.glEnable(GL11.GL_BLEND);
						float f4 = 0.5F;
						GL11.glColor4f(f4, f4, f4, 1.0F);
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
						modelBase.render(evt.entity, (float) evt.entity.posX, (float) evt.entity.posY, (float) evt.entity.posZ, evt.entity.rotationPitch, evt.entity.rotationYaw, 0.0625F);
						GL11.glMatrixMode(GL11.GL_TEXTURE);
						GL11.glLoadIdentity();
						GL11.glMatrixMode(GL11.GL_MODELVIEW);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glDisable(GL11.GL_BLEND);
					}
				}
				catch (Exception e)
				{
					ICBMCore.LOGGER.severe("Failed to render entity layer object");
					e.printStackTrace();
				}
			}
		}
	}
}
