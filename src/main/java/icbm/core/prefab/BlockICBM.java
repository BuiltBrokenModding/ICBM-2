package icbm.core.prefab;

import icbm.Reference;
import icbm.core.CreativeTabICBM;
import icbm.core.Settings;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.UniversalElectricity;
import calclavia.lib.prefab.block.BlockTile;
import calclavia.lib.prefab.tile.IRedstoneProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockICBM extends BlockTile
{
    protected Icon iconTop, iconSide, iconBottom;
    protected boolean requireSidedTextures = false;

    public BlockICBM(int id, String name)
    {
        super(id, UniversalElectricity.machine);
        this.setUnlocalizedName(Reference.PREFIX + name);
        this.setTextureName(Reference.PREFIX + name);
        this.setCreativeTab(CreativeTabICBM.INSTANCE);
    }

    public BlockICBM(String name)
    {
        this(name, UniversalElectricity.machine);
    }

    public BlockICBM(String name, Material material)
    {
        this(Settings.getNextBlockID(), name, material);
    }

    public BlockICBM(int id, String name, Material material)
    {
        super(id, material);
        setUnlocalizedName(Reference.PREFIX + name);
        setTextureName(Reference.PREFIX + name);
        setCreativeTab(CreativeTabICBM.INSTANCE);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);

        if (this.requireSidedTextures)
        {
            this.iconTop = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_top");
            this.iconSide = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_side");
            this.iconBottom = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_bottom");
        }
    }

    /** Is this block powering the block on the specified side */
    @Override
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IRedstoneProvider)
        {
            return ((IRedstoneProvider) tileEntity).isPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
        }

        return 0;
    }

    /** Is this block indirectly powering the block on the specified side */
    @Override
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof IRedstoneProvider)
        {
            return ((IRedstoneProvider) tileEntity).isIndirectlyPoweringTo(ForgeDirection.getOrientation(side)) ? 15 : 0;
        }

        return 0;
    }
}
