package com.builtbroken.icbm.content.warhead;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.Assets;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.casing.MissileCasings;
import com.builtbroken.icbm.content.crafting.missile.warhead.Warhead;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadCasings;
import com.builtbroken.icbm.content.crafting.missile.warhead.WarheadStandard;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.tile.IRemovable;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.content.tool.ItemSheetMetalTools;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.WorldChangeHelper;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.recipe.item.sheetmetal.RecipeSheetMetal;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * Block version of the warhead placed at the end of a missile
 *
 * @author Darkguardsman
 */
public class TileWarhead extends Tile implements IExplosive, IRemovable.ISneakPickup, IPostInit
{
    public boolean exploding = false;

    private Warhead warhead;

    public TileWarhead()
    {
        super("warhead", Material.iron);
        this.hardness = 100;
        this.renderNormalBlock = false;
        this.renderType = -1;
        this.renderTileEntity = true;
        this.isOpaque = false;
        this.itemBlock = ItemBlockWarhead.class;
        this.bounds = new Cube(0.2, 0, 0.2, 0.8, 0.5, 0.8);
    }

    @Override
    public void onPostInit()
    {
        //Small warhead recipes
        ItemStack micro_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, null).toStack();
        ItemStack small_warhead_empty = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_SMALL, null).toStack();

        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(micro_warhead_empty, " p ", "tch", " r ", 'p', Blocks.stone_pressure_plate, 'c', ItemSheetMetal.SheetMetal.CONE_MICRO.stack(), 'r', Items.redstone, 't', ItemSheetMetal.SheetMetal.THIRD.stack(), 'h', ItemSheetMetalTools.getHammer()));
            GameRegistry.addRecipe(new RecipeSheetMetal(small_warhead_empty, " p ", "tch", " r ", 'p', Blocks.heavy_weighted_pressure_plate, 'c', ItemSheetMetal.SheetMetal.CONE_SMALL.stack(), 'r', Items.redstone, 't', ItemSheetMetal.SheetMetal.HALF.stack(), 'h', ItemSheetMetalTools.getHammer()));
        }
        else
        {
            //Vanilla micro warhead recipes
            micro_warhead_empty.stackSize = 8;
            GameRegistry.addRecipe(new ShapedOreRecipe(micro_warhead_empty, " p ", " n ", "nrn", 'p', Blocks.stone_pressure_plate, 'n', Items.iron_ingot, 'r', Items.redstone));
            micro_warhead_empty.stackSize = 1;

            //Vanilla small warhead recipes
            GameRegistry.addRecipe(new ShapedOreRecipe(small_warhead_empty, " p ", " n ", "ncn", 'p', Blocks.heavy_weighted_pressure_plate, 'n', Items.iron_ingot, 'r', Items.redstone, 'c', UniversalRecipe.CIRCUIT_T1.get()));
        }

        //Register specialized recipe handlers
        RecipeSorter.register(ICBM.PREFIX + "warhead", WarheadRecipe.class, SHAPELESS, "after:minecraft:shaped");
        RecipeSorter.register(ICBM.PREFIX + "microMissile", MicroMissileRecipe.class, SHAPELESS, "after:minecraft:shaped");

        //TNT explosive recipe
        addMicroWarheadRecipe("TNT", Items.gunpowder, Items.gunpowder, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "TNT", Blocks.tnt, small_warhead_empty));

        //Fragment TODO add real recipe
        addMicroWarheadRecipe("ArrowFragment", Items.gunpowder, Items.arrow, Items.arrow, Items.arrow, Items.arrow, Items.arrow, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "ArrowFragment", Items.gunpowder, Items.arrow, Items.arrow, Items.arrow, Items.arrow, Items.arrow, Items.arrow, small_warhead_empty));

        //Exo TODO add real recipe
        addMicroWarheadRecipe("ExoThermic", Items.blaze_powder, Items.blaze_powder, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "ExoThermic", Items.blaze_powder, Items.blaze_powder, Items.blaze_powder, Items.blaze_powder, small_warhead_empty));

        //Exo TODO add real recipe
        addMicroWarheadRecipe("EndoThermic", Blocks.ice, Blocks.ice, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "EndoThermic", Blocks.ice, Blocks.ice, Blocks.ice, Blocks.ice, small_warhead_empty));

        //Antimatter TODO add real recipe
        addMicroWarheadRecipe("Antimatter", Items.ender_eye, Items.ender_eye, Items.nether_star, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "Antimatter", Items.ender_eye, Items.ender_eye, Items.nether_star, Items.nether_star, small_warhead_empty));

        //Fire Bomb TODO add real recipe
        addMicroWarheadRecipe("FireBomb", Items.gunpowder, Items.coal, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "FireBomb", Items.gunpowder, Items.gunpowder, Items.coal, Items.coal, small_warhead_empty));

        //Flash Fire TODO add real recipe
        addMicroWarheadRecipe("FlashFire", Items.fire_charge, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "FlashFire", Items.fire_charge, Items.fire_charge, small_warhead_empty));

        //Flash Fire TODO add real recipe
        addMicroWarheadRecipe("TorchEater", Items.fermented_spider_eye, Items.ender_eye, micro_warhead_empty);
        GameRegistry.addRecipe(new WarheadRecipe(WarheadCasings.EXPLOSIVE_SMALL, "TorchEater", Items.fermented_spider_eye, Items.ender_eye, Items.fermented_spider_eye, small_warhead_empty));
    }

    /**
     * Uses to generate micro warhead and missile recipes at the same time
     *
     * @param exe   - explosive handler to use
     * @param parts - parts for the recipe
     */
    private static void addMicroWarheadRecipe(String exe, Object... parts)
    {
        //Create objects for use in recipe
        IExplosiveHandler ex = ExplosiveRegistry.get(exe);
        ItemStack stack = MissileModuleBuilder.INSTANCE.buildWarhead(WarheadCasings.EXPLOSIVE_MICRO, ex).toStack();

        //Add recipe for micro warhead
        GameRegistry.addRecipe(new WarheadRecipe(stack, parts));

        //Create recipe to add micro warhead to micro missile
        GameRegistry.addRecipe(new MicroMissileRecipe(ex, MissileModuleBuilder.INSTANCE.buildMissile(MissileCasings.MICRO, null).toStack(), stack));
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
                Location position = toLocation();
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
            {
                explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
            }
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
            Location position = toLocation();
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
        {
            explode(new TriggerCause.TriggerCauseRedstone(ForgeDirection.UNKNOWN, powerMax));
        }
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
            {
                world().setBlockToAir(xi(), yi(), zi());
            }
            else
            {
                exploding = false;
            }
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
        {
            nbt.setTag("itemWarhead", getWarhead().toStack().writeToNBT(new NBTTagCompound()));
        }
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
            {
                list.add(MissileModuleBuilder.INSTANCE.buildWarhead(size, null).toStack());
            }
        }

        for (IExplosiveHandler handler : ExplosiveRegistry.getExplosives())
        {
            for (WarheadCasings size : WarheadCasings.values())
            {
                if (size.enabled)
                {
                    list.add(MissileModuleBuilder.INSTANCE.buildWarhead(size, handler).toStack());
                }
            }
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
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(Assets.CLASSIC_MISSILE_TEXTURE);
        Assets.CLASSIC_MISSILE_MODEL.renderOnly("WARHEAD 1", "WARHEAD 2", "WARHEAD 3", "WARHEAD 4");
        GL11.glPopMatrix();
    }

    /**
     * @SideOnly(Side.CLIENT) public void renderInventoryItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data)
     * {
     * //Translate and rotate
     * if (type == IItemRenderer.ItemRenderType.EQUIPPED)
     * {
     * GL11.glTranslatef(1f, 0.3f, 0.5f);
     * }
     * else if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
     * {
     * GL11.glTranslatef(1.15f, 1f, 0.5f);
     * }
     * <p/>
     * //Scale
     * if (type == IItemRenderer.ItemRenderType.ENTITY)
     * {
     * GL11.glScalef(0.5f, 0.5f, 0.5f);
     * }
     * <p/>
     * renderDynamic(new Pos(), 0, 0);
     * }
     */

    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.iron_block.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (WarheadCasings casing : WarheadCasings.values())
        {
            casing.icon = reg.registerIcon(ICBM.PREFIX + "warhead." + casing.name().replace("EXPLOSIVE_", "").toLowerCase());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return WarheadCasings.get(meta).icon;
    }

    public Warhead getWarhead()
    {
        if (warhead == null)
        {
            warhead = getNewWarhead();
            if (warhead == null)
            {
                warhead = new WarheadStandard(toItemStack());
            }
        }
        return warhead;
    }

    public Warhead getNewWarhead()
    {
        Warhead warhead = null;
        try
        {
            warhead = WarheadCasings.get(getMetadata()).warhead_clazz.getConstructor(ItemStack.class).newInstance(new ItemStack(this.getTileBlock(), 1, getMetadata()));
        }
        catch (InvocationTargetException e)
        {
            ICBM.INSTANCE.logger().error("[TileWarhead]Failed invoke warhead constructor for class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
            {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e)
        {
            ICBM.INSTANCE.logger().error("[TileWarhead]Failed to find ItemStack constructor for warhead class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
            {
                e.printStackTrace();
            }
        }
        catch (InstantiationException e)
        {
            ICBM.INSTANCE.logger().error("[TileWarhead]Failed to create new warhead instance for warhead class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
            {
                e.printStackTrace();
            }
        }
        catch (IllegalAccessException e)
        {
            ICBM.INSTANCE.logger().error("[TileWarhead]Something prevented us from making a new instance of class " + WarheadCasings.get(getMetadata()).warhead_clazz);
            if (Engine.runningAsDev)
            {
                e.printStackTrace();
            }
        }

        return warhead;
    }


    public void setWarhead(Warhead warhead)
    {
        this.warhead = warhead;
    }
}
