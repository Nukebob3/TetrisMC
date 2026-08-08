package net.nukebob.tetrismc.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At(value = "RETURN"), method = "createPauseMenu")
    private void tetrismc$addMinigameButton(CallbackInfo ci) {
        if (!TetrisConfig.loadConfig().mod_enabled) return;

        SpriteIconButton tetris = SpriteIconButton.builder(Component.empty(), (button) -> this.minecraft.setScreenAndShow(new TetrisScreen(this)), true).width(20).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/button"), 16, 16).build();
        tetris.setPosition(this.width / 2 - 100 + 203, 50);

        for (Button button : this.children().stream().filter(Button.class::isInstance).map(e -> (Button) e).toList()) {
            if (button.getMessage().equals(Component.translatable("menu.returnToGame"))) {
                int buttonX = button.getX();
                int buttonY = button.getY();
                int buttonWidth = button.getWidth();
                tetris.setPosition(buttonX + buttonWidth + 5, buttonY);
                break;
            }
        }

        this.addRenderableWidget(tetris);
    }
}
