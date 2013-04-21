package mffs.api.card;

import net.minecraft.item.ItemStack;
import universalelectricity.core.vector.Vector3;

public interface ICardLink
{
	public void setLink(ItemStack itemStack, Vector3 position);

	public Vector3 getLink(ItemStack itemStack);
}
