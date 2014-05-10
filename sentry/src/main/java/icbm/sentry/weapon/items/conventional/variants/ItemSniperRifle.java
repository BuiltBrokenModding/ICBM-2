package icbm.sentry.weapon.items.conventional.variants;

import icbm.sentry.weapon.items.WeaponContent;
import icbm.sentry.weapon.items.conventional.ItemConventional;

public class ItemSniperRifle extends ItemConventional {

	public ItemSniperRifle(int id) {
		super(id, "sniperRifle", new WeaponContent(7, 10, 0.3D, 10, 1, "sniper"));
	}

}
