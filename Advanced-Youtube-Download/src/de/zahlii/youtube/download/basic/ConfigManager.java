package de.zahlii.youtube.download.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is used for storing and retrieving the Config entries. Every entry
 * is associated with exactly one ConfigKey.
 * 
 * @author Zahlii
 * 
 */
public class ConfigManager {
	public enum ConfigKey {
		AUDIO_BITRATE, DIR_TARGET, FILENAME_CONVENTION, DIR_IMAGES, IS_DEFAULT, KEEP_VIDEO, IMPROVE_CONVERT, VOLUME_METHOD
	}

	public static final String DS = File.separator;
	public static final File YOUTUBE_DL = new File("youtube-dl.exe");
	public static final File MP3GAIN = new File("mp3gain.exe");
	public static final File FFMPEG = new File("ffmpeg.exe");
	public static final File TEMP_DIR = new File("temp");

	public static final File METAFLAC = new File("metaflac.exe");

	private static ConfigManager instance;
	private static File configFile = new File("ytload.config");

	private static String encoding = "UTF-8";

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}

		return instance;
	}

	private final Map<ConfigKey, String> config;

	private ConfigManager() {
		config = new HashMap<ConfigKey, String>();
		loadFile();
	}

	/**
	 * Get a config entry or the default value. Please note: When the entry does
	 * not exist, it will be saved to the default value in the config file.
	 * 
	 * @param key
	 *            the ConfigKey to get
	 * @param def
	 *            the default value
	 * @return default value or setting
	 */
	public String getConfig(final ConfigKey key, final String def) {
		if (!config.containsKey(key)) {
			setConfig(key, def);
			return def;
		} else
			return config.get(key);
	}

	/**
	 * Loads the config from the file, creating it if necessary.
	 */
	private void loadFile() {

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}

			final byte[] encoded = Files.readAllBytes(Paths.get(configFile
					.toURI()));
			final String[] lines = new String(encoded, encoding).split("\n");
			for (final String line : lines) {
				final String[] parts = line.split("=");
				if (parts.length < 2) {
					continue;
				}
				config.put(ConfigKey.valueOf(parts[0]),
						parts[1].replace("\r", "").replace("\n", ""));
			}
		} catch (final IOException e) {
			Logging.log("failed loading config file", e);
		}
	}

	/**
	 * Set a config entry.
	 * 
	 * @param key
	 *            ConfigKey to set
	 * @param value
	 *            Value it should take
	 */
	public void setConfig(final ConfigKey key, final String value) {
		config.put(key, value);
		writeFile();
	}

	/**
	 * Writes all settings out of the HashMap into the file.
	 */
	private void writeFile() {

		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}

			final StringBuilder sb = new StringBuilder();
			for (final Entry<ConfigKey, String> e : config.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("\n");
			}

			final FileWriter f2 = new FileWriter(configFile, false);
			f2.write(sb.toString());
			f2.close();

		} catch (final IOException e) {
			Logging.log("failed writing config file", e);
		}
	}

}
