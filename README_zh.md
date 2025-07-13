# Useless Carpet Extension
纯粹为了好玩而添加一些没什么用的特性。

# 模组设置
## keepSpectatingOnTarget
在旁观其他实体时，当其进入其他维度后仍会继续旁观该实体。在原版中，旁观者不会随被旁观的实体进入其他维度而且会脱离被旁观的实体。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `BUGFIX`


## playerKilledByChargedCreeperDropHead
被闪电杀死的玩家会掉落其头颅。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`

## commandReplaceItemFrame
替换指定区域内含有物品的(荧光)物品展示框为物品展示实体，不包括含有地图的物品展示框。荧光物品展示框会被替换为同样具有荧光效果的物品展示实体。可以使用remove参数替换回此命令创建的展示实体为物品展示框。
* 数据类型: `String`
* 默认值: `false`
* 可用选项: `true`, `false`, `0`, `1`, `2`, `3`, `4`, `ops`
* 所属类别: `UCE`, `COMMAND`

## retainTridentDamage
让三叉戟能够在击中实体或方块后仍能造成伤害。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`

## throwableFireCharge
允许玩家使用燃烧弹物品发射恶魂火球，长按可蓄力增加爆炸威力。潜行时使用可使火球停留在原地。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`

## muteJukebox
阻止唱片机播放音乐。该规则总是会覆盖jukeboxNoteblockMode。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`, `JUKEBOX`

## jukeboxNoteblockMode
唱片机仅在上方方块为空气时播放。该规则总是被muteJukebox覆盖。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`, `JUKEBOX`

## creeperDropAllDiscs
苦力怕被骷髅杀死后可掉落所有种类的唱片。
* 数据类型: `Boolean`
* 默认值: `false`
* 可用选项: `true`, `false`
* 所属类别: `UCE`, `FEATURE`

# 命令

## 替换（发光）展示框

**用法： `/replaceItemFrame <from> <to> [remove]`**

替换指定区域内含有物品的(荧光)物品展示框为物品展示实体，不包括含有地图的物品展示框。荧光物品展示框会被替换为同样具有荧光效果的物品展示实体。可以使用`remove`参数替换回此命令创建的展示实体为物品展示框。
