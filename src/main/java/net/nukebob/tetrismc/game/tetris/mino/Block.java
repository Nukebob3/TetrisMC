package net.nukebob.tetrismc.game.tetris.mino;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.MutableText;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Block {

    public int x, y;
    public static int SIZE = 16;
    public Identifier texture;
    public MutableText name;
    public String mino;
    public float destroying;

    public Block(Identifier texture, MutableText name, String mino) {
        this.texture = texture;
        this.name = name;
        this.destroying = -1;
        this.mino = mino;
    }

    public void draw(@NotNull DrawContext context) {
        Identifier drawTexture = (TetrisConfig.loadConfig().tetris_random_textures) ? texture : getDefaultTexture();

        Color color = new Color(1F, 1F, 1F, destroying == -1 ? 1 : 1 - ((int) destroying * 0.1f));
        Sprite sprite = MinecraftClient.getInstance().getAtlasManager().getAtlasTexture(Atlases.BLOCKS).getSprite(drawTexture);
        context.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, sprite, TetrisScreen.leftX + x, TetrisScreen.topY + y, Block.SIZE, Block.SIZE, color.getRGB());
        if ((int) destroying != -1) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("textures/block/destroy_stage_" + (int) destroying + ".png"), TetrisScreen.leftX + x, TetrisScreen.topY + y, 0, Block.SIZE * (int) (TetrisScreen.animation / 30f), Block.SIZE, Block.SIZE, Block.SIZE, Block.SIZE);
        }

        if (TetrisScreen.paused && TetrisScreen.active) {
            double mouseX = MinecraftClient.getInstance().mouse.getX() / MinecraftClient.getInstance().getWindow().getScaleFactor();
            double mouseY = MinecraftClient.getInstance().mouse.getY() / MinecraftClient.getInstance().getWindow().getScaleFactor();

            if (mouseX >= TetrisScreen.leftX + x && mouseX < TetrisScreen.leftX + x + SIZE && mouseY >= TetrisScreen.topY + y && mouseY < TetrisScreen.topY + y + SIZE) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, name, (int) mouseX, (int) mouseY);
            }
        }
    }

    public void draw(@NotNull DrawContext context, int yOffset) {
        Identifier drawTexture = (TetrisConfig.loadConfig().tetris_random_textures) ? texture : getDefaultTexture();

        Sprite sprite = MinecraftClient.getInstance().getAtlasManager().getAtlasTexture(Atlases.BLOCKS).getSprite(drawTexture);
        context.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, sprite, TetrisScreen.leftX + x, TetrisScreen.topY + y + yOffset, Block.SIZE, Block.SIZE, new Color(1, 1, 1, 0.3f).getRGB());
    }

    private @NotNull Identifier getDefaultTexture() {
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
