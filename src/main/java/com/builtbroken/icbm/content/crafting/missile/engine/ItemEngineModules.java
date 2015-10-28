package com.builtbroken.icbm.content.crafting.missile.engine;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.modules.IRocketEngine;
import com.builtbroken.icbm.content.crafting.missile.ItemAbstractModule;
import com.builtbroken.icbm.content.crafting.missile.MissileModuleBuilder;
import com.builtbroken.icbm.content.crafting.missile.engine.fluid.RocketEngineFluid;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineCoalPowered;
import com.builtbroken.icbm.content.crafting.missile.engine.solid.RocketEngineSolid;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

/**
 * Item for the modules to be contained in an inventory
 * Created by robert on 12/28/2014.
 */
public class ItemEngineModules extends ItemAbstractModule implements IPostInit
{
    public ItemEngineModules()
    {
        this.setHasSubtypes(true);
    }

    @Override
    public void onPostInit()
    {
        if (Engine.itemSheetMetal != null)
        {
            RocketEngineCoalPowered engine = new RocketEngineCoalPowered(new ItemStack(ICBM.itemEngineModules, 1, Engines.COAL_ENGINE.ordinal()));
            //Empty coal engine
            ItemStack engineStack = engine.toStack();
            GameRegistry.addRecipe(new ShapedOreRecipe(engineStack, " F ", "LRC", 'R', Items.redstone, 'F', Blocks.furnace, 'L', Items.flint_and_steel, 'C', ItemSheetMetal.SheetMetal.CONE_SMALL.stack()));

            //Coal fuel
            engine.getInventory().setInventorySlotContents(0, new ItemStack(Items.coal, 5));
            GameRegistry.addShapelessRecipe(engine.toStack(), engineStack, Items.coal, Items.coal, Items.coal, Items.coal, Items.coal);

            //Charcoal fuel
            engine.getInventory().setInventorySlotContents(0, new ItemStack(Items.coal, 5, 1));
            GameRegistry.addShapelessRecipe(engine.toStack(), engineStack, new ItemStack(Items.coal, 1, 1), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.coal, 1, 1), new ItemStack(Items.coal, 1, 1));
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        IModule module = getModule(stack);
        if (module instanceof IRocketEngine)
        {
            list.add("Speed: " + ((IRocketEngine) module).getSpeed(null));
            list.add("Range: " + ((IRocketEngine) module).getMaxDistance(null));
            if (module instanceof RocketEngineFluid)
            {
                list.add("Fuel: " + ((RocketEngineFluid) module).getFluidAmount() + "/" + ((RocketEngineFluid) module).getCapacity() + "mL");
            }
            else if (module instanceof RocketEngineSolid)
            {
                list.add("Fuel: " + ((RocketEngineSolid) module).getInventory().getStackInSlot(0));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (Engines engine : Engines.values())
        {
            RocketEngine e = MissileModuleBuilder.INSTANCE.buildEngine(engine.newModuleStack());
            e.initFuel();
            list.add(e.toStack());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean b)
    {
        //TODO maybe blow up engine if player is on fire?
        if (!world.isRemote)
        {
            //Remove creative module from non-creative mode player's
            if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
            {
                if (stack.getItemDamage() == Engines.CREATIVE_ENGINE.ordinal())
                {
                    ((EntityPlayer) entity).inventory.setInventorySlotContents(slot, null);
                    ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return Engines.get(meta) != null ? Engines.get(meta).icon : this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        for (Engines engine : Engines.values())
        {
            engine.icon = reg.registerIcon(ICBM.PREFIX + engine.name);
        }
    }
}
