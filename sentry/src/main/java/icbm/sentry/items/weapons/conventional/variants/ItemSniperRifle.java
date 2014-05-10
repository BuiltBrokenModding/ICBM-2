package icbm.sentry.items.weapons.conventional.variants;

import icbm.sentry.items.weapons.WeaponContent;
import icbm.sentry.items.weapons.conventional.ItemConventional;

public class ItemSniperRifle extends ItemConventional {

	public ItemSniperRifle(int id) {
		super(id, "sniperRifle", new WeaponContent(7, 10, 0.3D, 0, "sniper"));
	}

}
