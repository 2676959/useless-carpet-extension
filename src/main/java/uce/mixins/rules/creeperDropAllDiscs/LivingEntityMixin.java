package uce.mixins.rules.creeperDropAllDiscs;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static uce.UselessCarpetExtensionSettings.creeperDropAllDiscs;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getLootTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
    private LootTable uce$replaceLootTable(LootManager lootManager, Identifier identifier) {
        if (!creeperDropAllDiscs) {
            return lootManager.getLootTable(identifier);
        }
        if (identifier.equals(EntityType.CREEPER.getLootTableId())) {
            return LootTable.builder()
                    .pool(
                            LootPool.builder()
                                    .rolls(ConstantLootNumberProvider.create(1.0F))
                                    .with(ItemEntry.builder(Items.GUNPOWDER)
                                            .apply(SetCountLootFunction
                                                    .builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
                                            .apply(LootingEnchantLootFunction
                                                    .builder(UniformLootNumberProvider.create(0.0F, 1.0F)))))
                    .pool(
                            LootPool.builder()
                                    .with(TagEntry.expandBuilder(ItemTags.MUSIC_DISCS))
                                    .conditionally(
                                            EntityPropertiesLootCondition.builder(
                                                    LootContext.EntityTarget.KILLER,
                                                    EntityPredicate.Builder.create()
                                                            .type(EntityTypeTags.SKELETONS))))
                    .build();
        }
        return lootManager.getLootTable(identifier);
    }
}
