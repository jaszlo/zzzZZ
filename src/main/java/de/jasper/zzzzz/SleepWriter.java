package de.jasper.zzzzz;

import de.jasper.zzzzz.gui.EpilogueMessageSelectionScreen;
import de.jasper.zzzzz.gui.PrologMessageSelectionScreen;
import de.jasper.zzzzz.gui.ZzzzzSettings;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SleepWriter {

    static boolean alreadyWritten = false;
    // Wait for last tick to set it to false
    static long lastWritten = 0;

    // Minimum length of Z's
    static int minLength = 6;
    static int maxLength = 12;

    public static void writeProlog() {
        if (ZzzzzSettings.usePrologText.getValue()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(ZzzzzSettings.prologText.getText());
        }

        if (ZzzzzSettings.useRandomPrologMessage.getValue()) {
            int size = PrologMessageSelectionScreen.messageList.size();
            if (size > 0) {
                String message = PrologMessageSelectionScreen.messageList.get(new Random().nextInt(size));
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.networkHandler.sendChatMessage(message);
            }
        }

        // Once cool down is never reached. Therefore, print it in prolog which is always called when going to sleep
        if (ZzzzzSettings.useZZZ.getValue() && ZzzzzSettings.frequency.getValue().equals(ZzzzzSettings.Frequency.ONCE)) {
            writeZZZZZInternal();
        }
    }

    public static void writeEpilogue() {
        if (ZzzzzSettings.useEpilogueText.getValue()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage(ZzzzzSettings.epilogueText.getText());
        }

        if (ZzzzzSettings.useRandomPrologMessage.getValue()) {
            int size = EpilogueMessageSelectionScreen.messageList.size();
            if (size > 0) {
                String message = EpilogueMessageSelectionScreen.messageList.get(new Random().nextInt(size));
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.networkHandler.sendChatMessage(message);
            }
        }
    }

    private static void writeZZZZZInternal() {
        MinecraftClient client = MinecraftClient.getInstance();
        Random r = new Random();
        // Repeat random amount of small and random Z's
        // Reach MinLength/MaxLength by reducing/increasing Z_Amount
        int z_Amount = r.nextInt(5) + 1;
        int Z_Amount = r.nextInt(maxLength - z_Amount) + (minLength - z_Amount); // 1-8 but so that it fits min/max range
        String output = "z".repeat(z_Amount) + "Z".repeat(Z_Amount);
        lastWritten = System.currentTimeMillis();
        alreadyWritten = true;
        assert client.player != null;
        client.player.networkHandler.sendChatMessage(output);
    }

    public static void writeZZZZZ() {
        // If Once is enabled this will be printed in the prologue for easier implementation
        if (!ZzzzzSettings.useZZZ.getValue() || ZzzzzSettings.frequency.getValue().equals(ZzzzzSettings.Frequency.ONCE)) {
            return;
        }

        if (alreadyWritten) {
            long now = System.currentTimeMillis();
            if (now - lastWritten > ZzzzzSettings.frequency.getValue().toDelta()) {
                alreadyWritten = false;
            }
        } else {
            writeZZZZZInternal();
        }
    }
}
