package com.builtbroken.icbm.content.launcher;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ILauncher;
import com.builtbroken.icbm.api.IMissileItem;
import com.builtbroken.icbm.content.crafting.missile.casing.Missile;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileSmall;
import com.builtbroken.icbm.content.crafting.missile.casing.ModelRefs;
import com.builtbroken.icbm.content.display.TileMissileContainer;
import com.builtbroken.icbm.content.missile.EntityMissile;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.render.model.loader.EngineModelLoader;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

/**
 * Mainly a test launcher for devs this tile also can be used by players as a small portable launcher
 * Created by robert on 1/18/2015.
 */
public class TileSmallLauncher extends TileMissileContainer implements ILauncher, ISimpleItemRenderer, IGuiTile, IPacketIDReceiver, IPostInit
{
    protected Pos target = new Pos(0, -1, 0);

    @SideOnly(Side.CLIENT)
    private static IModelCustom launcher_model;

    public TileSmallLauncher()
    {
        super("smallLauncher", Material.anvil);
        this.addInventoryModule(1);
        this.bounds = new Cube(0, 0, 0, 1, .5, 1);
        this.isOpaque = false;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;

    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockSmallLauncher), "IIB", "IIB", "CBC", 'I', Items.iron_ingot, 'B', Blocks.iron_block, 'C', UniversalRecipe.CIRCUIT_T1.get()));
    }

    public void setTarget(Pos target)
    {
        this.target = target;
        if (isClient())
        {
            Engine.instance.packetHandler.sendToServer(new PacketTile(this, 1, target));
        }
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.flint_and_steel)
        {
            if (target != null && target.y() > -1)
            {
                double distance = target.distance(new Pos(this));
                if (distance <= 200 && distance >= 20)
                {
                    fireMissile();
                    //TODO add launch logging to console
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocalName(getInventoryName() + ".invaliddistance")));
                }
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocalName(getInventoryName() + ".invalidtarget")));
            }
            return true;
        }
        else if (player.getHeldItem() == null)
        {
            openGui(player, ICBM.INSTANCE);
            return true;
        }
        return super.onPlayerRightClick(player, side, hit);
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            if (ticks % 20 == 0)
            {
                if (world().isBlockIndirectlyGettingPowered(xi(), yi(), zi()))
                {
                    fireMissile();
                }
            }
        }
    }

    public void fireMissile()
    {
        Missile missile = getMissile();
        if (missile != null)
        {
            if (isServer())
            {
                //Create and setup missile
                EntityMissile entity = new EntityMissile(world());
                entity.setMissile(missile);
                entity.setPositionAndRotation(x() + 0.5, y() + 3, z() + 0.5, 0, 0);
                entity.setVelocity(0, 2, 0);

                //Set target data
                entity.setTarget(target, true);
                entity.sourceOfProjectile = new Pos(this);

                //Spawn and start moving
                world().spawnEntityInWorld(entity);
                entity.setIntoMotion();

                //Empty inventory slot
                this.setInventorySlotContents(0, null);
                sendDescPacket();
            }
            else
            {
                //TODO add some effects
                for (int l = 0; l < 20; ++l)
                {
                    double f = x() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
                    double f1 = y() + 0.1 + 0.5 * (world().rand.nextFloat() - world().rand.nextFloat());
                    double f2 = z() + 0.5 + 0.3 * (world().rand.nextFloat() - world().rand.nextFloat());
                    world().spawnParticle("largesmoke", f, f1, f2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public boolean canAcceptMissile(Missile missile)
    {
        return missile != null && missile.casing == MissileCasings.SMALL;
    }

    @Override
    public Tile newTile()
    {
        return new TileSmallLauncher();
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.gravel.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @Override
    public String getInventoryName()
    {
        return "tile.icbm:smallLauncher.container";
    }

    @Override
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        //Import model if missing
        if (launcher_model == null)
        {
            launcher_model = EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "small_launcher.tcn"));
        }

        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        GL11.glScaled(.8f, .8f, .8f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelRefs.GREY_FAKE_TEXTURE);
        launcher_model.renderAllExcept("rail");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 2, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Import model if missing
        if (launcher_model == null)
        {
            launcher_model = EngineModelLoader.loadModel(new ResourceLocation(ICBM.DOMAIN, ICBM.MODEL_PREFIX + "small_launcher.tcn"));
        }

        //Render launcher
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelRefs.GREY_FAKE_TEXTURE);
        launcher_model.renderAll();
        GL11.glPopMatrix();

        //Render missile
        if (getMissile() != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f, pos.zf() + 0.5f);
            GL11.glScaled(.0015625f, .0015625f, .0015625f);
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelRefs.GREY_FAKE_TEXTURE);
            ModelRefs.SMALL_MISSILE_MODEL.renderAll();
            GL11.glPopMatrix();
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy(player, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSmallLauncher(this, player);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isServer())
        {
            if (id == 1)
            {
                this.target = new Pos(buf);
                return true;
            }
        }
        else
        {
            if (id == 0)
            {
                this.target = new Pos(buf);
                ItemStack stack = ByteBufUtils.readItemStack(buf);
                if (stack.getItem() instanceof IMissileItem)
                    this.setInventorySlotContents(0, stack);
                else
                    this.setInventorySlotContents(0, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, target, getStackInSlot(0) != null ? getStackInSlot(0) : new ItemStack(Blocks.stone));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("target"))
            this.target = new Pos(nbt.getCompoundTag("target"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (target != null)
            nbt.setTag("target", target.toNBT());
    }
}
