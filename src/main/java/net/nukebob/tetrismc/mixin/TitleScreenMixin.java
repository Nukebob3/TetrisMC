package net.nukebob.tetrismc.mixin;

import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "createNormalMenuOptions")
    private void addMinigameButton(int y, int spacingY, CallbackInfoReturnable<Integer> cir) {
        SpriteIconButton textIconButtonWidget = SpriteIconButton.builder(Component.empty(), button -> this.minecraft.setScreenAndShow(new TetrisScreen(this)), true).width(20).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/button"), 16, 16).tooltip(Component.literal("Tetris MC")).build();
        textIconButtonWidget.setPosition(this.width / 2 - 100 + 204, y);
        if (TetrisConfig.loadConfig().mod_enabled) this.addRenderableWidget(textIconButtonWidget);
    }

    /*@WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SpriteIconButton;setPosition(II)V", ordinal = 1))
    private void addTetrisButton(SpriteIconButton instance, int x, int y, Operation<Void> original) {
        original.call(instance, x, y);

        if (!TetrisConfig.loadConfig().mod_enabled) return;

        int topPos = this.height / 4 + 48;
        topPos+=24+48;
        SpriteIconButton tetris = this.addRenderableWidget(SpriteIconButton.builder(Component.empty(), button -> this.minecraft.setScreenAndShow(new TetrisScreen(this)), true).width(20).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/button"), 16, 16).build());
        tetris.setPosition(this.getHorizontalPosition(4, 3, 20), topPos);
    }*/
}
