package de.jasper.zzzzz.gui;

import de.jasper.zzzzz.ModInit;
import de.jasper.zzzzz.gui.components.OptionButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * zzzZZ option screen to configure settings
 */
public class ZzzzzSettings extends GameOptionsScreen {

    public enum Frequency {
        ONCE,
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH,
        SPAM;

        @Override
        public String toString() {
            return switch (this) {
                case ONCE -> "once";
                case VERY_LOW -> "very_low";
                case LOW -> "low";
                case MEDIUM -> "medium";
                case HIGH -> "high";
                case VERY_HIGH -> "very_high";
                case SPAM -> "spam";
            };
        }

        public Text toText() {
            return switch (this) {
                case ONCE -> Text.of("Once");
                case VERY_LOW -> Text.of("Very Low");
                case LOW -> Text.of("Low");
                case MEDIUM -> Text.of("Medium");
                case HIGH -> Text.of("High");
                case VERY_HIGH -> Text.of("Very High");
                case SPAM -> Text.of("Spam");
            };
        }

        public static Frequency fromString(String s) {
            return switch (s) {
                case "once" -> ONCE;
                case "very_low" -> VERY_LOW;
                case "low" -> LOW;
                case "medium" -> MEDIUM;
                case "high" -> HIGH;
                case "very_high" -> VERY_HIGH;
                case "spam" -> SPAM;
                default -> null;
            };
        }

        // Returns milliseconds between zzzZZ output
        public long toDelta() {
            return switch (this) {
                case ONCE -> Integer.MAX_VALUE;
                case VERY_LOW -> 2000L;
                case LOW -> 1000L;
                case MEDIUM -> 500L;
                case HIGH -> 250L;
                case VERY_HIGH -> 100L;
                case SPAM -> 50L;
            };
        }
    }

    private static ClickableWidget[] useModChildren = new ClickableWidget[10];

    public static final File PROLOG_FILE = new File(String.valueOf(Path.of(ModInit.MESSAGES_FOLDER_PATH, "zzzzProlog")));
    public static final File EPILOGUE_FILE = new File(String.valueOf(Path.of(ModInit.MESSAGES_FOLDER_PATH, "zzzzEpilog")));

    public static final String PROLOG_MESSAGES_FILE = "zzzzz_prolog_messages.txt";
    public static final String EPILOGUE_MESSAGES_FILE = "zzzzz_epilogue_messages.txt";

    public static final String DEFAULT_PROLOG = "I go sleep sleep :)";
    public static final String DEFAULT_EPILOGUE = "Go' Mornin' Frien's :)";

    public static OptionButton<Boolean> useMod = new OptionButton<>(
        true,
        OptionButton.BOOLEAN_VALUES,
        "zzzzz.option.useMod",
        Object::toString,
        Boolean::parseBoolean,
        (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static OptionButton<Boolean> useZZZ = new OptionButton<>(
        true,
        OptionButton.BOOLEAN_VALUES,
        "zzzzz.option.useZZZ",
        Object::toString,
        Boolean::parseBoolean,
        (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static OptionButton<Frequency> frequency = new OptionButton<>(
            Frequency.MEDIUM,
            Frequency.values(),
            "zzzzz.option.frequency",
            Frequency::toString,
            Frequency::fromString,
            Frequency::toText

    );

    public static OptionButton<Boolean> usePrologText = new OptionButton<>(
        false,
        OptionButton.BOOLEAN_VALUES,
        "zzzzz.option.usePrologText",
        Object::toString,
        Boolean::parseBoolean,
        (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static OptionButton<Boolean> useEpilogueText = new OptionButton<>(
            false,
            OptionButton.BOOLEAN_VALUES,
            "zzzzz.option.useEpilogueText",
            Object::toString,
            Boolean::parseBoolean,
            (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );


    public static ButtonWidget openPrologMessageSelection = ButtonWidget.builder(
            Text.translatable("zzzzz.option.openPrologMessageSelection"),
            (button) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                client.setScreen(new PrologMessageSelectionScreen(Text.translatable("zzzzz.messageSelectionScreen.prolog.title"), client.currentScreen, PROLOG_MESSAGES_FILE));
            }
    ).build();

    public static OptionButton<Boolean> useRandomPrologMessage = new OptionButton<>(
            false,
            OptionButton.BOOLEAN_VALUES,
            "zzzzz.option.useRandomPrologMessage",
            Object::toString,
            Boolean::parseBoolean,
            (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static ButtonWidget openEpilogueMessageSelection = ButtonWidget.builder(
            Text.translatable("zzzzz.option.openEpilogueMessageSelection"),
            (button) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                client.setScreen(new EpilogueMessageSelectionScreen(Text.translatable("zzzzz.messageSelectionScreen.epilogue.title"), client.currentScreen, EPILOGUE_MESSAGES_FILE));
            }
    ).build();

    public static OptionButton<Boolean> useRandomEpilogueMessage = new OptionButton<>(
            false,
            OptionButton.BOOLEAN_VALUES,
            "zzzzz.option.useRandomEpilogueMessage",
            Object::toString,
            Boolean::parseBoolean,
            (bool) -> (bool ? ScreenTexts.ON : ScreenTexts.OFF)
    );

    public static TextFieldWidget prologText;
    public static TextFieldWidget epilogueText;

    public ZzzzzSettings(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("zzzzz.option.title"));
    }

    private static void storeText(boolean isProlog) {
        File file = isProlog ? PROLOG_FILE : EPILOGUE_FILE;
        String toStore = isProlog ? prologText.getText() : epilogueText.getText();
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(toStore);
            fw.close();
        } catch (IOException e) {
            ModInit.LOGGER.info(e.getMessage());
            //e.printStackTrace();
        }
    }

    private static String loadText(boolean isProlog) {
        File file = isProlog ? PROLOG_FILE : EPILOGUE_FILE;
        if (!file.exists()) {
            return isProlog ? DEFAULT_PROLOG : DEFAULT_EPILOGUE;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch(IOException e) {
            ModInit.LOGGER.info(e.getMessage());
            //e.printStackTrace();
        }

        return isProlog ? DEFAULT_PROLOG : DEFAULT_EPILOGUE;
    }

    @Override
    public void close() {
        assert this.client != null : "Closing screen of which this.client is null";
        // Use close from parent but also store new prolog/epilog
        for (Element element : this.children()) {
            if (!(element instanceof OptionListWidget optionListWidget)) continue;
            optionListWidget.applyAllPendingValues();
        }
        this.client.setScreen(this.parent);
        storeText(true);
        storeText(false);
    }

    public static void register() {
        MinecraftClient client = MinecraftClient.getInstance();
        prologText = new TextFieldWidget(client.textRenderer, 150, 20, Text.of(""));
        epilogueText = new TextFieldWidget(client.textRenderer, 150, 20, Text.of(""));

        prologText.setText(loadText(true));
        epilogueText.setText(loadText(false));

        useZZZ.buttonOf();
        frequency.buttonOf();
        usePrologText.buttonOf();
        useEpilogueText.buttonOf();
        useRandomPrologMessage.buttonOf();
        useRandomEpilogueMessage.buttonOf();
        useMod.buttonOf();

        useModChildren = new ClickableWidget[]{
                useZZZ.button,
                frequency.button,
                usePrologText.button,
                useEpilogueText.button,
                prologText,
                epilogueText,
                openPrologMessageSelection,
                useRandomPrologMessage.button,
                openEpilogueMessageSelection,
                useRandomEpilogueMessage.button
        };



        // Set all elements inactive if useMod.getValue() is false
        if (!useMod.getValue()) {
            for (ClickableWidget child : useModChildren) {
                child.active = useMod.getValue();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateElementsActive();
    }

    private void updateElementsActive() {

        for (ClickableWidget child : useModChildren) {
            child.active = useMod.getValue();
            if (child instanceof TextFieldWidget textFieldWidget) {
                textFieldWidget.setEditable(useMod.getValue());
            }
        }

        if (!useMod.getValue()) return;

        frequency.button.active = useZZZ.getValue();
        prologText.active = usePrologText.getValue();
        prologText.setEditable(usePrologText.getValue());
        openPrologMessageSelection.active = useRandomPrologMessage.getValue();

        epilogueText.active = useEpilogueText.getValue();
        epilogueText.setEditable(useEpilogueText.getValue());
        openEpilogueMessageSelection.active = useRandomEpilogueMessage.getValue();
    }


    public void init() {
        super.init();
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        adder.add(useMod.button, 2);

        adder.add(frequency.button);
        adder.add(useZZZ.button);

        adder.add(EmptyWidget.ofWidth(16), 2);

        adder.add(prologText);
        adder.add(usePrologText.button);

        adder.add(openPrologMessageSelection);
        adder.add(useRandomPrologMessage.button);

        adder.add(EmptyWidget.ofWidth(16), 2);

        adder.add(epilogueText);
        adder.add(useEpilogueText.button);

        adder.add(openEpilogueMessageSelection);
        adder.add(useRandomEpilogueMessage.button);

        adder.add(EmptyWidget.ofHeight(16), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5f, 0.0f);
        gridWidget.forEachChild(this::addDrawableChild);

    }
}