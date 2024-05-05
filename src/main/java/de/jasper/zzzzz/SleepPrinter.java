package de.jasper.zzzzz;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class SleepPrinter {

    static boolean alreadyPrinted = false;
    // Wait for last tick to set it to false
    static long lastPrinted = 0;

    // Minimum length of Z's
    static int minLength = 6;
    static int maxLength = 12;

    public static void printProlog() {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(ZzzzzSettings.prologText.getText());
        }

        // Once cool down is never reached. Therefore, print it in prolog which is always called when going to sleep
        if (ZzzzzSettings.frequency.getValue().equals(ZzzzzSettings.Frequency.ONCE)) {
            printZZZZZ();
        }
    }

    public static void printEpilogue() {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(ZzzzzSettings.epilogueText.getText());
        }
    }

    public static void printZZZZZ() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !ZzzzzSettings.useZZZ.getValue()) {
            return;
        }
        Random r = new Random();
        // Repeat random amount of small and random Z's
        // Reach MinLength/MaxLength by reducing/increasing Z_Amount
        int z_Amount = r.nextInt(5) + 1;
        int Z_Amount = r.nextInt(maxLength - z_Amount) + (minLength - z_Amount); // 1-8 but so that it fits min/max range
        String output = "z".repeat(z_Amount) + "Z".repeat(Z_Amount);
        lastPrinted = System.currentTimeMillis();
        alreadyPrinted = true;
        client.player.networkHandler.sendChatMessage(output);
    }

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!ZzzzzSettings.useMod.getValue() || client.player == null || !client.player.isSleeping()) {
               return;
            }

            if (alreadyPrinted) {
                long now = System.currentTimeMillis();
                if (now - lastPrinted > ZzzzzSettings.frequency.getValue().toDelta()) {
                    alreadyPrinted = false;
                }
            } else {
                printZZZZZ();
            }
        });
    }
}
