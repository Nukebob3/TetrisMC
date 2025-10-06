package net.nukebob.tetrismc.game.tetris.mino;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;

import java.awt.*;

public class Block {
    public int x, y;
    public static int SIZE = 16;
    public Identifier texture;
    public String mino;
    public float destroying;

    public Block (Identifier t, String mino) {
        this.texture = t;
        this.destroying = -1;
        this.mino = mino;
    }

    public void draw(DrawContext context) {
        Identifier texture = this.texture;
        if (!TetrisConfig.loadConfig().tetris_random_textures) {
            texture = getDefaultTexture();
        }

        Color color = new Color(1F, 1F, 1F, destroying==-1?1:1 - ((int) destroying * 0.1f));
        Identifier atlas = Identifier.ofVanilla("textures/atlas/blocks.png");
        Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(atlas).apply(texture);
        context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, TetrisScreen.left_x + x, TetrisScreen.top_y + y, Block.SIZE, Block.SIZE, color.getRGB());
        if ((int) destroying != -1) {
            context.drawTexture(RenderLayer::getGuiTextured, Identifier.of("textures/block/destroy_stage_" + (int) destroying + ".png"), TetrisScreen.left_x + x, TetrisScreen.top_y + y, 0, Block.SIZE * (int) (TetrisScreen.animation / 30f), Block.SIZE, Block.SIZE, Block.SIZE, Block.SIZE);
        }
    }

    public void draw(DrawContext context, int yOffset) {
        Identifier texture = this.texture;
        if (!TetrisConfig.loadConfig().tetris_random_textures) {
            texture = getDefaultTexture();
        }

        Identifier atlas = Identifier.ofVanilla("textures/atlas/blocks.png");
        Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(atlas).apply(texture);
        context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, TetrisScreen.left_x + x, TetrisScreen.top_y + y + yOffset, Block.SIZE, Block.SIZE, new Color(1, 1, 1, 0.3f).getRGB());
    }

    private Identifier getDefaultTexture() {
        return switch (mino) {
            case "square" -> Identifier.ofVanilla("block/gold_block");
            case "bar" -> Identifier.ofVanilla("block/diamond_block");
            case "t" -> Identifier.ofVanilla("block/amethyst_block");
            case "l1" -> Identifier.ofVanilla("block/copper_block");
            case "l2" -> Identifier.ofVanilla("block/lapis_block");
            case "z1" -> Identifier.ofVanilla("block/redstone_block");
            case "z2" -> Identifier.ofVanilla("block/emerald_block");
            default -> Identifier.ofVanilla("block/iron_block");
        };
    }
}
