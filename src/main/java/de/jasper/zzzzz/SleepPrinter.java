package de.jasper.zzzzz;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.Random;

public class SleepPrinter {

    static boolean alreadyPrinted = false;
    static boolean prologPrinted = false;

    static long lastPrinted = 0;     //
    static long printCooldown = 500; // 1s

    // Minimum length of Z's
    static int minLength = 6;
    static int maxLength = 12;



    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.isSleeping()) {
                if (!prologPrinted && ZzzzzSettings.usePrologText.getValue()) {
                    client.player.networkHandler.sendChatMessage(ZzzzzSettings.prologText.getText());
                }

                if (alreadyPrinted) {
                    long now = System.currentTimeMillis();
                    if (now - lastPrinted > printCooldown) {
                        alreadyPrinted = false;
                    }
                } else {
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

            }

            if (client.player != null && !client.player.isSleeping()) {
                alreadyPrinted = false;
                if (ZzzzzSettings.useEpilogueText.getValue()) {
                    client.player.networkHandler.sendChatMessage(ZzzzzSettings.epilogueText.getText());
                }
            }
        });
    }
}
