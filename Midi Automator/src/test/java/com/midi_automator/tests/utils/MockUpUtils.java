package com.midi_automator.tests.utils;

import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.midi_automator.utils.SystemUtils;

public class MockUpUtils {

	/**
	 * Gets the path to the mido and properties files
	 * 
	 * @return The path to the settings
	 */
	private static String getMidiAutomatorSettingsPath() {

		String settingsPath = "";

		if (System.getProperty("os.name").contains("Mac")) {
			settingsPath = SystemUtils
					.replaceSystemVariables("$HOME/Library/Application Support/Midi Automator");
		}

		if (System.getProperty("os.name").contains("Windows")) {
			settingsPath = SystemUtils
					.replaceSystemVariables("%HOMEPATH%\\AppData\\Roaming\\Midi Automator");
		}

		return settingsPath;
	}

	/**
	 * Returns the backuped mido file.
	 * 
	 * @return The backuped mido file
	 */
	private static File getMidoBackupFile() {
		return new File(getMidiAutomatorSettingsPath() + File.separator
				+ "file_list.mido.bak");
	}

	/**
	 * Returns the backuped properties file.
	 * 
	 * @return The backuped mido file
	 */
	private static File getPropertiesBackupFile() {
		return new File(getMidiAutomatorSettingsPath() + File.separator
				+ "midiautomator.properties.bak");
	}

	/**
	 * Returns the original mido file.
	 * 
	 * @return The original mido file
	 */
	private static File getMidoFile() {
		return new File(getMidiAutomatorSettingsPath() + File.separator
				+ "file_list.mido");
	}

	/**
	 * Returns the original properties file.
	 * 
	 * @return The original properties file
	 */
	private static File getPropertiesFile() {
		return new File(getMidiAutomatorSettingsPath() + File.separator
				+ "midiautomator.properties");
	}

	/**
	 * Backups the current mido file of the installation and changes it to a
	 * mockup file.
	 * 
	 * @param mockUpPath
	 *            The path to the mockup file
	 */
	public static void setMockupMidoFile(String mockUpPath) {

		// rename original file
		getMidoFile().renameTo(getMidoBackupFile());

		// copy mockup file
		File mockupFile = new File(mockUpPath);
		try {
			Files.copy(mockupFile.toPath(), getMidoFile().toPath(),
					REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Backups the current properties file of the installation and changes it to
	 * a mockup file.
	 * 
	 * @param mockUpPath
	 *            The path to the mockup file
	 */
	public static void setMockupPropertiesFile(String mockUpPath) {

		// rename original file
		getPropertiesFile().renameTo(getPropertiesBackupFile());

		// copy mockup file
		File mockupFile = new File(mockUpPath);
		try {
			Files.copy(mockupFile.toPath(), getPropertiesFile().toPath(),
					REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recovers the backup of the mido file.
	 */
	public static void recoverMidoBackup() {

		// delete mockup file
		if (getMidoBackupFile().canRead()) {
			getMidoFile().delete();
		}

		// rename backup file
		getMidoBackupFile().renameTo(getMidoFile());
	}

	/**
	 * Recovers the backup of the mido file.
	 */
	public static void recoverPropertiesBackup() {

		// delete mockup file
		if (getPropertiesBackupFile().canRead()) {
			getPropertiesFile().delete();
		}

		// rename backup file
		getPropertiesBackupFile().renameTo(getPropertiesFile());
	}
}
