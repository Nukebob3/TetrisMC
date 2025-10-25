package net.nukebob.tetrismc.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.nukebob.tetrismc.TetrisMC;

public class ConfigScreen extends Screen {

    private final Screen parent;

    TetrisConfig config = TetrisConfig.loadConfig();

    int enableColour = 8781731;
    int disableColour = 16745861;

    public ConfigScreen(Screen parent) {
        super(Text.translatable(TetrisMC.MOD_ID + ":config.title"));
        this.parent = parent;
        
    }

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        ButtonWidget toggleModEnabledWidget = ButtonWidget.builder(Text.translatable(TetrisMC.MOD_ID + ":config.mod").append(" ").append(Text.translatable(TetrisMC.MOD_ID + ":config." + (config.mod_enabled ? "enabled" : "disabled"))).withColor(config.mod_enabled ? Colors.GREEN : Colors.RED), this::toggleModEnabled)
                .dimensions(centerX - buttonWidth / 2, centerY - 45, buttonWidth, buttonHeight).build();
        ButtonWidget hardDropSettingsWidget = ButtonWidget.builder(Text.translatable(TetrisMC.MOD_ID + ":tetris.hard_drop").append(": ").append(Text.translatable(config.tetris_hard_drop == 0 ? TetrisMC.MOD_ID + ":config.disabled" : TetrisMC.MOD_ID + ":tetris.hard_drop." + (config.tetris_hard_drop == 1 ? "previewless" : (config.tetris_hard_drop == 2 ? "outline" : "hologram")))).withColor((config.tetris_hard_drop != 0) ? enableColour : disableColour), button -> {config.tetris_hard_drop++; if (config.tetris_hard_drop > 3) config.tetris_hard_drop = 0; button.setMessage(Text.translatable(TetrisMC.MOD_ID + ":tetris.hard_drop").append(": ").append(Text.translatable(config.tetris_hard_drop == 0 ? TetrisMC.MOD_ID + ":config.disabled" : TetrisMC.MOD_ID + ":tetris.hard_drop." + (config.tetris_hard_drop == 1 ? "previewless" : (config.tetris_hard_drop == 2 ? "outline" : "hologram")))).withColor((config.tetris_hard_drop != 0) ? enableColour : disableColour)); TetrisConfig.saveConfig();})
                .dimensions(centerX - buttonWidth / 2, centerY - 20, buttonWidth, buttonHeight).build();
        ButtonWidget randomTextureEnabledWidget = ButtonWidget.builder(Text.translatable(TetrisMC.MOD_ID + ":tetris.random_texture").append(" ").append(Text.translatable(TetrisMC.MOD_ID + ":config." + (config.tetris_random_textures ? "enabled" : "disabled"))).withColor(config.tetris_random_textures ? enableColour : disableColour), this::toggleRandomTextureEnabled)
                .dimensions(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight).build();
        SliderWidget volumeSlider = new SliderWidget(centerX - buttonWidth / 2, centerY + 20, buttonWidth, buttonHeight,
                Text.translatable(TetrisMC.MOD_ID + ":config.volume"), config.tetris_volume) {
            {
                this.updateMessage();
            }
            @Override
            protected void updateMessage() {
                this.setMessage(Text.translatable(TetrisMC.MOD_ID + ":config.volume").append(": " + (int) (Math.round(this.value * 100)) + "%"));
            }

            @Override
            protected void applyValue() {
                config.tetris_volume = (float) this.value;
                TetrisConfig.saveConfig();
            }
        };

        ButtonWidget doneButtonWidget = ButtonWidget.builder(Text.translatable(TetrisMC.MOD_ID + ":config.done").withColor(Colors.WHITE), button -> closeScreen())
                .dimensions(centerX - buttonWidth / 2, centerY + 45, buttonWidth, buttonHeight).build();

        this.addDrawableChild(toggleModEnabledWidget);
        this.addDrawableChild(hardDropSettingsWidget);
        this.addDrawableChild(randomTextureEnabledWidget);
        this.addDrawableChild(volumeSlider);
        this.addDrawableChild(doneButtonWidget);

        super.init();
    }

    private void toggleModEnabled(ButtonWidget buttonWidget) {
        config.mod_enabled = !config.mod_enabled;
        buttonWidget.setMessage(Text.translatable(TetrisMC.MOD_ID + ":config.mod").append(" ").append(Text.translatable(TetrisMC.MOD_ID + ":config." + (config.mod_enabled ? "enabled" : "disabled"))).withColor(config.mod_enabled ? Colors.GREEN : Colors.RED));
        TetrisConfig.saveConfig();
    }

    private void toggleRandomTextureEnabled(ButtonWidget buttonWidget) {
        config.tetris_random_textures = !config.tetris_random_textures;
        buttonWidget.setMessage(Text.translatable(TetrisMC.MOD_ID + ":tetris.random_texture").append(" ").append(Text.translatable(TetrisMC.MOD_ID + ":config." + (config.tetris_random_textures ? "enabled" : "disabled"))).withColor(config.tetris_random_textures ? enableColour : disableColour));
        TetrisConfig.saveConfig();
    }

    private void closeScreen() {
        this.client.setScreen(this.parent);
    }
}
