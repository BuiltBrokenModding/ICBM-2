package com.builtbroken.icbm.content.crafting.parts;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.blast.fragment.ExFragment;
import com.builtbroken.icbm.content.blast.fragment.Fragments;
import com.builtbroken.icbm.content.blast.fragment.IFragmentExplosiveHandler;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.core.registry.implement.IRegistryInit;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.world.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.explosive.ItemNBTExplosive;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.List;

/**
 * Explosive item, mainly used for crafting warheads.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/27/2016.
 */
public class ItemExplosive extends ItemNBTExplosive implements IExplosiveItem, IPostInit, IRegistryInit
{
    public ItemExplosive()
    {
        this.setMaxStackSize(10);
        this.setUnlocalizedName(ICBM.PREFIX + "explosiveItem");
        this.setTextureName(ICBM.PREFIX + "explosiveItem");
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() == ExplosiveItems.FRAGMENT.ordinal())
        {
            if (getExplosive(stack) instanceof IFragmentExplosiveHandler)
            {
                return ((IFragmentExplosiveHandler) ExplosiveItems.FRAGMENT.getExplosive()).getFragmentLocalization(stack);
            }
        }
        else if (stack.getItemDamage() >= 1 && stack.getItemDamage() < ExplosiveItems.values().length)
        {
            return super.getUnlocalizedName() + "." + ExplosiveItems.values()[stack.getItemDamage()].ex_name;
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.itemIcon = reg.registerIcon(this.getIconString());
        for (int i = 1; i < ExplosiveItems.values().length; i++)
        {
            ExplosiveItems.values()[i].icon = reg.registerIcon(ICBM.PREFIX + "explosiveItem." + ExplosiveItems.values()[i].ex_name);
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if (stack.getItemDamage() == ExplosiveItems.FRAGMENT.ordinal())
        {
            if (getExplosive(stack) instanceof IFragmentExplosiveHandler)
            {
                return ((IFragmentExplosiveHandler) ExplosiveItems.FRAGMENT.getExplosive()).getFragmentIcon(stack, pass);
            }
        }
        else if (stack.getItemDamage() >= 1 && stack.getItemDamage() < ExplosiveItems.values().length)
        {
            return ExplosiveItems.values()[stack.getItemDamage()].icon;
        }
        else if (getExplosive(stack) instanceof IFragmentExplosiveHandler)
        {
            return ((IFragmentExplosiveHandler) getExplosive(stack)).getFragmentIcon(stack, pass);
        }
        return itemIcon;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        if (metadata == ExplosiveItems.FRAGMENT.ordinal())
        {
            if (ExplosiveItems.FRAGMENT.getExplosive() instanceof IFragmentExplosiveHandler)
            {
                return ((IFragmentExplosiveHandler) ExplosiveItems.FRAGMENT.getExplosive()).getFragmentNumberOfPasses();
            }
        }
        return 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public boolean setExplosive(ItemStack stack, IExplosiveHandler ex, double size, NBTTagCompound nbt)
    {
        if (stack != null && size > 0)
        {
            if (stack.getTagCompound() == null)
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            if (ex != null)
            {
                boolean found = false;
                for (ExplosiveItems exItem : ExplosiveItems.values())
                {
                    IExplosiveHandler handler = exItem.getExplosive();
                    if (handler == ex)
                    {
                        stack.setItemDamage(exItem.ordinal());
                        found = true;
                    }
                }
                if (!found)
                {
                    ExplosiveItemUtility.setExplosive(stack, ex);
                }
            }
            else
            {
                ExplosiveItemUtility.setExplosive(stack, (IExplosiveHandler) null);
            }
            ExplosiveItemUtility.setSize(stack, size);
            stack.getTagCompound().setTag("exData", nbt);
            return true;
        }
        return false;
    }

    @Override
    public double getExplosiveSize(ItemStack stack)
    {
        if (stack.getItemDamage() >= 1 && stack.getItemDamage() < ExplosiveItems.values().length)
        {
            return ExplosiveItems.values()[stack.getItemDamage()].getSize(stack.stackSize);
        }
        return ExplosiveItemUtility.getSize(stack);
    }

    @Override
    public IExplosiveHandler getExplosive(ItemStack stack)
    {
        if (stack.getItemDamage() >= 1 && stack.getItemDamage() < ExplosiveItems.values().length)
        {
            return ExplosiveItems.values()[stack.getItemDamage()].getExplosive();
        }
        return super.getExplosive(stack);
    }

    @Override
    public void onPostInit()
    {
        final ItemStack explosiveCharge = ItemExplosiveParts.ExplosiveParts.GUNPOWDER_CHARGE.newItem();
        final ItemStack magicCharge = ItemExplosiveParts.ExplosiveParts.MAGIC_CHARGE.newItem();
        final ItemStack arrowBundle = ItemExplosiveParts.ExplosiveParts.ARROW_BUNDLE.newItem();

        //Fragment arrow explosive
        ItemStack arrowFrag = ExplosiveItems.FRAGMENT.newItem();
        arrowFrag.setTagCompound(ExFragment.setFragmentType(new NBTTagCompound(), Fragments.ARROW));
        GameRegistry.addRecipe(new ShapedOreRecipe(arrowFrag, "A A", " G ", "A A", 'A', arrowBundle, 'G', explosiveCharge));

        //Exothermic explosive TODO add tech based recipe
        newRecipe(ExplosiveItems.THERMIC_EXO, " B ", "BMB", " B ", 'B', Items.blaze_powder, 'M', magicCharge);

        //Endothermic explosive TODO add tech based recipe
        newRecipe(ExplosiveItems.THERMIC_ENDO, " B ", "BMB", " B ", 'B', Blocks.ice, 'M', magicCharge);

        //Antimatter explosive TODO add tech based recipe
        newRecipe(ExplosiveItems.ANTIMATTER, " B ", "EME", " B ", 'B', Items.nether_star, 'E', Items.ender_eye, 'M', magicCharge);

        //Fire Bomb
        newRecipe(ExplosiveItems.FIRE_BOMB, " B ", "EGE", " B ", 'B', Items.coal, 'E', Items.paper, 'G', explosiveCharge);

        //Flash fire
        newRecipe(ExplosiveItems.FIRE_FLASH, " B ", "BGB", " B ", 'B', Items.fire_charge, 'G', explosiveCharge);

        //Torch Eater
        newRecipe(ExplosiveItems.TORCH_EATER, "TBM", "BEB", "MBT", 'B', Items.fermented_spider_eye, 'E', Items.ender_eye, 'T', Blocks.torch, 'M', magicCharge);

        //Ender Blocks
        newRecipe(ExplosiveItems.ENDER_BLOCKS, "ZBM", "BEB", "MBZ", 'B', OreNames.INGOT_GOLD, 'E', Items.ender_eye, 'Z', Items.ender_pearl, 'M', magicCharge);

        //Anti Plant
        newRecipe(ExplosiveItems.ANTI_PLANT, "FTF", "BMB", "FTF", 'T', OreNames.SAPLING, 'F', Items.fermented_spider_eye, 'B', OreNames.LEAVES, 'M', magicCharge);

        //Plant
        newRecipe(ExplosiveItems.PLANT_LIFE, "FTF", "BMB", "FTF", 'T', OreNames.SAPLING, 'F', Items.bone, 'B', OreNames.LEAVES, 'M', magicCharge);

        //Cake
        newRecipe(ExplosiveItems.CAKE, "PPP", "PCP", "RRR", 'C', Items.cake, 'P', Blocks.glass_pane, 'R', UniversalRecipe.PRIMARY_PLATE.get());
    }

    private void newRecipe(ExplosiveItems item, Object... objects)
    {
        newRecipe(item.newItem(), objects);
    }

    private void newRecipe(ItemStack stack, Object... objects)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(stack, objects));
    }

    @Override
    public void onRegistered()
    {
        for (ExplosiveItems item : ExplosiveItems.values())
        {
            if (item.ex_name != null && item != ExplosiveItems.FRAGMENT && item != ExplosiveItems.BIOME_CHANGE)
            {
                ExplosiveRegistry.registerExplosiveItem(item.newItem());
            }
        }
        for (Fragments frag : Fragments.values())
        {
            ItemStack stack = ExplosiveItems.FRAGMENT.newItem();
            ExFragment.setFragmentType(stack, frag);
            ExplosiveRegistry.registerExplosiveItem(stack);
        }
        for (BiomeGenBase base : BiomeGenBase.getBiomeGenArray())
        {
            if (base != null && base.biomeID >= 0)
            {
                ItemStack stack = ExplosiveItems.BIOME_CHANGE.newItem();
                getAdditionalExplosiveData(stack).setByte("biomeID", (byte) base.biomeID);
                ExplosiveRegistry.registerExplosiveItem(stack);
            }
        }
    }

    @Override
    public void onClientRegistered()
    {
        //TODO add renderer for some of the explosives
        //For example arrow bundle for fragment

        ExplosiveRegistryClient.registerIcon(ExplosiveItems.ORE_PULLER.newItem(), ICBM.PREFIX + "ex.icon.ore");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (int i = 1; i < ExplosiveItems.values().length; i++)
        {
            if (i == ExplosiveItems.FRAGMENT.ordinal())
            {
                for (Fragments frag : Fragments.values())
                {
                    ItemStack stack = ExplosiveItems.values()[i].newItem();
                    ExFragment.setFragmentType(stack, frag);
                    list.add(stack);
                }
            }
            else if (i == ExplosiveItems.BIOME_CHANGE.ordinal())
            {
                for (BiomeGenBase base : BiomeGenBase.getBiomeGenArray())
                {
                    if (base != null && base.biomeID >= 0)
                    {
                        ItemStack stack = ExplosiveItems.values()[i].newItem();
                        getAdditionalExplosiveData(stack).setByte("biomeID", (byte) base.biomeID);
                        list.add(stack);
                    }
                }
            }
            else
            {
                list.add(ExplosiveItems.values()[i].newItem());
            }
        }
    }

    public enum ExplosiveItems
    {
        NBT(null, 1),
        FRAGMENT("Fragment", 5),
        THERMIC_EXO("ExoThermic", 3),
        THERMIC_ENDO("EndoThermic", 3),
        FIRE_BOMB("FireBomb", 1),
        FIRE_FLASH("FlashFire", 2),
        TORCH_EATER("TorchEater", 4),
        ENDER_BLOCKS("EnderBlocks", 2),
        ANTIMATTER("Antimatter", 5),
        ANTI_PLANT("AntiPlant", 4),
        REGEN("Regen", 1),
        REGEN_LOCAL("RegenLocal", 1),
        PLANT_LIFE("PlantLife", 4),
        CAKE("Cake", 1),
        BIOME_CHANGE("BiomeChange", 1),
        ORE_PULLER("OrePuller", 10);

        public final String ex_name;
        public final double sizePerUnit;
        public IIcon icon;

        private static HashMap<IExplosiveHandler, ExplosiveItems> cache;
        private static HashMap<Integer, Double> stackSizeToExplosiveSize = new HashMap();

        ExplosiveItems(String ex_name, double sizePerUnit)
        {
            this.ex_name = ex_name;
            this.sizePerUnit = sizePerUnit;
        }

        public IExplosiveHandler getExplosive()
        {
            return ExplosiveRegistry.get(ex_name);
        }

        /**
         * Get the explosive size for the stack size
         *
         * @param stackSize
         * @return
         */
        public double getSize(int stackSize)
        {
            if (!stackSizeToExplosiveSize.containsKey(stackSize))
            {
                stackSizeToExplosiveSize.put(stackSize, ExplosiveRegistry.getExplosiveSize(sizePerUnit, stackSize));
            }
            return stackSizeToExplosiveSize.get(stackSize);
        }

        /**
         * Creates a new item for the type
         *
         * @return new ItemStack
         */
        public ItemStack newItem()
        {
            return new ItemStack(ICBM.itemExplosive, 1, ordinal());
        }

        public static ItemStack get(IExplosiveHandler handler)
        {
            if (cache == null)
            {
                cache = new HashMap();
                for (ExplosiveItems item : values())
                {
                    cache.put(item.getExplosive(), item);
                }
            }
            if (cache.containsKey(handler))
            {
                return cache.get(handler).newItem();
            }
            return null;
        }
    }
}
