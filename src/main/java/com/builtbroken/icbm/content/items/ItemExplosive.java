package com.builtbroken.icbm.content.items;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.api.ICBM_API;
import com.builtbroken.icbm.client.ec.ECBiomeChange;
import com.builtbroken.icbm.content.blast.biome.ExBiomeChange;
import com.builtbroken.icbm.content.blast.entity.ExSpawn;
import com.builtbroken.icbm.content.blast.fragment.*;
import com.builtbroken.icbm.content.items.parts.ItemExplosiveParts;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.client.ExplosiveRegistryClient;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.core.registry.implement.IRegistryInit;
import com.builtbroken.mc.framework.explosive.ExplosiveItemUtility;
import com.builtbroken.mc.framework.explosive.ExplosiveRegistry;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.prefab.items.ItemNBTExplosive;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Explosive item, mainly used for crafting warheads.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/27/2016.
 */
public class ItemExplosive extends ItemNBTExplosive implements IExplosiveItem, IPostInit, IRegistryInit
{
    //TODO for converting to JSON
    //  Move biome and fragment to own item, USE NBT TO move old items to new items to prevent item loss
    //  Add loader for sub types to Item Processor
    //  Add loader for explosive item data
    //  Add explosive item registry JSON processor


    public ItemExplosive()
    {
        this.setMaxStackSize(10);
        this.setUnlocalizedName(ICBM.PREFIX + "explosiveItem");
        this.setTextureName(ICBM.PREFIX + "explosiveItem");
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        //How to use info
        String translation = Colors.DARK_GREY.code + LanguageUtility.getLocal(getUnlocalizedName() + ".info");
        list.add(translation);

        if (stack.getItemDamage() != ExplosiveItems.FRAGMENT.ordinal())
        {
            //What is "it" info
            final String translationKey = getUnlocalizedName(stack) + ".info";
            translation = LanguageUtility.getLocal(translationKey);
            if (!translation.isEmpty() && !translation.equals(translationKey))
            {
                if (translation.contains(","))
                {
                    String[] split = translation.split(",");
                    for (String s : split)
                    {
                        list.add(s.trim());
                    }
                }
                else
                {
                    list.add(translation);
                }
            }
        }

        //Custom info for biome change
        if (stack.getItemDamage() == ExplosiveItems.BIOME_CHANGE.ordinal())
        {
            int id = ExBiomeChange.getBiomeID(stack);
            if (id >= 0)
            {
                list.add(Colors.RED.code + LanguageUtility.getLocal(getUnlocalizedName() + ".WIP.info"));
                list.add(Colors.RED.code + LanguageUtility.getLocal(getUnlocalizedName() + ".warning.WIP.info"));

                translation = LanguageUtility.getLocal(getUnlocalizedName(stack) + ".name.info");
                translation = translation.replace("%1", "" + (BiomeGenBase.getBiome(id) == null ? Colors.RED.code + "Error" : BiomeGenBase.getBiome(id).biomeName));
                list.add(translation);
            }
        }

        if (stack.getItemDamage() == ExplosiveItems.ENTITY_SPAWN.ordinal())
        {
            int id = ExSpawn.getEntityID(stack);
            if (id != -1)
            {
                list.add("Entity: " + EntityList.getStringFromID(id));
            }
        }

        //Unique info for fragments
        if (stack.getItemDamage() == ExplosiveItems.FRAGMENT.ordinal())
        {
            list.add(Colors.RED.code + LanguageUtility.getLocal(getUnlocalizedName() + ".warning.breaksBlocks.info"));
            final FragBlastType type = ExFragment.getFragmentType(stack);
            if (type == FragBlastType.ARROW)
            {
                translation = LanguageUtility.getLocal(getUnlocalizedName(stack) + ".info");
                translation = translation.replace("%1", "" + BlastArrows.ARROWS);
                list.add(translation);
            }
            else if (type == FragBlastType.BLAZE)
            {
                translation = LanguageUtility.getLocal(getUnlocalizedName(stack) + ".info");
                translation = translation.replace("%1", "" + BlastArrows.ARROWS);
                list.add(translation);
            }
            else
            {
                translation = LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.info");
                list.add(translation);
                if (type.blockMaterial != null)
                {
                    translation = LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.damage.info");
                    translation = translation.replace("%1", "" + (type.blockMaterial.blockHardness * BlastFragments.START_VELOCITY));
                    list.add(translation);
                }
                int count = stack.stackSize * (int) ExplosiveItems.FRAGMENT.sizePerUnit;
                translation = LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.frags.info");
                translation = translation.replace("%1", "" + count);
                list.add(translation);

                if (Engine.isShiftHeld())
                {
                    list.add(LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.damage.equation.info"));

                    translation = LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.velocity.equation.info");
                    translation = translation.replace("%1", "" + BlastFragments.START_VELOCITY);
                    list.add(translation);

                    translation = LanguageUtility.getLocal(getUnlocalizedName() + ".fragment.frags.equation.info");
                    translation = translation.replace("%1", "" + ExplosiveItems.FRAGMENT.sizePerUnit);
                    list.add(translation);
                }
                else
                {
                    translation = LanguageUtility.getLocal("item.tooltip.description.more");
                    translation = translation.replace("%key", Colors.AQUA.code + "SHIFT");
                    list.add(translation);
                }
            }
        }
        else
        {
            if (Engine.isShiftHeld())
            {
                //Custom info for biome change
                if (stack.getItemDamage() == ExplosiveItems.BIOME_CHANGE.ordinal())
                {
                    int id = ExBiomeChange.getBiomeID(stack);
                    if (id >= 0)
                    {
                        translation = LanguageUtility.getLocal(getUnlocalizedName(stack) + ".id.info");
                        translation = translation.replace("%1", "" + id);
                        list.add(translation);
                    }
                }
                IExplosiveHandler handler = getExplosive(stack);

                translation = LanguageUtility.getLocal(getUnlocalizedName() + ".explosive.name.info");
                translation = translation.replace("%1", "" + LanguageUtility.getLocalName(handler.getTranslationKey()));
                list.add(translation);

                translation = LanguageUtility.getLocal(getUnlocalizedName() + ".explosive.size.info");
                translation = translation.replace("%1", "" + ((int) ((ExplosiveItems.values()[stack.getItemDamage()].sizePerUnit * handler.getYieldModifier()) * 100) / 100));
                list.add(translation);
            }
            else
            {
                translation = LanguageUtility.getLocal("item.tooltip.description.more");
                translation = translation.replace("%key", Colors.AQUA.code + "SHIFT");
                list.add(translation);
            }
        }
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
        for (ExplosiveItems item : ExplosiveItems.values())
        {
            if (item != ExplosiveItems.FRAGMENT && item != ExplosiveItems.BIOME_CHANGE)
            {
                item.icon = reg.registerIcon(ICBM.PREFIX + "explosiveItem." + item.ex_name);
            }
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
        else if (stack.getItemDamage() >= 1 && stack.getItemDamage() < ExplosiveItems.values().length && ExplosiveItems.values()[stack.getItemDamage()].icon != null)
        {
            return ExplosiveItems.values()[stack.getItemDamage()].icon;
        }
        else if (getExplosive(stack) instanceof IFragmentExplosiveHandler)
        {
            return ((IFragmentExplosiveHandler) getExplosive(stack)).getFragmentIcon(stack, pass);
        }
        else if (getExplosive(stack) instanceof ECBiomeChange)
        {
            return ((ECBiomeChange) getExplosive(stack)).getIcon(stack, pass);
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
        else if (metadata == ExplosiveItems.BIOME_CHANGE.ordinal())
        {
            return 4;
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
        ItemStack fragStack = ExplosiveItems.FRAGMENT.newItem();
        ExFragment.setFragmentType(fragStack, FragBlastType.ARROW);
        GameRegistry.addRecipe(new ShapedOreRecipe(fragStack, "A A", " G ", "A A", 'A', arrowBundle, 'G', explosiveCharge));

        fragStack = ExplosiveItems.FRAGMENT.newItem();
        ExFragment.setFragmentType(fragStack, FragBlastType.COBBLESTONE);
        GameRegistry.addRecipe(new ShapedOreRecipe(fragStack, "ccc", "cGc", "ccc", 'c', OreNames.COBBLESTONE, 'G', explosiveCharge));

        fragStack = ExplosiveItems.FRAGMENT.newItem();
        ExFragment.setFragmentType(fragStack, FragBlastType.WOOD);
        GameRegistry.addRecipe(new ShapedOreRecipe(fragStack, "ccc", "cGc", "ccc", 'c', OreNames.WOOD, 'G', explosiveCharge));

        fragStack = ExplosiveItems.FRAGMENT.newItem();
        ExFragment.setFragmentType(fragStack, FragBlastType.BLAZE);
        GameRegistry.addRecipe(new ShapedOreRecipe(fragStack, "ccc", "cGc", "ccc", 'c', Items.blaze_powder, 'G', explosiveCharge));

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

        //Ore Puller
        newRecipe(ExplosiveItems.ORE_PULLER, "TBT", "BBB", "TBT", 'B', Blocks.coal_ore, 'T', Items.ender_pearl, 'M', magicCharge);

        //Emp
        if (OreDictionary.doesOreNameExist("battery"))
        {
            newRecipe(ExplosiveItems.EMP, "WRW", "RBR", "WRW", 'B', "battery", 'W', OreNames.WIRE_COPPER, 'R', OreNames.PLATE_GOLD); //TODO replace wires with large coils, TODO replace battery with capacitor, TODO make costly to balance size
        }
        else
        {
            newRecipe(ExplosiveItems.EMP, "WRW", "RCR", "WRW", 'C', Items.diamond, 'W', OreNames.WIRE_COPPER, 'R', Items.redstone);
        }
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
        for (FragBlastType frag : FragBlastType.values())
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

        Iterator iterator = EntityList.entityEggs.values().iterator();

        while (iterator.hasNext())
        {
            EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo) iterator.next();
            ItemStack stack = ExplosiveItems.ENTITY_SPAWN.newItem();
            ExSpawn.setEntityID(stack, entityegginfo.spawnedID);
            ExplosiveRegistry.registerExplosiveItem(stack);
        }
    }

    @Override
    public void onClientRegistered()
    {
        //TODO add renderer for some of the explosives
        //For example arrow bundle for fragment

        ExplosiveRegistryClient.registerIcon(ExplosiveItems.ORE_PULLER.newItem(), ICBM.PREFIX + "ex.icon.ore");
        ExplosiveRegistryClient.registerIcon(ExplosiveItems.MIDAS_ORE.newItem(), ICBM.PREFIX + "ex.icon.ore.midas");
        ExplosiveRegistryClient.registerIcon(ExplosiveItems.FLASH.newItem(), ICBM.PREFIX + "ex.icon.flash");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (int i = 1; i < ExplosiveItems.values().length; i++)
        {
            if (i == ExplosiveItems.FRAGMENT.ordinal())
            {
                for (FragBlastType frag : FragBlastType.values())
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
                    if (base != null && base.biomeID >= 0 && base.biomeID < BiomeGenBase.getBiomeGenArray().length)
                    {
                        ItemStack stack = ExplosiveItems.values()[i].newItem();
                        getAdditionalExplosiveData(stack).setInteger("biomeID", base.biomeID);
                        list.add(stack);
                    }
                }
            }
            else if (i == ExplosiveItems.ENTITY_SPAWN.ordinal())
            {
                Iterator iterator = EntityList.entityEggs.values().iterator();

                while (iterator.hasNext())
                {
                    EntityList.EntityEggInfo entityegginfo = (EntityList.EntityEggInfo) iterator.next();
                    if (entityegginfo != null)
                    {
                        ItemStack stack = new ItemStack(item, 1, ExplosiveItems.ENTITY_SPAWN.ordinal());
                        ExSpawn.setEntityID(stack, entityegginfo.spawnedID);
                        if (EntityList.getStringFromID(ExSpawn.getEntityID(stack)) != null)
                        {
                            list.add(stack);
                        }
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
        ORE_PULLER("OrePuller", 20),
        SLIME_RAIN("SlimeRain", 5),
        EMP("Emp", 1),
        GRAVITY("Gravity", 5),
        MICROWAVE("Microwave", 5),
        ENTITY_SPAWN("EntitySpawn", 1),
        NUKE("Nuke", 1),
        FLASH("Flash", 10),
        RADIATION("Radiation", 10),
        MIDAS_ORE("MidasOre", 10);

        //TODO implement tool tips to hint at usage

        public final String ex_name;
        public final double sizePerUnit;
        public IIcon icon;

        private static HashMap<IExplosiveHandler, ExplosiveItems> cache;
        private HashMap<Integer, Double> stackSizeToExplosiveSize = new HashMap();

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
            if (this == ENTITY_SPAWN || this == CAKE || this == REGEN)
            {
                return stackSize;
            }
            if (stackSize == 1)
            {
                return sizePerUnit;
            }
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
            return new ItemStack(ICBM_API.itemExplosive, 1, ordinal());
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
