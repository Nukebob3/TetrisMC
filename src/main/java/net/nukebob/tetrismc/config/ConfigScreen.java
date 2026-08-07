package net.nukebob.tetrismc.config;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.nukebob.tetrismc.TetrisMC;

public class ConfigScreen extends Screen {

    private final Screen parent;

    TetrisConfig config = TetrisConfig.loadConfig();

    int enableColour = 8781731;
    int disableColour = 16745861;

    public ConfigScreen(Screen parent) {
        super(Component.translatable(TetrisMC.MOD_ID + ":config.title"));
        this.parent = parent;
        
    }

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Button toggleModEnabledWidget = Button.builder(Component.translatable(TetrisMC.MOD_ID + ":config.mod").append(" ").append(Component.translatable(TetrisMC.MOD_ID + ":config." + (config.mod_enabled ? "enabled" : "disabled"))).withColor(config.mod_enabled ? CommonColors.GREEN : CommonColors.RED), this::toggleModEnabled)
                .bounds(centerX - buttonWidth / 2, centerY - 45, buttonWidth, buttonHeight).build();
        Button hardDropSettingsWidget = Button.builder(Component.translatable(TetrisMC.MOD_ID + ":tetris.hard_drop").append(": ").append(Component.translatable(config.tetris_hard_drop == 0 ? TetrisMC.MOD_ID + ":config.disabled" : TetrisMC.MOD_ID + ":tetris.hard_drop." + (config.tetris_hard_drop == 1 ? "previewless" : (config.tetris_hard_drop == 2 ? "outline" : "hologram")))).withColor((config.tetris_hard_drop != 0) ? enableColour : disableColour), button -> {config.tetris_hard_drop++; if (config.tetris_hard_drop > 3) config.tetris_hard_drop = 0; button.setMessage(Component.translatable(TetrisMC.MOD_ID + ":tetris.hard_drop").append(": ").append(Component.translatable(config.tetris_hard_drop == 0 ? TetrisMC.MOD_ID + ":config.disabled" : TetrisMC.MOD_ID + ":tetris.hard_drop." + (config.tetris_hard_drop == 1 ? "previewless" : (config.tetris_hard_drop == 2 ? "outline" : "hologram")))).withColor((config.tetris_hard_drop != 0) ? enableColour : disableColour)); TetrisConfig.saveConfig();})
                .bounds(centerX - buttonWidth / 2, centerY - 20, buttonWidth, buttonHeight).build();
        Button randomTextureEnabledWidget = Button.builder(Component.translatable(TetrisMC.MOD_ID + ":tetris.random_texture").append(" ").append(Component.translatable(TetrisMC.MOD_ID + ":config." + (config.tetris_random_textures ? "enabled" : "disabled"))).withColor(config.tetris_random_textures ? enableColour : disableColour), this::toggleRandomTextureEnabled)
                .bounds(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight).build();
        AbstractSliderButton volumeSlider = new AbstractSliderButton(centerX - buttonWidth / 2, centerY + 20, buttonWidth, buttonHeight,
                Component.translatable(TetrisMC.MOD_ID + ":config.volume"), config.tetris_volume) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable(TetrisMC.MOD_ID + ":config.volume").append(": " + (int) (Math.round(this.value * 100)) + "%"));
            }

            @Override
            protected void applyValue() {
                config.tetris_volume = (float) this.value;
                TetrisConfig.saveConfig();
            }
        };

        Button doneButtonWidget = Button.builder(Component.translatable(TetrisMC.MOD_ID + ":config.done").withColor(CommonColors.WHITE), button -> closeScreen())
                .bounds(centerX - buttonWidth / 2, centerY + 45, buttonWidth, buttonHeight).build();

        this.addRenderableWidget(toggleModEnabledWidget);
        this.addRenderableWidget(hardDropSettingsWidget);
        this.addRenderableWidget(randomTextureEnabledWidget);
        this.addRenderableWidget(volumeSlider);
        this.addRenderableWidget(doneButtonWidget);

        super.init();
    }

    private void toggleModEnabled(Button buttonWidget) {
        config.mod_enabled = !config.mod_enabled;
        buttonWidget.setMessage(Component.translatable(TetrisMC.MOD_ID + ":config.mod").append(" ").append(Component.translatable(TetrisMC.MOD_ID + ":config." + (config.mod_enabled ? "enabled" : "disabled"))).withColor(config.mod_enabled ? CommonColors.GREEN : CommonColors.RED));
        TetrisConfig.saveConfig();
    }

    private void toggleRandomTextureEnabled(Button buttonWidget) {
        config.tetris_random_textures = !config.tetris_random_textures;
        buttonWidget.setMessage(Component.translatable(TetrisMC.MOD_ID + ":tetris.random_texture").append(" ").append(Component.translatable(TetrisMC.MOD_ID + ":config." + (config.tetris_random_textures ? "enabled" : "disabled"))).withColor(config.tetris_random_textures ? enableColour : disableColour));
        TetrisConfig.saveConfig();
    }

    private void closeScreen() {
        this.minecraft.setScreenAndShow(this.parent);
    }
}
