package com.builtbroken.icbm.content.launcher.block;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.content.resources.items.ItemSheetMetal;
import com.builtbroken.mc.core.content.tool.ItemSheetMetalTools;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.WrenchUtility;
import com.builtbroken.mc.lib.helper.recipe.OreNames;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.recipe.item.sheetmetal.RecipeSheetMetal;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Mainly a placeholder block for creating launchers. In other words it can be used as a decoration as it has very little functionality.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/2015.
 */
public final class BlockLauncherPart extends Block implements IPostInit
{
    @SideOnly(Side.CLIENT)
    IIcon cpuTop;

    public BlockLauncherPart()
    {
        super(Material.iron);
        this.setBlockName(ICBM.PREFIX + "launcherPart");
        this.setResistance(20);
        this.setHardness(20);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xx, float yy, float zz)
    {

        int meta = world.getBlockMetadata(x, y, z);
        //Launcher CPU
        if (meta == 0 && WrenchUtility.isHoldingWrench(player) && player.isSneaking() && side != 0 && side != 1)
        {
            if (!world.isRemote)
            {
                //Detects all launcher frame blocks above it(up to max)
                int count = 0;
                Block block = world.getBlock(x, y + 1, z);
                while (count < 5 && block == ICBM.blockLauncherFrame)
                {
                    //Increase count
                    count++;
                    //Get next block above last
                    block = world.getBlock(x, y + count, z);
                    //Detects for clear path near launcher
                    Pos pos = new Pos(x, y + count, z).add(ForgeDirection.getOrientation(side));
                    if (!pos.isAirBlock(world))
                    {
                        player.addChatComponentMessage(new ChatComponentText("To prevent issues clear the blocks from the side of the tower that the missile will occupy."));
                        return true;
                    }
                }
                if (count == 5)
                {
                    //create standard launcher
                    new Pos(x, y, z).setBlock(world, ICBM.blockStandardLauncher, side);
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Standard launcher created"));
                }
                else if (count > 5)
                {
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Detected " + (count - 5) + " extra tower blocks. You need only 5 for standard launcher setup."));
                }
                else if (count < 5)
                {
                    //TODO add translation key
                    player.addChatComponentMessage(new ChatComponentText("Detected " + (5 - count) + " missing tower blocks. You need 5 for standard launcher setup."));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg)
    {
        this.blockIcon = reg.registerIcon(ICBM.PREFIX + "launcher.box");
        this.cpuTop = reg.registerIcon(ICBM.PREFIX + "launcher.box.top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        if (meta == 0)
        {
            if (side == 1)
            {
                return cpuTop;
            }
            return this.blockIcon;
        }
        return Blocks.hopper.getIcon(side, meta);
    }

    @Override
    public void onPostInit()
    {
        if (Engine.itemSheetMetal != null && Engine.itemSheetMetalTools != null)
        {
            GameRegistry.addRecipe(new RecipeSheetMetal(new ItemStack(ICBM.blockLauncherParts, 1, 0), "RPH", "GCR", "DPR", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'R', OreNames.ROD_IRON, 'P', ItemSheetMetal.SheetMetal.FULL.stack(), 'H', ItemSheetMetalTools.getHammer(), 'D', ItemSheetMetalTools.getShears(), 'G', OreNames.GEAR_IRON));
        }
        else
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockLauncherParts, 1, 0), "RPR", "PCP", "RPR", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'R', UniversalRecipe.WIRE.get(), 'P', UniversalRecipe.PRIMARY_PLATE.get()));
        }
    }
}
