package de.jasper.zzzzz;

import de.jasper.zzzzz.gui.ZzzzzSettings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SleepTracker {


    public enum State {
        AWAKE,              // sleeping -> falling_asleep,  !sleeping -> awake
        FALLING_ASLEEP,     // sleeping -> sleeping,        !sleeping -> awake
        SLEEPING,           // sleeping -> sleeping,        !sleeping -> waking up
        WAKING_UP           // sleeping -> sleeping(?)      !sleeping -> awake
    }



    private static State state = State.AWAKE;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Check if player is in-game. If not reset state and do nothing else
            if (client.world == null || client.player == null) {
                state = State.AWAKE;
                return;
            }

            boolean sleeping = client.player.isSleeping();
            long timeOfDay = client.world.getTimeOfDay();
            boolean canGoToSleep = 12541 <= timeOfDay && timeOfDay <= 23460;

            // Update state accordingly
            switch (state) {
                case AWAKE:
                    if (sleeping) state = State.FALLING_ASLEEP;
                    break;
                case FALLING_ASLEEP:
                    if (sleeping) state = State.SLEEPING;
                    else state = State.AWAKE;
                    break;
                case SLEEPING:
                    // If no longer sleeping but also could not go to sleep it's the next day
                    if (!sleeping && !canGoToSleep) state = State.WAKING_UP;
                    // If no longer sleeping but also could sleep player canceled going sleeping
                    if (!sleeping && canGoToSleep) state = State.AWAKE;
                    break;
                case WAKING_UP:
                    if (!sleeping) state = State.AWAKE;
                    break;
            }

            // If mod is disabled do not act upon state!
            if (!ZzzzzSettings.useMod.getValue()) return;

            // Act upon state. Sleep printer itself will check if options are enabled or disabled!
            switch (state) {
                case AWAKE:
                    // Do Nothing
                    break;
                case FALLING_ASLEEP:
                    SleepWriter.writeProlog();
                    break;
                case SLEEPING:
                    SleepWriter.writeZZZZZ();
                    break;
                case WAKING_UP:
                    SleepWriter.writeEpilogue();
                    break;
            }
        });
    }

}
