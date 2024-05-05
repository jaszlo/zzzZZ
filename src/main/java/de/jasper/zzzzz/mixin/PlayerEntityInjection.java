package de.jasper.zzzzz.mixin;

import com.mojang.datafixers.util.Either;
import de.jasper.zzzzz.SleepPrinter;
import de.jasper.zzzzz.ZzzzzSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityInjection {

    @Unique
    private boolean toggle = false;

    @Inject(method="trySleep", at=@At("RETURN"))
    private void printProlog(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        toggle = true;
        Either<PlayerEntity.SleepFailureReason, Unit> result = cir.getReturnValue();
        // Did go to sleep
        if (result.left().isEmpty() && ZzzzzSettings.usePrologText.getValue()) {
            SleepPrinter.printProlog();
        }
    }


    @Inject(method="wakeUp(ZZ)V", at=@At("HEAD"))
    private void printEpilogue(CallbackInfo ci) {
        if (toggle && ZzzzzSettings.useEpilogueText.getValue()) {
            toggle = false;
            SleepPrinter.printEpilogue();
        }
    }
}
