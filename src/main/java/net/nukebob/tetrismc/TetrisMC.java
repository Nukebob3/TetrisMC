package net.nukebob.tetrismc;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TetrisMC implements ClientModInitializer {
	public static final String MOD_ID = "tetrismc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Random RANDOM = Random.create();

	@Override
	public void onInitializeClient() {

	}
}