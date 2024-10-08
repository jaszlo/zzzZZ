package de.jasper.zzzzz.gui.components;

import de.jasper.zzzzz.ModInit;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds and takes care of playerautoma options
 * @param <Value> The Type of the value that the option has
 */
public class OptionButton<Value> {

    private static final Logger LOGGER = LoggerFactory.getLogger("zzzzz::options");

    private static final String OPTION_FILE_NAME = "zzzzz_options.txt";
    public static final File OPTION_FILE = new File(String.valueOf(Path.of(ModInit.OPTIONS_FOLDER_PATH, OPTION_FILE_NAME)));

    public static final Boolean[] BOOLEAN_VALUES = { true, false };

    public String key;
    public ButtonWidget button;

    private Value currentValue;
    private final Value[] values;
    private final Value defaultValue;
    public final ValueDecoder<Value> decoder;
    public final ValueEncoder<Value> encoder;
    public final TextProvider<Value> textProvider;

    private int valueIndex = -1;

    public interface ValueEncoder<Value> {
        String encode(Value v);
    }

    public interface ValueDecoder<Value> {
        Value decode(String s);
    }

    public interface TextProvider<Value> {
        Text provide(Value v);
    }


    public Value getValue() {
        return this.currentValue;
    }

    public OptionButton(Value defaultValue, Value[] values, String key, ValueEncoder<Value> encoder, ValueDecoder<Value> decoder, TextProvider<Value> textProvider) {
        this.button = null;
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
        this.values = values;
        this.key = key;
        this.encoder = encoder;
        this.decoder = decoder;
        this.textProvider = textProvider;

        load();

        for (int i = 0; i < values.length; i++) {
            if (values[i] == this.currentValue) {
                valueIndex = i;
                break;
            }
        }

        if (valueIndex < 0) {
            LOGGER.error("currentValue is not a defined value for Option " + key);
            valueIndex = 0;
            this.currentValue = values[0];
        }
    }

    public void setButton(ButtonWidget button) {
        this.button = button;
    }

    public void next() {
        this.valueIndex = (this.valueIndex + 1) % this.values.length;
        this.currentValue = this.values[valueIndex];
        this.button.setMessage(Text.translatable(this.key).append(": ").append(this.textProvider.provide(this.currentValue)));
        this.store();
    }


    public void store() {
        List<String> lines = new ArrayList<>();

        // Read existing content from the file
        try (BufferedReader br = new BufferedReader(new FileReader(OPTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            assert false : "Could not read playerautoma options";
        }

        boolean keyFound = false;

        String valueToWrite = this.encoder.encode(currentValue);
        // Check if the key already exists in the file
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(this.key + ":")) {
                lines.set(i, this.key + ":" + valueToWrite);
                keyFound = true;
                break;
            }
        }

        // If the key is not found, append it to the end of the file
        if (!keyFound) {
            lines.add(this.key + ":" + valueToWrite);
        }

        // Write the updated content back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(OPTION_FILE))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            assert false : "Could not write playerautoma options";
        }
    }

    public void load() {
        String readValue = null;
        try (BufferedReader br = new BufferedReader(new FileReader(OPTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split each line into key and readValue
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].trim().equals(key)) {
                    readValue = parts[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            assert false : "Could not read playerautoma options";
        }

        // Value was not in option file therefore store default value
        if (readValue == null) {
            store();
            return;
        }
        // Catch invalid values in config (if edited manually) and set/write defaultValue
        try {
            this.currentValue = decoder.decode(readValue);
        } catch (Exception e) {
            LOGGER.warn("Forbidden value '" + readValue + "' found in playerautoma_options.txt for '" + this.key + "'");
            this.currentValue = defaultValue;
            store();
        }
    }

    /**
     * Creates the default button widget for this option button and returns it. Also registers the button for this option.
     * Will always call this.next for button onClick.
     * @return ButtonWidget of this option
     */
    public ButtonWidget buttonOf() {
        ButtonWidget button =  ButtonWidget.builder(
            Text.translatable(this.key).append(": ").append(this.textProvider.provide(this.getValue())),
            (b) -> this.next()).build();
        this.setButton(button);
        return button;
    }
}
