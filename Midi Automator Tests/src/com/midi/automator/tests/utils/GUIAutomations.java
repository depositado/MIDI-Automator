package com.midi.automator.tests.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Region;

public class GUIAutomations extends SikuliAutomation {

	/**
	 * Opens the Midi Automator program
	 * 
	 * @throws IOException
	 */
	public static void openMidiAutomator() throws IOException {

		String filePath = "";

		if (System.getProperty("os.name").equals("Mac OS X")) {
			filePath = "/Applications/Midi Automator.app";
		}

		if (System.getProperty("os.name").equals("Windows 7")) {
			filePath = SystemUtils
					.replaceSystemVariables("%ProgramFiles%\\Midi Automator\\Midi Automator.exe");
		}

		File file = new File(filePath);

		Desktop.getDesktop().open(file);
	}

	/**
	 * Puts the program to midi learn for the given screenshot
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param similarity
	 *            the minimum similarity
	 * @throws FindFailed
	 */
	public static void midiLearn(String state1, String state2, String state3,
			float similarity) throws FindFailed {
		openPopupMenu(state1, state2, state3, similarity);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "midi_learn.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Unlearns a midi message on the given screenshot
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void midiUnlearn(String state1, String state2, String state3)
			throws FindFailed {
		openPopupMenu(state1, state2, state3, LOW_SIMILARITY);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "midi_unlearn.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Cancels the midi learn for the given screenshot
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void cancelMidiLearn(String state1, String state2,
			String state3) throws FindFailed {
		openPopupMenu(state1, state2, state3, LOW_SIMILARITY);
		SikuliAutomation.setMinSimilarity(LOW_SIMILARITY);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "cancel_midi_learn.png", MAX_TIMEOUT);
		SikuliAutomation.setMinSimilarity(DEFAULT_SIMILARITY);
		match.click();
	}

	/**
	 * Opens the preferences window.
	 * 
	 * @return the region of the preferences window
	 * @throws FindFailed
	 */
	public static Region openPreferences() throws FindFailed {
		openFileMenu();
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "preferences.png", MAX_TIMEOUT);
		match.click();
		SikuliAutomation.setSearchRegion(SCREEN);
		SikuliAutomation.setMinSimilarity(LOW_SIMILARITY);
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "midi_automator_preferences.png", MAX_TIMEOUT);
		SikuliAutomation.setMinSimilarity(DEFAULT_SIMILARITY);
		match.y -= 20;
		match.h += 20;
		return match;
	}

	/**
	 * Sets the Midi Remote IN device to the given screenshot and saves the
	 * preferences
	 * 
	 * @param scLabel
	 *            The label of the combo box
	 * @param scValue
	 *            The value to choose
	 * @throws FindFailed
	 */
	public static void setAndSavePreferencesComboBox(String scLabel,
			String scValue) throws FindFailed {

		setPreferencesComboBox(scLabel, scValue);
		GUIAutomations.saveDialog();
		SikuliAutomation.setSearchRegion(GUIAutomations
				.findMidiAutomatorRegion());
	}

	/**
	 * Sets the Midi Remote IN device to the given screenshot
	 * 
	 * @param scLabel
	 *            The label of the combo box
	 * @param scValue
	 *            The value to choose
	 * @throws FindFailed
	 */
	public static void setPreferencesComboBox(String scLabel, String scValue)
			throws FindFailed {

		Region preferencesRegion = GUIAutomations.openPreferences();
		SikuliAutomation.setSearchRegion(preferencesRegion);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + scLabel, MAX_TIMEOUT);
		match.click(match.offset(0, 20));
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + scValue, MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Opens the next file
	 * 
	 * @throws FindFailed
	 */
	public static void nextFile() throws FindFailed {
		SikuliAutomation.setMinSimilarity(HIGH_SIMILARITY);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "next.png", MAX_TIMEOUT);
		SikuliAutomation.setMinSimilarity(DEFAULT_SIMILARITY);
		match.click();
	}

	/**
	 * Opens the previous file
	 * 
	 * @throws FindFailed
	 */
	public static void prevFile() throws FindFailed {
		SikuliAutomation.setMinSimilarity(HIGH_SIMILARITY);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "prev.png", MAX_TIMEOUT);
		SikuliAutomation.setMinSimilarity(DEFAULT_SIMILARITY);
		match.click();
	}

	/**
	 * Opens the file menu
	 * 
	 * @throws FindFailed
	 */
	public static void openFileMenu() throws FindFailed {
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "file.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Opens the exit menu
	 * 
	 * @throws FindFailed
	 */
	public static void openExitMenu() throws FindFailed {
		openFileMenu();
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "exit.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Deletes an entry
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param screenshot
	 * @throws FindFailed
	 */
	public static void deleteEntry(String state1, String state2, String state3)
			throws FindFailed {
		Region match = findMultipleStateRegion(MIN_TIMEOUT, state1, state2,
				state3);
		match.rightClick();
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "delete.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Moves a file entry one position up
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void moveUpEntry(String state1, String state2, String state3)
			throws FindFailed {

		Region match = GUIAutomations.findMultipleStateRegion(MIN_TIMEOUT,
				state1, state2, state3);

		match.rightClick();
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "move_up.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Moves a file entry one position up
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void moveDownEntry(String state1, String state2, String state3)
			throws FindFailed {

		Region match = GUIAutomations.findMultipleStateRegion(MIN_TIMEOUT,
				state1, state2, state3);

		match.rightClick();
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "move_down.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Adds a new file to the entry list
	 * 
	 * @param name
	 *            name of the entry
	 * @param path
	 *            path to the file
	 * @throws FindFailed
	 */
	public static void addFile(String name, String path) throws FindFailed {

		openAddDialog();
		fillTextField("name_text_field.png", name);
		fillTextField("file_text_field.png", path);
		saveDialog();
	}

	/**
	 * Opens the popup menu
	 * 
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param similarity
	 *            The minimum similarity
	 * @throws FindFailed
	 */
	public static void openPopupMenu(String state1, String state2,
			String state3, float similarity) throws FindFailed {
		setMinSimilarity(similarity);
		Region match = findMultipleStateRegion(MIN_TIMEOUT, state1, state2,
				state3);
		setMinSimilarity(DEFAULT_SIMILARITY);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		match.rightClick();
	}

	/**
	 * Opens the add dialog
	 * 
	 * @throws FindFailed
	 */
	public static void openAddDialog() throws FindFailed {

		openPopupMenu("midi_automator.png", null, null, LOW_SIMILARITY);
		SikuliAutomation.setMinSimilarity(LOW_SIMILARITY);
		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "add.png", MAX_TIMEOUT);
		SikuliAutomation.setMinSimilarity(DEFAULT_SIMILARITY);
		match.click();
	}

	/**
	 * Opens the edit dialog
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void openEditDialog(String state1, String state2,
			String state3) throws FindFailed {

		Region match = findMultipleStateRegion(MIN_TIMEOUT, state1, state2,
				state3);
		match.click();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		match.rightClick();
		match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "edit.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Finds a region that can have multiple states, i.e. active, inactive,
	 * unfocused
	 * 
	 * @param timeout
	 *            the timeout to search for every state
	 * @param states
	 *            the different states of the region
	 * @return The found region
	 * @throws FindFailed
	 */
	public static Region findMultipleStateRegion(double timeout,
			String... states) throws FindFailed {

		Region match;
		FindFailed findFailed = null;

		for (String state : states) {
			if (state != null) {
				try {
					match = SikuliAutomation.getSearchRegion().wait(
							screenshotpath + state, timeout);
					return match;
				} catch (FindFailed e) {
					findFailed = e;
					System.out.println(state
							+ " not found. Trying next state...");
				}
			}
		}

		throw findFailed;
	}

	/**
	 * Opens an entry of the list.
	 * 
	 * @param state1
	 *            first try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state2
	 *            second try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @param state3
	 *            third try screenshot (unchoosen, choosen, choosen-unfocused)
	 * @throws FindFailed
	 */
	public static void openEntryByDoubleClick(String state1, String state2,
			String state3) throws FindFailed {

		Region match = findMultipleStateRegion(MIN_TIMEOUT, state1, state2,
				state3);
		match.doubleClick();
	}

	/**
	 * Sets the focus on the Midi Automator window
	 * 
	 * @throws FindFailed
	 */
	public static void focusMidiAutomator() throws FindFailed {
		try {
			SikuliAutomation.setSearchRegion(findMidiAutomatorRegion());
			Region match = findMultipleStateRegion(MIN_TIMEOUT,
					"Midi_Automator_title.png",
					"Midi_Automator_title_inactive.png");
			match.click(match.offset(50, 20));
		} catch (FindFailed e) {
			System.err.println("focusMidiAutomator() failed");
			throw e;
		}
	}

	/**
	 * Finds the region of the Midi Automator main Window
	 * 
	 * @return the found region
	 * @throws FindFailed
	 */
	public static Region findMidiAutomatorRegion() throws FindFailed {

		try {
			setMinSimilarity(LOW_SIMILARITY);
			SikuliAutomation.setSearchRegion(SCREEN);
			Region searchRegion = findMultipleStateRegion(MAX_TIMEOUT,
					"midi_automator.png");
			setMinSimilarity(DEFAULT_SIMILARITY);
			searchRegion.y = searchRegion.y - 21;
			searchRegion.w = searchRegion.w + 100;
			searchRegion.h = searchRegion.h + 100;
			return searchRegion;
		} catch (FindFailed e) {
			System.err.println("findMidiAutomatorRegion() failed");
			throw e;
		}
	}

	/**
	 * Checks if the file opened correctly
	 * 
	 * @param active
	 *            screenshot of active window
	 * @param inactive
	 *            screenshot of inactive window
	 * @return <TRUE> if file was opened, <FALSE> if not
	 */
	public static boolean checkIfFileOpened(String active, String inactive) {

		Region match = null;

		try {

			// minimize Midi Automator
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			focusMidiAutomator();
			hideFocusedProgram();

			// check if file opened
			SikuliAutomation.setSearchRegion(SCREEN);
			match = findMultipleStateRegion(DEFAULT_TIMEOUT, active, inactive);
			match.highlight(HIGHLIGHT_DURATION);

			// close editor
			match.click();
			closeFocusedProgram();

		} catch (FindFailed e) {
			return false;
		} finally {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				unhideMidiAutomator();
				focusMidiAutomator();
			} catch (FindFailed e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Minimizes the current focused program
	 */
	public static void hideFocusedProgram() {
		if (System.getProperty("os.name").equals("Mac OS X")) {
			SCREEN.type("h", KeyModifier.CMD);
		}
		if (System.getProperty("os.name").equals("Windows 7")) {
			SCREEN.type(Key.DOWN, KeyModifier.WIN);
		}
	}

	/**
	 * Closes the current focused program.
	 */
	public static void closeFocusedProgram() {
		if (System.getProperty("os.name").equals("Mac OS X")) {
			SCREEN.type("q", Key.CMD);
		}
		if (System.getProperty("os.name").equals("Windows 7")) {
			SCREEN.type(Key.F4, KeyModifier.WIN | KeyModifier.ALT);
		}
	}

	/**
	 * Shows the Midi Automator main window when it was hidden.
	 * 
	 * @throws FindFailed
	 */
	public static void unhideMidiAutomator() throws FindFailed {
		// show Midi Automator
		if (System.getProperty("os.name").equals("Mac OS X")) {
			SCREEN.type(Key.TAB, Key.CMD);

			// MAc Dock not recognized by Sikuli
			// Region match = SCREEN.wait(screenshotpath
			// + "midi_automator_icon.png", TIMEOUT);
			// match.click();
		}
		if (System.getProperty("os.name").equals("Windows 7")) {
			SCREEN.type(Key.TAB, Key.ALT);
			SCREEN.type(Key.TAB, Key.ALT);
		}
	}

	/**
	 * Opens a search file chooser
	 * 
	 * @throws FindFailed
	 */
	public static void openSearchDialog() throws FindFailed {

		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "search_button.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Cancels a dialog
	 * 
	 * @throws FindFailed
	 */
	public static void cancelDialog() throws FindFailed {

		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "cancel_button.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Saves a dialog
	 * 
	 * @throws FindFailed
	 */
	public static void saveDialog() throws FindFailed {

		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + "save_button.png", MAX_TIMEOUT);
		match.click();
	}

	/**
	 * Fills a text field with a given text
	 * 
	 * @param screenshot
	 *            screenshot of the text field
	 * @param text
	 *            the text to type in
	 * @throws FindFailed
	 */
	public static void fillTextField(String screenshot, String text)
			throws FindFailed {

		Region match = SikuliAutomation.getSearchRegion().wait(
				screenshotpath + screenshot, MAX_TIMEOUT);
		match.click(match.offset(50, 0));

		if (System.getProperty("os.name").equals("Mac OS X")) {
			SCREEN.type("a", KeyModifier.CMD);
		}

		if (System.getProperty("os.name").equals("Windows 7")) {
			SCREEN.type("a", KeyModifier.CTRL);
		}
		SCREEN.paste(text);
	}

	/**
	 * Closes the Midi Automator program
	 * 
	 * @throws FindFailed
	 */
	public static void closeMidiAutomator() throws FindFailed {

		focusMidiAutomator();
		closeFocusedProgram();
	}

	/**
	 * Restarts the MIDI Automator and sets search region to it
	 * 
	 * @throws FindFailed
	 * @throws IOException
	 */
	public static void restartMidiAutomator() throws FindFailed, IOException {

		closeMidiAutomator();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		GUIAutomations.openMidiAutomator();

		SikuliAutomation.setSearchRegion(GUIAutomations
				.findMidiAutomatorRegion());
	}

}