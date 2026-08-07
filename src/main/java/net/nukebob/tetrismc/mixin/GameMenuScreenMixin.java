package net.nukebob.tetrismc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 3), method = "createPauseMenu")
    private LayoutElement tetrismc$addMinigameButton(LinearLayout instance, LayoutElement child, Operation<LayoutElement> original) {
        original.call(instance, child);

        if (!TetrisConfig.loadConfig().mod_enabled) return instance;

        SpriteIconButton tetris = SpriteIconButton.builder(Component.empty(), (button) -> this.minecraft.setScreenAndShow(new TetrisScreen(this)), true).width(20).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/button"), 16, 16).tooltip(Component.literal("Tetris MC")).build();
        tetris.setPosition(this.width / 2 - 100 + 203, 50);

        instance.addChild(tetris);
        return instance;
    }
}
