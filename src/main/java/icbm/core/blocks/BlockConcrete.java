package icbm.core.blocks;

import icbm.core.prefab.BlockICBM;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import calclavia.api.atomicscience.IAntiPoisonBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockConcrete extends BlockICBM implements IAntiPoisonBlock
{
    private Icon iconCompact, iconReinforced;

    public BlockConcrete(int id)
    {
        super(id, "concrete", Material.rock);
        this.setHardness(3.8f);
        this.setResistance(50);
        this.setStepSound(soundMetalFootstep);
    }

    @Override
    public Icon getIcon(int side, int metadata)
    {
        switch (metadata)
        {
            case 1:
                return this.iconCompact;
            case 2:
                return this.iconReinforced;
        }

        return this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);

        this.iconCompact = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "Compact");
        this.iconReinforced = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + "Reinforced");

    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        switch (metadata)
        {
            case 1:
                return 38;
            case 2:
                return 48;
        }

        return this.getExplosionResistance(par1Entity);
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 0; i < 3; i++)
        {
            par3List.add(new ItemStack(par1, 1, i));
        }
    }

    @Override
    public boolean isPoisonPrevention(World par1World, int x, int y, int z, String type)
    {
        return type.equalsIgnoreCase("radiation");
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return false;
    }
}
