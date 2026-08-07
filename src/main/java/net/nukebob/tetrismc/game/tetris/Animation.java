package net.nukebob.tetrismc.game.tetris;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import org.jetbrains.annotations.NotNull;

public class Animation {
    public int x;
    public int y;
    public int width;
    public int height;
    public String animation;
    public int frames;
    public float frame;

    public Animation(int x, int y, int width, int height, String animation, int frames) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animation = animation;
        this.frames = frames;
        this.frame = 0;
    }

    public void draw(GuiGraphicsExtractor graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "animation/" + animation + "/" + (int) frame + ".png"), x, y, 0, 0, width, height, width, height);
    }

    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "animation/" + animation + "/" + (int) frame + ".png"), x, y, 0, 0, width, height, width, height);
    }
}
