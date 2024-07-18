package uce.commands;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import carpet.utils.Translations;

import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import static carpet.utils.CommandHelper.canUseCommand;

import static uce.utils.nbtHelper.createNbtListFromFloatArray;
import static uce.UselessCarpetExtensionSettings.commandReplaceItemFrame;

public class ReplaceItemFrameCommand {

    private static final NbtElement ROTATION_AXIS_NBT = createNbtListFromFloatArray(new Float[]{0.0F, 0.0F, -1.0F});

    private static final NbtElement TRANSFORMATION_SCALE_NBT = createNbtListFromFloatArray(new Float[]{0.5F,0.5F,0.5F});

    private static final NbtElement TRANSFORMATION_RIGHT_ROTATION_NBT = createNbtListFromFloatArray(new Float[]{0.0F,0.0F,0.0F,1.0F});

    private static final NbtElement TRANSFORMATION_TRANSLATION_NBT = createNbtListFromFloatArray(new Float[]{0.0F,0.0F,0.0F});

    private static final NbtElement MAX_BRIGHTNESS_NBT = createBrightnessNbt();

    private static final NbtCompound TRANSFORMATION_NBT = createTransformationNbt();

    private static NbtCompound createTransformationNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("right_rotation", TRANSFORMATION_RIGHT_ROTATION_NBT);
        nbt.put("scale", TRANSFORMATION_SCALE_NBT);
        nbt.put("translation", TRANSFORMATION_TRANSLATION_NBT);
        return nbt;
    }

    private static NbtElement createRotationNbt(float angle) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("axis", ROTATION_AXIS_NBT);
        nbt.put("angle", NbtFloat.of(angle));
        return nbt;
    }

    private static NbtElement createBrightnessNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("sky", 15);
        nbt.putInt("block", 15);
        return nbt;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("replaceItemFrame")
                        .requires(source -> canUseCommand(source, commandReplaceItemFrame))
                        .then(argument("from", blockPos())
                                .then(argument("to", blockPos())
                                        .executes(
                                                context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    BlockPos from = getBlockPos(context, "from");
                                                    BlockPos to = getBlockPos(context, "to");
                                                    context.getSource().getServer().execute(
                                                            () -> ReplaceItemFrameCommand.execute(source, from, to));
                                                    return 1;
                                                }
                                        )
                                )
                        )
        );
    }

    private static void execute(ServerCommandSource source, BlockPos from, BlockPos to) {
        ServerWorld world = source.getWorld();
        Box box = new Box(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
        List<ItemFrameEntity> itemFrameEntities = world.getEntitiesByType(EntityType.ITEM_FRAME, box, (Predicate.not(ItemFrameEntity::containsMap)));
        int replacedItemFrames = replaceItemFrames(world, itemFrameEntities, ReplaceItemFrameCommand::createItemDisplayNbt);
        List<GlowItemFrameEntity> glowItemFrameEntities = world.getEntitiesByType(EntityType.GLOW_ITEM_FRAME, box, (Predicate.not(ItemFrameEntity::containsMap)));
        int replacedGlowItemFrames = replaceItemFrames(world, glowItemFrameEntities, ReplaceItemFrameCommand::createGlowItemDisplayNbt);
        boolean noItemFrame = itemFrameEntities.isEmpty();
        boolean noGlowItemFrame = glowItemFrameEntities.isEmpty();
        if (noItemFrame && noGlowItemFrame) {
            source.sendError(Text.of(Translations.tr("commands.uce.replaceItemFrame.failed.no_item_frame_found")));
        } else if (!noItemFrame && !noGlowItemFrame) {
            source.sendFeedback(() -> Text.of(Translations.tr("commands.uce.replaceItemFrame.success.both")
                            .formatted(
                                    replacedItemFrames, itemFrameEntities.size(),
                                    replacedGlowItemFrames, glowItemFrameEntities.size())),
                    true);
        } else if (!noItemFrame) {
            source.sendFeedback(() -> Text.of(Translations.tr("commands.uce.replaceItemFrame.success.item_frame")
                    .formatted(
                            replacedItemFrames, itemFrameEntities.size())),
                    true);
        } else {
            source.sendFeedback(() -> Text.of(Translations.tr("commands.uce.replaceItemFrame.success.glow_item_frame")
                    .formatted(
                            replacedGlowItemFrames, glowItemFrameEntities.size())),
                    true);
        }
    }

    private static <T extends ItemFrameEntity> int replaceItemFrames(ServerWorld world, List<T> itemFrameEntities, BiFunction<Integer, NbtCompound, NbtCompound> createItemDisplayNbt) {
        int numberOfSuccess = 0;
        for (T itemFrameEntity : itemFrameEntities) {
            ItemStack itemStack = itemFrameEntity.getHeldItemStack();
            int rotationIndex = itemFrameEntity.getRotation();
            DisplayEntity.ItemDisplayEntity itemDisplayEntity = createItemDisplayEntity(world, itemStack);
            NbtCompound itemDisplayNbt = createItemDisplayNbt.apply(rotationIndex, itemDisplayEntity.writeNbt(new NbtCompound()));
            itemDisplayEntity.readNbt(itemDisplayNbt);
            itemDisplayEntity.copyPositionAndRotation(itemFrameEntity);
            InteractionEntity interactionEntity = null;
            if (itemStack.hasCustomName()) {
                interactionEntity = createHoverText(world, itemStack.getName());
                interactionEntity.refreshPositionAndAngles(itemFrameEntity.getX(), itemFrameEntity.getY() - 0.125F, itemFrameEntity.getZ(), itemFrameEntity.getYaw(), itemFrameEntity.getPitch());
            }
            if (world.spawnEntity(itemDisplayEntity) && ((interactionEntity == null) || world.spawnEntity(interactionEntity))) {
                itemFrameEntity.kill();
                numberOfSuccess++;
                continue;
            }
            itemDisplayEntity.kill();
            if (interactionEntity != null) {
                interactionEntity.kill();
            }
        }
        return numberOfSuccess;
    }

    private static DisplayEntity.ItemDisplayEntity createItemDisplayEntity(ServerWorld world, ItemStack itemStack) {
        DisplayEntity.ItemDisplayEntity itemDisplayEntity = new DisplayEntity.ItemDisplayEntity(EntityType.ITEM_DISPLAY, world);
        itemDisplayEntity.getStackReference(0).set(itemStack);
        return itemDisplayEntity;
    }

    private static InteractionEntity createHoverText(ServerWorld world, Text hoverText) {
        InteractionEntity interactionEntity = new InteractionEntity(EntityType.INTERACTION, world);
        interactionEntity.setCustomName(hoverText);
        NbtCompound interactionEntityNbt = interactionEntity.writeNbt(new NbtCompound());
        interactionEntityNbt.putFloat("width", 0.25F);
        interactionEntityNbt.putFloat("height", 0.25F);
        interactionEntity.readNbt(interactionEntityNbt);
        return interactionEntity;
    }

    private static NbtCompound createCommonItemDisplayNbt(int rotationIndex, NbtCompound itemDisplayEntityNbt) {
        itemDisplayEntityNbt.putString("item_display", "fixed");
        NbtCompound transformationNbt = TRANSFORMATION_NBT;
        float rotation = (float) (rotationIndex / 4.0F * Math.PI);
        NbtElement leftRotation = createRotationNbt(rotation);
        transformationNbt.put("left_rotation", leftRotation);
        itemDisplayEntityNbt.put("transformation", transformationNbt);
        return itemDisplayEntityNbt;
    }

    private static NbtCompound createItemDisplayNbt(int rotationIndex, NbtCompound itemDisplayEntityNbt) {
        return createCommonItemDisplayNbt(rotationIndex, itemDisplayEntityNbt);
    }

    private static NbtCompound createGlowItemDisplayNbt(int rotationIndex, NbtCompound itemDisplayEntityNbt) {
        NbtCompound itemDisplayNbt = createCommonItemDisplayNbt(rotationIndex, itemDisplayEntityNbt);
        itemDisplayNbt.put("brightness", MAX_BRIGHTNESS_NBT);
        return itemDisplayNbt;
    }
}
