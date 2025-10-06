package net.nukebob.tetrismc.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "addNormalWidgets")
    private void addMinigameButton(int y, int spacingY, CallbackInfoReturnable<Integer> cir) {
        TextIconButtonWidget textIconButtonWidget = TextIconButtonWidget.builder(Text.empty(), (button) -> this.client.setScreen(new TetrisScreen(this)), true).width(20).texture(Identifier.of(TetrisMC.MOD_ID, "icon/button"), 16, 16).build();
        textIconButtonWidget.setPosition(this.width / 2 - 100 + 204, y);
        if (TetrisConfig.loadConfig().mod_enabled) this.addDrawableChild(textIconButtonWidget);
    }
}
