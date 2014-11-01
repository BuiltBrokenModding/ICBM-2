package icbm.core.prefab;

import icbm.Reference;
import icbm.TabICBM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockICBM extends Block
{
    @SideOnly(Side.CLIENT) protected IIcon iconTop;
    @SideOnly(Side.CLIENT) protected IIcon iconSide;
    @SideOnly(Side.CLIENT) protected IIcon iconBottom;

    protected boolean requireSidedTextures = false;

    public BlockICBM(String name)
    {
        this(name, Material.circuits);
    }

    public BlockICBM(String name, Material material)
    {
        super(material);
        setBlockName(Reference.PREFIX + name);
        setBlockTextureName(Reference.PREFIX + name);
        setCreativeTab(TabICBM.INSTANCE);
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        super.registerBlockIcons(iconRegister);

        if (this.requireSidedTextures)
        {
            this.iconTop = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_top");
            this.iconSide = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_side");
            this.iconBottom = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "_bottom");
        }
    }
}
