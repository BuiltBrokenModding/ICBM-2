package icbm.content.warhead;

import icbm.content.ItemSaveUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import icbm.api.explosion.IExplosive;
import resonant.lib.utility.LanguageUtility;

import java.util.List;

public class ItemBlockExplosive extends ItemBlock
{
    public ItemBlockExplosive(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName()
    {
        return "icbm.explosive";
    }

    public IExplosive getExplosive(ItemStack itemStack)
    {
        return ItemSaveUtil.getExplosive(itemStack);
    }

    public void setExplosive(ItemStack itemStack, IExplosive ex)
    {
        ItemSaveUtil.setExplosive(itemStack, ex);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        super.addInformation(stack, player, lines, b);
        lines.add(LanguageUtility.getLocal(getUnlocalizedName() + ".explosive") + ": " + getExplosive(stack));
    }
}
