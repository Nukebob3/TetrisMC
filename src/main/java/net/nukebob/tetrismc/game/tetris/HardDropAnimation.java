package net.nukebob.tetrismc.game.tetris;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.game.tetris.mino.Block;
import net.nukebob.tetrismc.screen.TetrisScreen;

import java.awt.*;

public class HardDropAnimation extends Animation{
    public HardDropAnimation(int x, int y, int width, int height, int frames) {
        super(x, y, width, height, "hard_drop", frames);
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics) {
        Color color = new Color(1, 1, 1, (1 - frame * ((float) 1 / frames)) / 4);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "animation/hard_drop/0.png"), x + TetrisScreen.leftX - width / 2, y + TetrisScreen.topY + Block.SIZE, 0, 0, width, height, width, height, color.getRGB());
    }
}
