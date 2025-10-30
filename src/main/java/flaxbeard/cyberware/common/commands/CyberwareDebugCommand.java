package flaxbeard.cyberware.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;

public class CyberwareDebugCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("cyberware-debug")
                .requires(src -> src.hasPermission(2)) // Only operators
                .then(Commands.literal("get")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(ctx -> getData(ctx.getSource(), EntityArgument.getEntity(ctx, "target")))
                        )
                )
                .then(Commands.literal("setTolerance")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 200))
                                        .executes(ctx -> setTolerance(
                                                ctx.getSource(),
                                                EntityArgument.getEntity(ctx, "target"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                ))
                )
                .then(Commands.literal("setEssence")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setEssence(
                                                ctx.getSource(),
                                                EntityArgument.getEntity(ctx, "target"),
                                                IntegerArgumentType.getInteger(ctx, "amount")
                                        ))
                                ))
                )
        );
    }

    private static int getData(CommandSource source, Entity target) {
        if (!(target instanceof LivingEntity)) {
            source.sendFailure(new StringTextComponent("§cTarget must be a LivingEntity"));
            return 0;
        }

        LivingEntity living = (LivingEntity) target;
        LazyOptional<ICyberwareUserData> lazy = CyberwareAPI.getCyberwareData(living);

        if (!lazy.isPresent()) {
            source.sendFailure(new StringTextComponent("§cTarget has no Cyberware capability"));
            return 0;
        }

        ICyberwareUserData data = lazy.orElseThrow(RuntimeException::new);
        CompoundNBT nbt = data.serializeNBT();
        source.sendSuccess(new StringTextComponent("§aCyberware Data: §f" + nbt), false);

        return 1;
    }

    private static int setTolerance(CommandSource source, Entity target, int amount) {
        if (!(target instanceof LivingEntity)) {
            source.sendFailure(new StringTextComponent("§cTarget must be a LivingEntity"));
            return 0;
        }

        LivingEntity living = (LivingEntity) target;
        LazyOptional<ICyberwareUserData> lazy = CyberwareAPI.getCyberwareData(living);

        if (!lazy.isPresent()) {
            source.sendFailure(new StringTextComponent("§cTarget has no Cyberware capability"));
            return 0;
        }

        ICyberwareUserData data = lazy.orElseThrow(RuntimeException::new);
        data.setTolerance(living, amount);
        source.sendSuccess(new StringTextComponent("§aSet tolerance of " + living.getName().getString() + " to " + amount), false);

        return 1;
    }

    private static int setEssence(CommandSource source, Entity target, int amount) {
        if (!(target instanceof LivingEntity)) {
            source.sendFailure(new StringTextComponent("§cTarget must be a LivingEntity"));
            return 0;
        }

        LivingEntity living = (LivingEntity) target;
        LazyOptional<ICyberwareUserData> lazy = CyberwareAPI.getCyberwareData(living);

        if (!lazy.isPresent()) {
            source.sendFailure(new StringTextComponent("§cTarget has no Cyberware capability"));
            return 0;
        }

        ICyberwareUserData data = lazy.orElseThrow(RuntimeException::new);
        data.setEssence(amount);
        source.sendFailure(new StringTextComponent("§aSet legacy Essence of " + living.getName().getString() + " to " + amount));

        return 1;
    }
}

