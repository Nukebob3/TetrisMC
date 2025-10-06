package net.nukebob.tetrismc.game.tetris;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.TetrisMC;

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

    public void draw(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(TetrisMC.MOD_ID, "animation/" + animation + "/" + (int) frame + ".png"), x, y, 0, 0, width, height, width, height);
        }

    public void draw(DrawContext context, int x, int y) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(TetrisMC.MOD_ID, "animation/" + animation + "/" + (int) frame + ".png"), x, y, 0, 0, width, height, width, height);
    }
}
