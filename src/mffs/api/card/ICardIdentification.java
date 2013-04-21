package mffs.api.card;

import mffs.api.security.SecurityPermission;
import net.minecraft.item.ItemStack;

/**
 * Applied to Item ID cards.
 * 
 * @author Calclavia
 * 
 */
public interface ICardIdentification extends ICard
{
	public boolean hasPermission(ItemStack itemStack, SecurityPermission permission);

	public boolean addPermission(ItemStack itemStack, SecurityPermission permission);

	public boolean removePermission(ItemStack itemStack, SecurityPermission permission);

	public String getUsername(ItemStack itemStack);

	public void setUsername(ItemStack itemStack, String username);
}
