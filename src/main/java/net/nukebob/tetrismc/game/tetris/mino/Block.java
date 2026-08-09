package net.nukebob.tetrismc.game.tetris.mino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.screen.TetrisScreen;
import org.jetbrains.annotations.NotNull;

public class Block {

    public int x, y;
    public static final int SIZE = 16;
    public final Identifier texture;
    public MutableComponent name;
    public String mino;
    public float destroying;

    public Block(Identifier texture, MutableComponent name, String mino) {
        this.texture = texture;
        this.name = name;
        this.destroying = -1;
        this.mino = mino;
    }

    public void draw(@NotNull GuiGraphicsExtractor context) {
        Identifier drawTexture = resolveTexture();

        int color = destroying == -1 ? 0xFFFFFFFF : packAlpha(1f - ((int) destroying * 0.1f));
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(drawTexture);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, TetrisScreen.leftX + x, TetrisScreen.topY + y, Block.SIZE, Block.SIZE, color);
        if ((int) destroying != -1) {
            context.blit(RenderPipelines.GUI_TEXTURED, Identifier.parse("textures/block/destroy_stage_" + (int) destroying + ".png"), TetrisScreen.leftX + x, TetrisScreen.topY + y, 0, 0, Block.SIZE, Block.SIZE, Block.SIZE, Block.SIZE);
        }

        if (TetrisScreen.paused && TetrisScreen.active) {
            double mouseX = Minecraft.getInstance().mouseHandler.xpos() / Minecraft.getInstance().getWindow().getGuiScale();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos() / Minecraft.getInstance().getWindow().getGuiScale();

            if (mouseX >= TetrisScreen.leftX + x && mouseX < TetrisScreen.leftX + x + SIZE && mouseY >= TetrisScreen.topY + y && mouseY < TetrisScreen.topY + y + SIZE) {
                context.setTooltipForNextFrame(Minecraft.getInstance().font, name, (int) mouseX, (int) mouseY);
            }
        }
    }

    public void draw(@NotNull GuiGraphicsExtractor context, int yOffset) {
        Identifier drawTexture = resolveTexture();

        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(drawTexture);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, TetrisScreen.leftX + x, TetrisScreen.topY + y + yOffset, Block.SIZE, Block.SIZE, 0x4CFFFFFF);
    }

    private static int packAlpha(float alpha) {
        int a = (int) (Mth.clamp(alpha, 0, 1) * 255f) << 24;
        return a | 0xFFFFFF;
    }

    private Identifier resolveTexture() {
        return TetrisMC.CONFIG.tetris_random_textures ? texture : getDefaultTexture();
    }

    private @NotNull Identifier getDefaultTexture() {
        return switch (mino) {
            case "square" -> Identifier.withDefaultNamespace("block/gold_block");
            case "bar" -> Identifier.withDefaultNamespace("block/diamond_block");
            case "t" -> Identifier.withDefaultNamespace("block/amethyst_block");
            case "l1" -> Identifier.withDefaultNamespace("block/copper_block");
            case "l2" -> Identifier.withDefaultNamespace("block/lapis_block");
            case "z1" -> Identifier.withDefaultNamespace("block/redstone_block");
            case "z2" -> Identifier.withDefaultNamespace("block/emerald_block");
            default -> Identifier.withDefaultNamespace("block/iron_block");
        };
    }
}
