package icbm.core.tiles;

import icbm.core.ICBMCore;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import resonant.lib.content.module.TileRender;
import resonant.lib.content.module.prefab.TileInventory;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Basic version of the chest with a reduce inventory size but increase protection. Will also be
 * used for cargo drops by missiles.
 * 
 * @author Darkguardsman */
public class TileBox extends TileInventory
{
    private ForgeDirection facing = ForgeDirection.NORTH;

    public TileBox()
    {
        super(Material.iron);
        this.maxSlots = 27;
        this.blockHardness = 100;
        this.blockResistance = 100;
    }

    @Override
    public void onInstantiate()
    {
        super.onInstantiate();
        //TODO add recipe equal to a iron based chest
    }

    @Override
    protected boolean use(EntityPlayer player, int side, Vector3 hit)
    {
        player.openGui(ICBMCore.INSTANCE, 0, world(), x(), y(), z());
        return true;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot >= 0 && slot < this.maxSlots;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.facing = ForgeDirection.getOrientation(nbt.getByte("facing"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("facing", (byte) this.facing.ordinal());
    }

    @Override
    public Class<? extends Container> getContainer()
    {
        return ContainerBox.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected TileRender newRenderer()
    {
        return null;
    }

}
