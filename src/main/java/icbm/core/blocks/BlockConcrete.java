package icbm.core.blocks;

import icbm.Reference;
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
    	 if (metadata < ConcreteType.values().length)
         {
             return ConcreteType.values()[metadata].icon;
         }

         return ConcreteType.CompactConcrete.icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        for (ConcreteType data : ConcreteType.values())
        {
            data.icon = iconRegister.registerIcon(this.getUnlocalizedName().replace("tile.", "") + data.name);
        }

        this.blockIcon = ConcreteType.CompactConcrete.icon;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        for(ConcreteType con : ConcreteType.values()) {
        	return con.explores;
        }
        return this.getExplosionResistance(par1Entity);
    }

    @Override
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(ConcreteType con : ConcreteType.values()) {
            par3List.add(new ItemStack(par1, 1, con.ordinal()));
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
    
    public static enum ConcreteType {
    	Concrete("_normal", 28),
    	CompactConcrete("_compact", 38),
    	ReinforcedConcrete("_reinforced", 48),
        ColoredConcrete("_colored", 28),
        DarkConcrete("_dark", 28);
        
        ConcreteType(String name, int explosionResis) {
        	this.name = name;
        	this.explores = explosionResis;
        }
        
        public int explores;
        public Icon icon;
        public String name;
    }
}
