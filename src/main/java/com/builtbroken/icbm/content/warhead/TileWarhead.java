package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadStandard;
import com.builtbroken.icbm.content.missile.RenderMissile;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.ISimpleItemRenderer;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Block version of the warhead placed at the end of a missile
 *
 * @author Darkguardsman
 */
public class TileWarhead extends Tile implements IExplosive, IRemovable.ISneakPickup, ISimpleItemRenderer
{
    public boolean exploding = false;

    private Warhead warhead;

    public TileWarhead()
    {
        super("warhead", Material.iron);
        this.hardness = 100;
        this.renderNormalBlock = false;
        this.renderTileEntity = true;
        this.isOpaque = false;
        this.itemBlock = ItemBlockWarhead.class;
        this.bounds = new Cube(0.2, 0, 0.2, 0.8, 0.5, 0.8);
    }

    @Override
    public void onCollide(Entity entity)
    {
        if (entity != null && entity.isBurning())
        {
            explode(new TriggerCause.TriggerCauseEntity(entity));
        }
    }

    @Override
    public Tile newTile()
    {
        return new TileWarhead();
    }

    @Override
    public boolean onPlayerActivated(EntityPlayer entityPlayer, int side, Pos hit)
    {
        if (entityPlayer.getCurrentEquippedItem() != null)
        {
            if (entityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
            {
                explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(side)));
                return true;
            }
            else if (WrenchUtility.isUsableWrench(entityPlayer, entityPlayer.getCurrentEquippedItem(), xi(), yi(), zi()))
            {
                byte change = 3;

                // Reorient the block
                switch (getBlockMetadata())
                {
                    case 0:
                        change = 2;
                        break;
                    case 2:
                        change = 5;
                        break;
                    case 5:
                        change = 3;
                        break;
                    case 3:
                        change = 4;
                        break;
                    case 4:
                        change = 1;
                        break;
                    case 1:
                        change = 0;
                        break;
                }

                setMeta(ForgeDirection.getOrientation(change).ordinal());

                world().notifyBlockChange(xi(), yi(), zi(), this.getBlockType());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPlaced(EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onPlaced(entityLiving, itemStack);
        if (!world().isRemote && ExplosiveItemUtility.getExplosive(itemStack) != null)
        {
            //Set explosive id
            setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(itemStack));

            //Set rotation for direction based explosives
            setMeta(determineOrientation(entityLiving));

            int power_side = -1;
            int powerMax = 0;
            // If anything can set it on fire blow up
            for (byte i = 0; i < 6; i++)
            {
                Location position = toVectorWorld();
                position.add(ForgeDirection.getOrientation(i));

                Block b = position.getBlock();

                if (world().getIndirectPowerLevelTo(xi(), yi(), zi(), i) > powerMax)
                {
                    powerMax = world().getIndirectPowerLevelTo(xi(), yi(), zi(), i);
                    power_side = i;
                }

                if (b == Blocks.fire || b == Blocks.lava || b == Blocks.flowing_lava)
                {
                    explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(i)));
                    return;
                }
            }

            if (power_side != -1)
                explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
        }
    }

    @Override
    public void onWorldJoin()
    {
        super.onWorldJoin();
        world().markBlockForUpdate(xi(), yi(), zi());
    }

    @Override
    public void onNeighborChanged(Block block)
    {
        super.onNeighborChanged(block);
        int power_side = -1;
        int powerMax = 0;
        // If anything can set it on fire blow up
        for (byte i = 0; i < 6; i++)
        {
            Location position = toVectorWorld();
            position.add(ForgeDirection.getOrientation(i));

            Block b = position.getBlock();

            if (world().getIndirectPowerLevelTo(xi(), yi(), zi(), i) > powerMax)
            {
                powerMax = world().getIndirectPowerLevelTo(xi(), yi(), zi(), i);
                power_side = i;
            }

            if (b == Blocks.fire || b == Blocks.lava || b == Blocks.flowing_lava)
            {
                explode(new TriggerCause.TriggerCauseFire(ForgeDirection.getOrientation(i)));
                return;
            }
        }

        if (power_side != -1)
            explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
    }

    /*
     * Called to detonate the TNT. Args: world, x, y, z, metaData, CauseOfExplosion (0, intentional,
     * 1, exploded, 2 burned)
     */
    public void explode(TriggerCause triggerCause)
    {
        if (!exploding)
        {
            exploding = true;
            if (getWarhead().trigger(triggerCause, world(), x(), y(), z()) == WorldChangeHelper.ChangeResult.COMPLETED)
                world().setBlockToAir(xi(), yi(), zi());
            else
                exploding = false;
        }
    }

    @Override
    public void onDestroyedByExplosion(Explosion explosion)
    {
        explode(new TriggerCause.TriggerCauseExplosion(explosion));
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        setWarhead(null);
        if (nbt.hasKey("itemWarhead"))
        {
            setWarhead(MissileModuleBuilder.INSTANCE.buildWarhead(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("itemWarhead"))));
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (getWarhead() != null && getWarhead().toStack() != null)
            nbt.setTag("itemWarhead", getWarhead().toStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, getSaveData());
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        return getWarhead().ex;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List list)
    {
        for (WarheadCasings size : WarheadCasings.values())
        {
            if (size.enabled)
                list.add(MissileModuleBuilder.INSTANCE.buildWarhead(size, null).toStack());
        }
    }

    @Override
    public ItemStack toItemStack()
    {
        ItemStack stack = new ItemStack(this.getBlockType());
        if (getWarhead() != null)
        {
            getWarhead().save(stack);
        }
        else
        {
            stack.setItemDamage(1);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getRemovedItems(EntityPlayer entity)
    {
        List<ItemStack> list = new ArrayList();
        list.add(toItemStack());
        return list;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderDynamic(Pos position, float frame, int pass)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(position.xf() + 0.5f, position.yf() - 2.5f, position.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderMissile.SMALL_TEXTURE);
        RenderMissile.SMALL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
    {
        //Translate and rotate
        if (type == IItemRenderer.ItemRenderType.EQUIPPED)
        {
            GL11.glTranslatef(1f, 0.3f, 0.5f);
        }
        else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(1.15f, 1f, 0.5f);
        }

        //Scale
        if (type == IItemRenderer.ItemRenderType.ENTITY)
        {
            GL11.glScalef(0.5f, 0.5f, 0.5f);
        }

        renderDynamic(new Pos(), 0, 0);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {

    }

    public Warhead getWarhead()
    {
        if (warhead == null)
        {
            warhead = getNewWarhead();
            if (warhead == null)
                warhead = new WarheadStandard(toItemStack());
        }
        return warhead;
    }

    public Warhead getNewWarhead()
    {
        Warhead warhead = null;
        try
        {
            warhead = WarheadCasings.get(getMetadata()).warhead_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(this.getTileBlock(), 1, getMetadata()));
        } catch (InvocationTargetException e)
        {
            ICBM.LOGGER.error("[TileWarhead]Failed invoke warhead constructor for class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
                e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            ICBM.LOGGER.error("[TileWarhead]Failed to find ItemStack constructor for warhead class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
                e.printStackTrace();
        } catch (InstantiationException e)
        {
            ICBM.LOGGER.error("[TileWarhead]Failed to create new warhead instance for warhead class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
                e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            ICBM.LOGGER.error("[TileWarhead]Something prevented us from making a new instance of class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
                e.printStackTrace();
        }

        return warhead;
    }


    public void setWarhead(Warhead warhead)
    {
        this.warhead = warhead;
    }
}
