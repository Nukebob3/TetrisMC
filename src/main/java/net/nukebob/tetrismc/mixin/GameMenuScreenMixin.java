package net.nukebob.tetrismc.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    private void addMinigameButton(CallbackInfo ci) {
        TextIconButtonWidget textIconButtonWidget = TextIconButtonWidget.builder(Text.empty(), (button) -> this.client.setScreen(new TetrisScreen(this)), true).width(20).texture(Identifier.of(TetrisMC.MOD_ID, "icon/button"), 16, 16).build();
        textIconButtonWidget.setPosition(this.width / 2 - 100 + 203, 50);

        for (ButtonWidget button : this.children().stream().filter(e -> e instanceof ButtonWidget).map(e -> (ButtonWidget) e).toList()) {
            if (button.getMessage().equals(Text.translatable("menu.returnToGame"))) {
                int buttonX = button.getX();
                int buttonY = button.getY();
                int buttonWidth = button.getWidth();
                textIconButtonWidget.setPosition(buttonX + buttonWidth + 5, buttonY);
                break;
            }
        }

        if (TetrisConfig.loadConfig().mod_enabled) this.addDrawableChild(textIconButtonWidget);
    }
}
