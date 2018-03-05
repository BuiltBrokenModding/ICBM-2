# INFO
This log contains changes made to the project. Each entry contains changed made after the last version but before the number was changed. Any changes made after a number change are considered part of the next release. This is regardless if versions are still being released with that version number attached. 

If this is a problem, use exact build numbers to track changes. As each build logs the git-hash it was created from to better understand changes made.

# Versions
## 2.15.5 - 3/4/2018
### Runtime Changes
Fixed: server side crash caused by sending client side packets in launcher block
Fixed: Empty spawn eggs registering as valid explosives
Fixed: Rocket not being craftable with engines
Fixed: Micro missile warhead recipe not being registered
Fixed: NPE while crafting fuel with engine, due to failure to build module instance
Added: Gift box explosive
Added: Firework blast
Disabled: Trigger slot in warhead station

### Development Changes
Deprecated: Missile Module Builder - will be replaced by a registery system
Reworked: Module creation process - module items create instances instead of builder

## 2.15.4 - 11/3/2017
### Runtime Changes
Added: icon for medium missile
Added: missing explosive icons
Added: fragment for swords
Added: new fragment renderer
Reworked: Ore puller explosive to improve runtime (scans in rows instead of using a pathfinder)
Reworked: creative tab handling
Removed: Grid crafting of warhead + explosive
Fixed: duplication when breaking launcher
Fixed: Recipes
Fixed: items missing from creative tab
Fixed: warhead crafter running in automation mode always
Improved: messages for launcher creation
Changed: emp size from 1 to 10 (in reflection of VE 10 to 1 change, basically nothing changed :P)


//TODO update change log for older versions
