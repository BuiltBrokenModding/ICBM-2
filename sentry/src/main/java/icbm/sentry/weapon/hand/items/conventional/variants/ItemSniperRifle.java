package icbm.sentry.weapon.hand.items.conventional.variants;

import icbm.sentry.weapon.hand.items.WeaponContent;
import icbm.sentry.weapon.hand.items.conventional.ItemConventional;

public class ItemSniperRifle extends ItemConventional {

	public ItemSniperRifle(int id) {
		super(id, "sniperRifle", new WeaponContent(7, 10, 0.3D, 20, 1, "sniper"));
	}

}
