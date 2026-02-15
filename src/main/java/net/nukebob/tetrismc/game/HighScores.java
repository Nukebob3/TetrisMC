package net.nukebob.tetrismc.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.nukebob.tetrismc.TetrisMC;
import net.nukebob.tetrismc.util.Encryption;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class HighScores {

    public int tetrisHighScore = 0;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File HIGH_SCORES_FILE = new File("config/" + TetrisMC.MOD_ID + "/high_scores.json");
    private static HighScores highScores;

    public static HighScores loadHighScores() {
        if (!HIGH_SCORES_FILE.exists()) {
            highScores = new HighScores();
            saveHighScores();
        } else {
            try (FileReader reader = new FileReader(HIGH_SCORES_FILE)) {
                char[] buffer = new char[(int) HIGH_SCORES_FILE.length()];
                reader.read(buffer);
                String encryptedContent = new String(buffer);

                String decryptedContent = Encryption.decrypt(encryptedContent);

                highScores = GSON.fromJson(decryptedContent, HighScores.class);
            } catch (Exception e) {
                TetrisMC.LOGGER.error("Could not load high scores file", e);
                highScores = new HighScores();
                saveHighScores();
            }
        }
        return highScores;
    }

    public static void saveHighScores() {
        if (!HIGH_SCORES_FILE.getParentFile().exists()) {
            HIGH_SCORES_FILE.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(HIGH_SCORES_FILE)) {
            String jsonContent = GSON.toJson(highScores);

            String encryptedContent = Encryption.encrypt(jsonContent);

            writer.write(encryptedContent);
        } catch (Exception e) {
            TetrisMC.LOGGER.error("Could not save high scores file", e);
        }
    }
}
