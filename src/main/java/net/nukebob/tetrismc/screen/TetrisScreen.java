package net.nukebob.tetrismc.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.CommonColors;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.game.HighScores;
import net.nukebob.tetrismc.game.tetris.Animation;
import net.nukebob.tetrismc.game.tetris.mino.*;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisScreen extends Screen {

    public static int dropInterval = 60;
    static final int GRID_X = 10;
    static final int GRID_Y = 16;
    public static final int TETRIS_WIDTH = Block.SIZE * GRID_X;
    public static final int TETRIS_HEIGHT = Block.SIZE * GRID_Y;

    int levelLength = 5;

    public static final int NEXT_WIDTH = Block.SIZE * 4;
    public static final int NEXT_HEIGHT = Block.SIZE * 5;

    public static int leftX;
    public static int rightX;
    public static int topY;
    public static int bottomY;

    public static boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed, paused, active = false;
    public static int hardDrop;

    public static ArrayList<Block> staticBlocks = new ArrayList<>();
    public static ArrayList<Block> destroying = new ArrayList<>();

    public static ArrayList<Animation> animations = new ArrayList<>();

    public static int score = 0;
    public static int linesCleared = 0;
    public static int level = 0;
    public static int combo = 0;

    public static boolean isNewHighScore = false;

    public static Component onScreenText;
    public static int onScreenTextColour;
    public static int onScreenTextOpacity = 0;

    public static float animation = 0;

    public final Screen parent;

    public static Mino currentMino;
    public static Mino nextMino;

    private final TetrisConfig config = TetrisConfig.loadConfig();

    public TetrisScreen(Screen parent) {
        super(Component.nullToEmpty("Tetris Screen"));
        this.parent = parent;

        this.init();
    }

    Button playButton = Button.builder(Component.translatable(TetrisMC.MOD_ID + ":game.start").withColor(CommonColors.YELLOW), button -> reset()).build();

    @Override
    protected void init() {
        //main play area frame
        leftX = this.width / 2 - TETRIS_WIDTH / 2;
        rightX = leftX + TETRIS_WIDTH;
        topY = this.height / 2 - TETRIS_HEIGHT / 2;
        bottomY = topY + TETRIS_HEIGHT;

        paused = true;

        hardDrop = config.tetris_hard_drop;

        Button returnButton = SpriteIconButton.builder(Component.empty(), button -> this.minecraft.setScreenAndShow(this.parent), true).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/return"), 15, 15).build();
        returnButton.setTooltip(Tooltip.create(Component.translatable(TetrisMC.MOD_ID + ":game.return")));
        returnButton.setRectangle(20, 20, 20, 20);
        Button restartButton = SpriteIconButton.builder(Component.empty(), button -> gameOver(), true).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/restart"), 15, 15).build();
        restartButton.setTooltip(Tooltip.create(Component.translatable(TetrisMC.MOD_ID + ":game.restart")));
        restartButton.setRectangle(20, 20, 45, 20);
        Button pauseButton = SpriteIconButton.builder(Component.empty(), button -> paused = !paused, true).sprite(Identifier.fromNamespaceAndPath(TetrisMC.MOD_ID, "icon/pause"), 15, 15).build();
        pauseButton.setTooltip(Tooltip.create(Component.translatable(TetrisMC.MOD_ID + ":game.pause")));
        pauseButton.setRectangle(20, 20, 70, 20);

        playButton.setPosition(this.width / 2 - 150 / 2, this.height / 2 - 20 / 2);
        playButton.setSize(150, 20);

        this.addRenderableWidget(returnButton);
        this.addRenderableWidget(restartButton);
        this.addRenderableWidget(pauseButton);
        this.addRenderableWidget(playButton);
    }

    public void reset() {
        score = 0;
        linesCleared = 0;
        level = 0;
        combo = 0;
        dropInterval = 60;
        onScreenTextOpacity = 60;
        onScreenTextColour = 0;
        onScreenText = Component.empty();
        animation = (animation % 30) * 10;
        isNewHighScore = false;

        paused = false;
        active = true;

        staticBlocks = new ArrayList<>();
        destroying = new ArrayList<>();
        animations = new ArrayList<>();
        currentMino = pickMino();
        currentMino.setXY(TETRIS_WIDTH / 2, Block.SIZE);

        leftPressed = rightPressed = upPressed = downPressed = spacePressed = false;

        nextMino = pickMino();
        nextMino.setXY(TETRIS_WIDTH + Block.SIZE * 2 + (nextMino instanceof MinoL2 || nextMino instanceof MinoZ1 ? Block.SIZE : (nextMino instanceof MinoT ? Block.SIZE / 2 : 0)), TETRIS_HEIGHT - (int) (Block.SIZE * 2.5f));
    }

    public void manager() {
        if (currentMino == null) {
            reset();
        }
        if (!currentMino.active) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace("block.stone.place")), 1.5F, 5.0f * config.tetris_volume));
            score += 10;

            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            int lines = 0;
            if (checkClear(currentMino.b[0].y)) lines++;
            if (checkClear(currentMino.b[1].y)) lines++;
            if (checkClear(currentMino.b[2].y)) lines++;
            if (checkClear(currentMino.b[3].y)) lines++;
            int height = 108;
            int width = 192;
            if (lines > 0) {
                combo++;
                if (combo > 1) {
                    onScreenText = Component.translatable(TetrisMC.MOD_ID + ":tetris.combo").append(" x" + combo);
                    onScreenTextOpacity = 30;
                    onScreenTextColour = (combo > 3 ? CommonColors.RED : (combo > 2 ? CommonColors.YELLOW : CommonColors.WHITE));
                    score += 50 * (combo - 1);
                }
            } else combo = 0;
            switch (lines) {
                case 1:
                    score += 100;
                    break;
                case 2:
                    score += 300;
                    break;
                case 3:
                    score += 500;
                    break;
                case 4:
                    score += 800;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace("entity.generic.explode")), 0.8F, 5.0f * config.tetris_volume));
                    animations.add(new Animation(this.width / 2 - width / 2, currentMino.b[2].y, width, height, "explosion", 20));
                    onScreenText = Component.translatable(TetrisMC.MOD_ID + ":tetris.tetris");
                    onScreenTextColour = 11141290;
                    onScreenTextOpacity = 30;
                    break;
            }

            switch (level) {
                case 1:
                    dropInterval = 54;
                    break;
                case 2:
                    dropInterval = 48;
                    break;
                case 3:
                    dropInterval = 41;
                    break;
                case 4:
                    dropInterval = 35;
                    break;
                case 5:
                    dropInterval = 29;
                    break;
                case 6:
                    dropInterval = 22;
                    break;
                case 7:
                    dropInterval = 16;
                    break;
                case 8:
                    dropInterval = 10;
                    break;
                case 9:
                    dropInterval = 8;
                    break;
                case 10:
                    dropInterval = 6;
                    break;
            }

            for (Block b : currentMino.b) {
                if (b.y <= Block.SIZE * 2) {
                    gameOver();
                    return;
                }
            }

            currentMino = nextMino;
            currentMino.setXY(TETRIS_WIDTH / 2, Block.SIZE);

            nextMino = pickMino();
            nextMino.setXY(TETRIS_WIDTH + Block.SIZE * 2 + (nextMino instanceof MinoL2 || nextMino instanceof MinoZ1 ? Block.SIZE : (nextMino instanceof MinoT ? Block.SIZE / 2 : 0)), TETRIS_HEIGHT - (int) (Block.SIZE * 2.5f));
        }
        float frameDuration = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();
        currentMino.update(frameDuration * 3);
        animation += frameDuration * 3;
    }

    private void gameOver() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace("entity.pig.ambient")), 1.0F, 5.0f * config.tetris_volume));
        isNewHighScore = score > HighScores.loadHighScores().tetrisHighScore;
        active = false;
    }

    private boolean checkClear(int y) {
        int count = 0;
        for (Block block : staticBlocks) {
            if (block.y == y) count++;
        }
        if (count < GRID_X) {
            return false;
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace("block.deepslate.break")), 1.0F, 5.0f * config.tetris_volume));
        linesCleared++;
        for (Block block : staticBlocks) {
            if (block.y == y) {
                destroying.add(block);
            }
        }

        staticBlocks.removeIf(b -> b.y == y);
        for (Block block : staticBlocks) {
            if (block.y < y) block.y += Block.SIZE;
        }

        if (linesCleared % levelLength == 0) {
            level++;
        }
        return true;
    }

    private Mino pickMino() {
        Mino mino = null;
        int i = new Random().nextInt(7);
        mino = switch (i) {
            case 0 -> new MinoL1();
            case 1 -> new MinoL2();
            case 2 -> new MinoSquare();
            case 3 -> new MinoBar();
            case 4 -> new MinoT();
            case 5 -> new MinoZ1();
            case 6 -> new MinoZ2();
            default -> mino;
        };
        return mino;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (active) super.extractRenderState(graphics, mouseX, mouseY, delta);
        //called here as this is run quite frequently
        if (!paused && active) manager();
        if (paused) {
            upPressed = downPressed = leftPressed = rightPressed = spacePressed = false;
        }
        float scale = switch (Minecraft.getInstance().options.guiScale().get()) {
            case 4 -> 0.8f;
            case 1 -> 3f;
            case 2 -> 1.6f;
            default -> 1;
        };

        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        float offsetX = graphics.guiWidth() * (1 - scale) / (2f * scale);
        float offsetY = graphics.guiHeight() * (1 - scale) / (2f * scale);
        graphics.pose().translate(offsetX, offsetY);

        //draw border
        graphics.horizontalLine(leftX - 1, rightX, topY - 1 + Block.SIZE * 3, new Color(1, 0, 0, 0.3f).getRGB());
        graphics.outline(leftX - 1, topY - 1, TETRIS_WIDTH + 2, TETRIS_HEIGHT + 2, CommonColors.WHITE);

        //draw moving mino
        if (currentMino != null) {
            currentMino.draw(graphics);
            //draw hard drop
            if (hardDrop > 0) currentMino.drawHardDrop(graphics);
        }


        //draw next mino
        graphics.outline(rightX + Block.SIZE - 1, bottomY - NEXT_HEIGHT + 1, NEXT_WIDTH + 2, NEXT_HEIGHT, CommonColors.WHITE);
        Component nextText = Component.translatable(TetrisMC.MOD_ID + ":tetris.next");
        graphics.text(this.font, nextText, rightX + Block.SIZE * 2, bottomY - NEXT_HEIGHT + Block.SIZE / 2, CommonColors.WHITE, true);
        if (nextMino != null) nextMino.draw(graphics);

        //draw score
        Component scoreText = Component.translatable(TetrisMC.MOD_ID + ":tetris.score").append(": " + score);
        graphics.text(this.font, scoreText, rightX + Block.SIZE * 2, topY + Block.SIZE, CommonColors.WHITE, true);
        Component linesText = Component.translatable(TetrisMC.MOD_ID + ":tetris.lines").append(": " + linesCleared);
        graphics.text(this.font, linesText, rightX + Block.SIZE * 2, topY + Block.SIZE + 10, CommonColors.WHITE, true);

        //draw static minos
        for (Block block : staticBlocks) {
            block.draw(graphics);
        }

        //draw destroying minos
        for (Block d : destroying) {
            d.destroying += Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks() * 3;
            if (d.destroying > 9) d.destroying = 9;
            d.draw(graphics);
        }
        destroying.removeIf(d -> d.destroying >= 9);

        //draw explosions
        for (Animation a : animations) {
            if (a.animation.equals("explosion"))
                a.draw(graphics, this.width / 2 - a.width / 2, topY + a.y - a.height / 2);
            else a.draw(graphics);
            a.frame += 1f;
        }
        animations.removeIf(an -> an.frame > an.frames);

        //draw paused text
        Component pausedText = Component.translatable(TetrisMC.MOD_ID + ":tetris.paused");
        if (paused && active)
            graphics.text(this.font, pausedText, this.width / 2 - (3 * pausedText.getString().length()), this.height / 2 - 7, CommonColors.WHITE, true);

        //draw combo and tetris texts
        if (onScreenTextOpacity > 0) {
            Color base = new Color(onScreenTextColour, false);
            float alpha = Math.clamp(onScreenTextOpacity / 10f, 0, 1);
            Color color = new Color(base.getRed() / 255f, base.getGreen() / 255f, base.getBlue() / 255f, alpha);
            graphics.text(this.font, onScreenText, this.width / 2 - onScreenText.getString().length(), this.height / 2, color.getRGB(), true);
            onScreenTextOpacity--;
        }


        //draw play button if not active
        playButton.visible = !active;
        if (!active) {
            //Color black = new Color(Colors.BLACK);
            //graphics.fill(left_x - 1, top_y - 1, left_x - 1 + WIDTH + 2, top_y -1 + HEIGHT + 2, new Color(black.getRed(), black.getGreen(), black.getBlue(), 0.5f).getRGB());
            graphics.fill(leftX - 1, topY - 1, rightX+1, bottomY+1, 0xAA000000);
            super.extractRenderState(graphics, mouseX, mouseY, delta);
            if (currentMino != null) {
                Component finalScoreText = Component.translatable(TetrisMC.MOD_ID + ":tetris.score").append(": " + score).withColor(CommonColors.SOFT_YELLOW);
                graphics.text(this.font, finalScoreText, this.width / 2 - (finalScoreText.getString().length() * 3), this.height / 2 - 35, CommonColors.WHITE, true);
                Component linesClearedText = Component.translatable(TetrisMC.MOD_ID + ":tetris.lines").append(": " + linesCleared).withColor(CommonColors.SOFT_YELLOW);
                graphics.text(this.font, linesClearedText, this.width / 2 - (linesClearedText.getString().length() * 3), this.height / 2 - 25, CommonColors.WHITE, true);
                HighScores.loadHighScores();
                Component highScoreClearedText;
                if (score > HighScores.loadHighScores().tetrisHighScore) {
                    HighScores.loadHighScores().tetrisHighScore = score;
                    HighScores.saveHighScores();
                }
                highScoreClearedText = Component.translatable(isNewHighScore ? TetrisMC.MOD_ID + ":tetris.new_high_score" : TetrisMC.MOD_ID + ":tetris.high_score").append(": " + HighScores.loadHighScores().tetrisHighScore).withColor(CommonColors.YELLOW);
                if (HighScores.loadHighScores().tetrisHighScore > 0)
                    graphics.text(this.font, highScoreClearedText, this.width / 2 - (highScoreClearedText.getString().length() * 3), this.height / 2 + 25, CommonColors.WHITE, true);
            }
        }
        graphics.pose().popMatrix();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent input) {
        int keyCode = input.key();
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            switch (keyCode) {
                case 262, 68:
                    rightPressed = true;
                    break;
                case 263, 65:
                    leftPressed = true;
                    break;
                case 264, 83:
                    downPressed = true;
                    break;
                case 265, 87:
                    upPressed = true;
                    break;
                case 32:
                    spacePressed = true;
                    break;
                default:
                    return false;
            }
        }

        return false;
    }
}
