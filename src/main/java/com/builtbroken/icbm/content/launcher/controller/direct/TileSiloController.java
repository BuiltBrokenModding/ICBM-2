package com.builtbroken.icbm.content.launcher.controller.direct;

import com.builtbroken.icbm.ICBM;
import com.builtbroken.icbm.content.launcher.TileAbstractLauncher;
import com.builtbroken.icbm.content.prefab.ItemBlockICBM;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.recipe.UniversalRecipe;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileMachine;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Controller that links directly to a silo. Cheap and simple... all a player should really need :P
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2016.
 */
public class TileSiloController extends TileMachine implements IPostInit, IPacketIDReceiver
{
    @SideOnly(Side.CLIENT)
    private static IIcon top;
    @SideOnly(Side.CLIENT)
    private static IIcon side;

    /** Is the tile in redstone mode, meaning it will trigger launch if it receives a redstone signal */
    protected boolean inRedstoneMode = true;

    /** Connected launcher */
    protected TileAbstractLauncher launcher;

    public TileSiloController()
    {
        super("siloController", Material.iron);
        this.itemBlock = ItemBlockICBM.class;
        this.canEmmitRedstone = true;
        this.resistance = 5f;
        this.hardness = 10f;
    }

    @Override
    public void update()
    {
        super.update();
        if (ticks % 20 == 0 && launcher == null)
        {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            {
                Pos pos = toPos().add(dir);
                TileEntity tile = pos.getTileEntity(world());
                if (tile instanceof TileAbstractLauncher)
                {
                    launcher = (TileAbstractLauncher) tile;
                    break;
                }
            }
            if (launcher != null && inRedstoneMode && isIndirectlyPowered())
            {
                launcher.fireMissile();
            }
        }
    }

    @Override
    protected boolean onPlayerRightClickWrench(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            if (inRedstoneMode)
            {
                inRedstoneMode = false;
                player.addChatComponentMessage(new ChatComponentTranslation("tile.icbm:siloController.msg.redstone.enabled"));
            }
            else
            {
                inRedstoneMode = true;
                player.addChatComponentMessage(new ChatComponentTranslation("tile.icbm:siloController.msg.redstone.disabled"));
            }
        }
        return true;
    }

    @Override
    public Tile newTile(World world, int meta)
    {
        return new TileSiloController();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        side = iconRegister.registerIcon(ICBM.PREFIX + "controller.direct.silo");
        top = iconRegister.registerIcon(ICBM.PREFIX + "controller.direct.silo.top");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int face)
    {
        if (face == 1)
        {
            return top;
        }
        return side;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("redstoneMode"))
        {
            this.inRedstoneMode = nbt.getBoolean("redstoneMode");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("redstoneMode", inRedstoneMode);
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ICBM.blockDirectSiloController), "ITI", "ZCZ", "RER", 'C', UniversalRecipe.CIRCUIT_T2.get(), 'I', Items.iron_ingot, 'R', Items.redstone, 'T', Blocks.redstone_torch, 'Z', UniversalRecipe.WIRE.get(), 'E', Items.ender_eye));
    }
}
