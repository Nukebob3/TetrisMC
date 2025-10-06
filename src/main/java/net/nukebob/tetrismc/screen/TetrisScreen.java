package net.nukebob.tetrismc.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.config.TetrisConfig;
import net.nukebob.tetrismc.game.HighScores;
import net.nukebob.tetrismc.game.tetris.Animation;
import net.nukebob.tetrismc.game.tetris.mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TetrisScreen extends Screen {
    public static int dropInterval = 60;
    static final int gridX = 10;
    static final int gridY = 16;
    public static final int WIDTH = Block.SIZE * gridX;
    public static final int HEIGHT = Block.SIZE * gridY;

    int levelLength = 5;

    public static final int nextWIDTH = Block.SIZE * 4;
    public static final int nextHEIGHT = Block.SIZE * 5;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

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

    public static Text onScreenText;
    public static int onScreenTextColour;
    public static int onScreenTextOpacity = 0;

    public static float animation = 0;

    public final Screen parent;

    public static Mino currentMino;
    public static Mino nextMino;

    private final TetrisConfig config = TetrisConfig.loadConfig();

    public TetrisScreen(Screen parent) {
        super(Text.of("Tetris Screen"));
        this.parent = parent;

        this.init();
    }

    ButtonWidget playButton = ButtonWidget.builder(Text.translatable(TetrisMC.MOD_ID + ":game.start").withColor(Colors.YELLOW), button -> reset()).build();

    @Override
    protected void init() {
        //main play area frame
        left_x = this.width / 2 - WIDTH / 2;
        right_x = left_x + WIDTH;
        top_y = this.height / 2 - HEIGHT / 2;
        bottom_y = top_y + HEIGHT;

        paused = true;

        hardDrop = config.tetris_hard_drop;

        ButtonWidget returnButton = TextIconButtonWidget.builder(Text.empty(), button -> this.client.setScreen(this.parent), true)
                .texture(Identifier.of(TetrisMC.MOD_ID, "icon/return"), 15, 15).build();
        returnButton.setTooltip(Tooltip.of(Text.translatable(TetrisMC.MOD_ID + ":game.return")));
        returnButton.setDimensionsAndPosition(20, 20, 20, 20);
        ButtonWidget restartButton = TextIconButtonWidget.builder(Text.empty(), button -> gameOver(), true)
                .texture(Identifier.of(TetrisMC.MOD_ID, "icon/restart"), 15, 15).build();
        restartButton.setTooltip(Tooltip.of(Text.translatable(TetrisMC.MOD_ID + ":game.restart")));
        restartButton.setDimensionsAndPosition(20, 20, 45, 20);
        ButtonWidget pauseButton = TextIconButtonWidget.builder(Text.empty(), button -> paused = !paused, true)
                .texture(Identifier.of(TetrisMC.MOD_ID, "icon/pause"), 15, 15).build();
        pauseButton.setTooltip(Tooltip.of(Text.translatable(TetrisMC.MOD_ID + ":game.pause")));
        pauseButton.setDimensionsAndPosition(20, 20, 70, 20);

        playButton.setPosition(this.width / 2 - 150 / 2, this.height / 2 - 20 / 2);
        playButton.setDimensions(150, 20);

        this.addDrawableChild(returnButton);
        this.addDrawableChild(restartButton);
        this.addDrawableChild(pauseButton);
        this.addDrawableChild(playButton);
    }

    public void reset() {
        score = 0;
        linesCleared = 0;
        level = 0;
        combo = 0;
        dropInterval = 60;
        onScreenTextOpacity = 60;
        onScreenTextColour = 0;
        onScreenText = Text.empty();
        animation = (animation % 30) * 10;
        isNewHighScore = false;

        paused = false;
        active = true;

        staticBlocks = new ArrayList<>();
        destroying = new ArrayList<>();
        animations = new ArrayList<>();
        currentMino = pickMino();
        currentMino.setXY(WIDTH / 2, Block.SIZE);

        leftPressed = rightPressed = upPressed = downPressed = spacePressed = false;

        nextMino = pickMino();
        nextMino.setXY(WIDTH + Block.SIZE * 2 +
                        (nextMino instanceof Mino_L2 || nextMino instanceof Mino_Z1 ? Block.SIZE : (nextMino instanceof Mino_T ? Block.SIZE / 2 : 0)),
                HEIGHT - (int) (Block.SIZE * 2.5f));
    }

    public void manager() {
        if (currentMino == null) {
            reset();
        }
        if (!currentMino.active) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(Identifier.ofVanilla("block.stone.place")), 1.5F, 5.0f * config.tetris_volume));
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
                    onScreenText = Text.translatable(TetrisMC.MOD_ID + ":tetris.combo").append(" x"+combo);
                    onScreenTextOpacity = 30;
                    onScreenTextColour = (combo > 3 ? Colors.RED : (combo > 2 ? Colors.YELLOW : Colors.WHITE));
                    score += 50 * (combo - 1);
                }
            } else combo = 0;
            switch (lines) {
                case 1: score += 100; break;
                case 2: score += 300; break;
                case 3: score += 500; break;
                case 4: score += 800;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(Identifier.ofVanilla("entity.generic.explode")), 0.8F, 5.0f * config.tetris_volume));
                    animations.add(new Animation(this.width/2 - width/2, currentMino.b[2].y, width, height, "explosion", 20));
                    onScreenText = Text.translatable(TetrisMC.MOD_ID + ":tetris.tetris");
                    onScreenTextColour = 11141290;
                    onScreenTextOpacity = 30;
                    break;
            }

            switch (level) {
                case 1: dropInterval = 54; break;
                case 2: dropInterval = 48; break;
                case 3: dropInterval = 41; break;
                case 4: dropInterval = 35; break;
                case 5: dropInterval = 29; break;
                case 6: dropInterval = 22; break;
                case 7: dropInterval = 16; break;
                case 8: dropInterval = 10; break;
                case 9: dropInterval = 8; break;
                case 10: dropInterval = 6; break;
            }

            for (Block b : currentMino.b) {
                if (b.y <= Block.SIZE * 2) {
                    gameOver();
                    return;
                }
            }

            currentMino = nextMino;
            currentMino.setXY(WIDTH / 2, Block.SIZE);

            nextMino = pickMino();
            nextMino.setXY(WIDTH + Block.SIZE * 2 +
                            (nextMino instanceof Mino_L2 || nextMino instanceof Mino_Z1 ? Block.SIZE : (nextMino instanceof Mino_T ? Block.SIZE / 2 : 0)),
                    HEIGHT - (int) (Block.SIZE * 2.5f));
        }
        float frameDuration = MinecraftClient.getInstance().getRenderTickCounter().getDynamicDeltaTicks();
        currentMino.update(frameDuration*3);
        animation+=frameDuration*3;
    }

    private void gameOver() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(Identifier.ofVanilla("entity.pig.ambient")), 1.0F, 5.0f * config.tetris_volume));
        isNewHighScore = score > HighScores.loadHighScores().tetrisHighScore;
        active = false;
    }

    private boolean checkClear(int y) {
        int count = 0;
        for (Block block : staticBlocks) {
            if (block.y == y) count++;
        }
        if (count < gridX) {
            return false;
        }
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvent.of(Identifier.ofVanilla("block.deepslate.break")), 1.0F, 5.0f * config.tetris_volume));
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
            case 0 -> new Mino_L1();
            case 1 -> new Mino_L2();
            case 2 -> new Mino_Square();
            case 3 -> new Mino_Bar();
            case 4 -> new Mino_T();
            case 5 -> new Mino_Z1();
            case 6 -> new Mino_Z2();
            default -> mino;
        };
        return mino;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (active) super.render(context, mouseX, mouseY, delta);
        //called here as this is run quite frequently
        if (!paused && active) manager();
        if (paused) {
            upPressed = downPressed = leftPressed = rightPressed = spacePressed = false;
        }
        float scale = switch (MinecraftClient.getInstance().options.getGuiScale().getValue()) {
            case 4 -> 0.8f;
            case 1 -> 3f;
            case 2 -> 1.6f;
            default -> 1;
        };
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale, scale);
        float offsetX = context.getScaledWindowWidth() * (1 - scale) / (2f * scale);
        float offsetY = context.getScaledWindowHeight() * (1 - scale) / (2f * scale);
        context.getMatrices().translate(offsetX, offsetY);

        //draw border

        context.drawHorizontalLine(left_x - 1, right_x, top_y - 1 + Block.SIZE * 3, new Color(1, 0, 0, 0.3f).getRGB());
        context.drawBorder(left_x - 1, top_y - 1, WIDTH + 2, HEIGHT + 2, Colors.WHITE);

        //draw moving mino
        if (currentMino!= null) {
            currentMino.draw(context);
            //draw hard drop
            if (hardDrop > 0) currentMino.drawHardDrop(context);
        }


        //draw next mino
        context.drawBorder(right_x + Block.SIZE - 1, bottom_y - nextHEIGHT + 1, nextWIDTH + 2, nextHEIGHT, Colors.WHITE);
        Text nextText = Text.translatable(TetrisMC.MOD_ID + ":tetris.next");
        context.drawText(this.textRenderer, nextText, right_x + Block.SIZE * 2,
                bottom_y - nextHEIGHT + Block.SIZE/2, Colors.WHITE, true);
        if (nextMino!= null) nextMino.draw(context);

        //draw score
        Text scoreText = Text.translatable(TetrisMC.MOD_ID + ":tetris.score").append(": " + score);
        context.drawText(this.textRenderer, scoreText, right_x + Block.SIZE * 2,
                top_y + Block.SIZE, Colors.WHITE, true);
        Text linesText = Text.translatable(TetrisMC.MOD_ID + ":tetris.lines").append(": " + linesCleared);
        context.drawText(this.textRenderer, linesText, right_x + Block.SIZE * 2,
                top_y + Block.SIZE + 10, Colors.WHITE, true);

        //draw static minos
        for (Block block : staticBlocks) {
            block.draw(context);
        }

        //draw destroying minos
        for (Block d : destroying) {
            d.destroying += MinecraftClient.getInstance().getRenderTickCounter().getDynamicDeltaTicks()*3;
            if (d.destroying>9) d.destroying = 9;
            d.draw(context);
        }
        destroying.removeIf(d -> d.destroying >= 9);

        //draw explosions
        for (Animation a : animations) {
            if (a.animation.equals("explosion")) a.draw(context, this.width/2 - a.width/2, top_y + a.y - a.height/2);
            else a.draw(context);
            a.frame += 1f;
        }
        animations.removeIf(an -> an.frame > an.frames);

        //draw paused text
        Text pausedText = Text.translatable(TetrisMC.MOD_ID + ":tetris.paused");
        if (paused&&active) context.drawText(this.textRenderer, pausedText, this.width / 2 - (3 * pausedText.getString().length()),
                this.height / 2 - 7, Colors.WHITE, true);
        
        //draw combo and tetris texts
        if (onScreenTextOpacity > 0) {
            Color base = new Color(onScreenTextColour, false);
            float alpha = Math.clamp(onScreenTextOpacity/10f,0,1);
            Color color = new Color(base.getRed()/255f, base.getGreen()/255f, base.getBlue()/255f, alpha);
            context.drawText(this.textRenderer, onScreenText, this.width / 2 - onScreenText.getString().length(), this.height / 2, color.getRGB(), true);
            onScreenTextOpacity--;
        }


        //draw play button if not active
        playButton.visible = !active;
        if (!active) {
            //Color black = new Color(Colors.BLACK);
            //context.fill(left_x - 1, top_y - 1, left_x - 1 + WIDTH + 2, top_y -1 + HEIGHT + 2, new Color(black.getRed(), black.getGreen(), black.getBlue(), 0.5f).getRGB());
            super.render(context, mouseX, mouseY, delta);
            if (currentMino != null) {
                Text finalScoreText = Text.translatable(TetrisMC.MOD_ID + ":tetris.score").append(": " + score).withColor(Colors.LIGHT_YELLOW);
                context.drawText(this.textRenderer, finalScoreText, this.width / 2 - (finalScoreText.getString().length() * 3),
                        this.height / 2 - 35, Colors.WHITE, true);
                Text linesClearedText = Text.translatable(TetrisMC.MOD_ID + ":tetris.lines").append(": " + linesCleared).withColor(Colors.LIGHT_YELLOW);
                context.drawText(this.textRenderer, linesClearedText, this.width / 2 - (linesClearedText.getString().length() * 3),
                        this.height / 2 - 25, Colors.WHITE, true);
                HighScores.loadHighScores();
                Text highScoreClearedText;
                if (score > HighScores.loadHighScores().tetrisHighScore) {
                    HighScores.loadHighScores().tetrisHighScore = score;
                    HighScores.saveHighScores();
                }
                highScoreClearedText = Text.translatable(isNewHighScore ? TetrisMC.MOD_ID + ":tetris.new_high_score" : TetrisMC.MOD_ID + ":tetris.high_score").append(": " + HighScores.loadHighScores().tetrisHighScore).withColor(Colors.YELLOW);
                if (HighScores.loadHighScores().tetrisHighScore > 0) context.drawText(this.textRenderer, highScoreClearedText, this.width / 2 - (highScoreClearedText.getString().length() * 3),
                        this.height / 2 + 25, Colors.WHITE, true);
            }
        }
        context.getMatrices().popMatrix();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        } else {
            switch (keyCode) {
                case 262, 68: rightPressed = true; break;
                case 263, 65: leftPressed = true; break;
                case 264, 83: downPressed = true; break;
                case 265, 87: upPressed = true; break;
                case 32: spacePressed = true; break;
            }
        }

        return false;
    }
}
