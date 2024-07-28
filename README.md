# Useless Carpet Extension
Add some useless features in game just for fun.

# Carpet Mod Settings
## keepSpectatingOnTarget
Keep spectating the target entity even when it entered other dimension. In vanilla, player will stop spectating its target after it entered other dimension.
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `UCE`, `BUGFIX`


## playerKilledByChargedCreeperDropHead
Players killed by charged creeper will drop their own player head.
* Type: `Boolean`  
* Default value: `false`  
* Allowed options: `true`, `false`  
* Categories: `UCE`, `FEATURE`  

## commandReplaceItemFrame
Replace all (glow) item frames containing item in selected area with item display entity, excluding item frames containing maps.
* Type: `String`
* Default value: `false`
* Allowed options: `true`, `false`, `0`, `1`, `2`, `3`, `4`, `ops`
* Categories: `UCE`, `COMMAND`

## retainTridentDamage
Allow trident to still deal damage after hitting an entity or block
* type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `UCE`, `FEATURE`

# Commands

## Replace (Glow) Item Frame command 

**Usage: `/replaceItemFrame <from> <to>`**

Replace all (glow) item frames containing item in selected area with item display entity, excluding item frames containing maps. Glow item frame will be replaced by the item display entity that also appears to glow.