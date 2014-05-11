package icbm.sentry.weapon.hand.items.conventional.variants;

import icbm.sentry.weapon.hand.items.WeaponContent;
import icbm.sentry.weapon.hand.items.conventional.ItemConventional;

public class ItemShotgun extends ItemConventional {

	public ItemShotgun(int id) {
		super(id, "shotgun", new WeaponContent(20, 6, 6.5D, 20, 5, "shotgun"));
	}

}
