package de.jasper.zzzzz;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Path;


/**
 * Playerautoma option screen to configure settings
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

    public static final File PROLOG_FILE = new File(String.valueOf(Path.of(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "zzzzProlog")));
    public static final File EPILOGUE_FILE = new File(String.valueOf(Path.of(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "zzzzEpilog")));
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

    public static TextFieldWidget prologText;
    public static TextFieldWidget epilogueText;

    public ZzzzzSettings(String title, Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.of(title));
    }

    private static void storeText(boolean isProlog) {
        File file = isProlog ? PROLOG_FILE : EPILOGUE_FILE;
        String toStore = isProlog ? prologText.getText() : epilogueText.getText();
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(toStore);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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

    public void init() {
        super.init();
        assert this.client != null;

        prologText = new TextFieldWidget(client.textRenderer, 150, 20, Text.of(""));
        epilogueText = new TextFieldWidget(client.textRenderer, 150, 20, Text.of(""));

        prologText.setText(loadText(true));
        epilogueText.setText(loadText(false));

        prologText.setEditable(usePrologText.getValue());
        epilogueText.setEditable(useEpilogueText.getValue());


        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        ButtonWidget useZZZButton = ButtonWidget.builder(
                Text.translatable(useZZZ.key).append(": ").append(useZZZ.getValue() ? ScreenTexts.ON : ScreenTexts.OFF),
                (_b) -> {
                    useZZZ.next();
                    frequency.button.active = useZZZ.getValue();
                }
        ).build();
        useZZZ.setButton(useZZZButton);

        ButtonWidget frequencyButton = frequency.buttonOf();
        frequencyButton.active = useZZZ.getValue();
        frequency.setButton(frequencyButton);
        ButtonWidget usePrologTextButton = ButtonWidget.builder(
                Text.translatable(usePrologText.key).append(": ").append(usePrologText.getValue() ? ScreenTexts.ON : ScreenTexts.OFF),
                (_b) -> {
                    usePrologText.next();
                    boolean val = usePrologText.getValue();
                    prologText.active = val;
                    prologText.setEditable(val);
                }
        ).build();
        usePrologText.setButton(usePrologTextButton);

        ButtonWidget useEpilogueTextButton = ButtonWidget.builder(
                Text.translatable(useEpilogueText.key).append(": ").append(useEpilogueText.getValue() ? ScreenTexts.ON : ScreenTexts.OFF),
                (_b) -> {
                    useEpilogueText.next();
                    boolean val = useEpilogueText.getValue();
                    epilogueText.active = val;
                    epilogueText.setEditable(val);
                }
        ).build();
        useEpilogueText.setButton(useEpilogueTextButton);

        ClickableWidget[] useModChildren = {
                useZZZButton,
                frequencyButton,
                usePrologTextButton,
                useEpilogueTextButton,
                prologText,
                epilogueText
        };

        ButtonWidget useModButton = ButtonWidget.builder(
                Text.translatable(useMod.key).append(": ").append(useMod.getValue() ? ScreenTexts.ON : ScreenTexts.OFF),
                (_b) -> {
                    useMod.next();
                    for (ClickableWidget child : useModChildren) {
                        child.active = useMod.getValue();
                    }
                    prologText.setEditable(useMod.getValue());
                    epilogueText.setEditable(useMod.getValue());
                }
        ).build();
        useMod.setButton(useModButton);

        // Set active state according to useMod.getValue()
        for (ClickableWidget child : useModChildren) {
            child.active = useMod.getValue();
        }

        adder.add(useModButton, 2);

        adder.add(frequency.button);
        adder.add(useZZZ.button);

        adder.add(prologText);
        adder.add(usePrologTextButton);

        adder.add(epilogueText);
        adder.add(useEpilogueTextButton);

        adder.add(EmptyWidget.ofHeight(16), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5f, 0.0f);
        gridWidget.forEachChild(this::addDrawableChild);
    }
}