package com.midi_automator.presenter.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.midi_automator.Resources;
import com.midi_automator.model.IModel;
import com.midi_automator.model.MidiAutomatorProperties;
import com.midi_automator.model.SetList;
import com.midi_automator.model.SetListItem;
import com.midi_automator.model.TooManyEntriesException;
import com.midi_automator.presenter.Messages;
import com.midi_automator.presenter.MidiAutomator;
import com.midi_automator.utils.FileUtils;
import com.midi_automator.utils.MidiUtils;
import com.midi_automator.view.frames.MainFrame;

/**
 * Handles all functions on the file list.
 * 
 * @author aguelle
 *
 */
@Service
public class FileListService {

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Autowired
	private Resources resources;

	@Autowired
	private IModel model;

	@Autowired
	private MidiAutomator presenter;

	@Autowired
	private MainFrame mainFrame;

	@Autowired
	private MidiService midiDevicesService;
	@Autowired
	private GUIAutomationsService guiAutomationsService;
	@Autowired
	private MidiRemoteOpenService midiRemoteOpenService;
	@Autowired
	private MidiItemChangeNotificationService midiNotificationService;

	private static int currentItem = -1;

	private final long WAIT_BEFORE_OPENING = 100;

	/**
	 * Adds a new item
	 * 
	 * @param entryName
	 *            the name of the entry
	 * @param filePath
	 *            the path to the file
	 * @param midiSendingSignature
	 *            the midi signature the item will send on opening
	 */
	public void addItem(String entryName, String filePath,
			String midiSendingSignature) {
		setItem(null, entryName, filePath, "", midiSendingSignature);
	}

	/**
	 * Sets the attributes of an existing item
	 * 
	 * @param index
	 *            the index of the entry
	 * @param entryName
	 *            the name of the entry
	 * @param filePath
	 *            the path to the file
	 * @param midiListeningSignature
	 *            the midi signature the item is listening to
	 * @param midiSendingSignature
	 *            the midi signature the item will send on opening
	 * 
	 */
	public void setItem(Integer index, String entryName, String filePath,
			String midiListeningSignature, String midiSendingSignature) {

		insertItem(index, entryName, filePath, midiListeningSignature,
				midiSendingSignature, true);
	}

	/**
	 * Inserts an item
	 * 
	 * @param index
	 *            the index of the entry
	 * @param entryName
	 *            the name of the entry
	 * @param filePath
	 *            the path to the file
	 * @param midiListeningSignature
	 *            the midi signature the item is listening to
	 * @param midiSendingSignature
	 *            the midi signature the item will send on opening
	 * @param overwrite
	 *            <TRUE> the item will be overwritten, <FALSE> the item will be
	 *            inserted
	 * 
	 */
	public void insertItem(Integer index, String entryName, String filePath,
			String midiListeningSignature, String midiSendingSignature,
			boolean overwrite) {

		presenter.removeInfoMessage(Messages.builtMessages
				.get(Messages.KEY_ERROR_TOO_MUCH_ENTRIES));

		if (index == null && model.getSetList().getItems().size() >= 128) {
			String error = String.format(Messages.MSG_FILE_LIST_IS_FULL,
					entryName);
			Messages.builtMessages.put(Messages.KEY_ERROR_TOO_MUCH_ENTRIES,
					error);
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_TOO_MUCH_ENTRIES));
			return;
		}

		if (entryName != null && !entryName.equals("")) {
			SetListItem item = new SetListItem(entryName, filePath,
					midiListeningSignature, midiSendingSignature);

			if (overwrite) {
				model.getSetList().setItem(item, index);
				log.debug("Set item on index: " + index + ", entry: "
						+ entryName + ", file: " + filePath + ", listening: \""
						+ midiListeningSignature + "\", sending: \""
						+ midiSendingSignature + "\"");
			} else {
				model.getSetList().addItem(item, index);
				log.debug("Add item on index: " + index + ", entry: "
						+ entryName + ", file: " + filePath + ", listening: \""
						+ midiListeningSignature + "\", sending: \""
						+ midiSendingSignature + "\"");
			}

			saveSetList();
		}

		reloadSetList();
	}

	/**
	 * Deletes the item at the given index from the model
	 * 
	 * @param index
	 *            index of the item
	 */
	public void deleteItem(int index) {

		model.getSetList().getItems().remove(index);
		saveSetList();
		reloadSetList();
	}

	/**
	 * Saves the model to a file.
	 */
	public void saveSetList() {

		presenter.removeInfoMessage(Messages.builtMessages
				.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
		presenter.removeInfoMessage(Messages.builtMessages
				.get(Messages.KEY_ERROR_MIDO_FILE_IO));
		try {
			model.save();
		} catch (FileNotFoundException e) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
		} catch (IOException e) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_MIDO_FILE_IO));
		}
	}

	/**
	 * Loads the model file.
	 */
	public void reloadSetList() {
		try {
			model.load();
			presenter.removeInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
			presenter.removeInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_IO));
			presenter.removeInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_MIDO_FILE_TOO_BIG));

			List<SetListItem> items = model.getSetList().getItems();
			List<String> entryNames = new ArrayList<String>();
			List<String> midiListeningSignatures = new ArrayList<String>();
			List<String> midiSedningSignatures = new ArrayList<String>();

			for (SetListItem item : items) {
				entryNames.add(item.getName());
				midiListeningSignatures.add(item.getMidiListeningSignature());
				midiSedningSignatures.add(item.getMidiSendingSignature());
			}

			mainFrame.setFileEntries(entryNames);
			mainFrame.setMidiListeningSignatures(midiListeningSignatures);
			mainFrame.setMidiSendingSignatures(midiSedningSignatures);

		} catch (FileNotFoundException e1) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
		} catch (IOException e1) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_MIDO_FILE_IO));
		} catch (TooManyEntriesException e) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_MIDO_FILE_TOO_BIG));
		}

		mainFrame.setSelectedIndex(mainFrame.getLastSelectedIndex());
		mainFrame.reload();
	}

	/**
	 * Pushes the item at the given index one step up
	 * 
	 * @param index
	 *            index of the item
	 */
	public void moveUpItem(int index) {

		model.getSetList().exchangeIndexes(index, index - 1);
		saveSetList();
		reloadSetList();
	}

	/**
	 * Pushes the item at the given index one step down
	 * 
	 * @param index
	 *            index of the item
	 */
	public void moveDownItem(int index) {

		model.getSetList().exchangeIndexes(index, index + 1);
		saveSetList();
		reloadSetList();
	}

	/**
	 * Opens a file from the file list
	 * 
	 * @param index
	 *            The index of the file to open from the list
	 * @param send
	 *            <TRUE> opened index will be sent to slaves, <FALSE> index will
	 *            not be sent
	 */
	public void openFileByIndex(int index, boolean send) {

		log.debug("Open file with index: " + index);

		try {

			if (!model.getSetList().getItems().isEmpty()) {
				SetListItem item = model.getSetList().getItems().get(index);

				presenter.removeInfoMessage(Messages.builtMessages
						.get(Messages.KEY_INFO_ENTRY_OPENED));
				String infoEntryOpened = String.format(
						Messages.MSG_OPENING_ENTRY, item.getName());
				Messages.builtMessages.put(Messages.KEY_INFO_ENTRY_OPENED,
						infoEntryOpened);

				mainFrame.setSelectedIndex(index);
				currentItem = index;

				// Send MIDI change notifier
				midiNotificationService
						.sendItemChangeNotifier(midiDevicesService
								.getMidiDeviceByKey(MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_NOTIFIER_DEVICE));

				// activate per change triggered automations
				guiAutomationsService.activateAllOncePerChangeAutomations();

				// Send MIDI remote open command
				if (send) {
					midiRemoteOpenService.sendRemoteOpenMidiMessage(index);
				}

				// wait a little before opening file...
				try {
					Thread.sleep(WAIT_BEFORE_OPENING);
				} catch (InterruptedException e) {
					log.error("Delay before opening a file failed.", e);
				}

				// Send MIDI item signature
				midiNotificationService
						.sendItemSignature(
								midiDevicesService
										.getMidiDeviceByKey(MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_ITEM_DEVICE),
								item.getMidiSendingSignature());

				String filePath = item.getFilePath();

				String path = resources.generateRelativeLoadingPath(filePath);

				presenter.removeInfoMessage(Messages.builtMessages
						.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
				presenter.removeInfoMessage(Messages.builtMessages
						.get(Messages.KEY_ERROR_ITEM_FILE_IO));

				String errFileNotFound = String.format(
						Messages.MSG_FILE_LIST_NOT_FOUND, filePath);
				String errFileNotReadable = String.format(
						Messages.MSG_FILE_LIST_NOT_READABLE, filePath);

				Messages.builtMessages
						.put(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND,
								errFileNotFound);
				Messages.builtMessages.put(Messages.KEY_ERROR_ITEM_FILE_IO,
						errFileNotReadable);

				if (!path.equals("")) {
					log.info("Opening file: " + path);
					FileUtils.openFileFromPath(path);
					presenter.setInfoMessage(infoEntryOpened);
				}
			}
		} catch (IllegalArgumentException ex) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND));
			log.error(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_NOT_FOUND), ex);
		} catch (IOException ex) {
			presenter.setInfoMessage(Messages.builtMessages
					.get(Messages.KEY_ERROR_ITEM_FILE_IO));
			log.error(
					Messages.builtMessages.get(Messages.KEY_ERROR_ITEM_FILE_IO),
					ex);
		}
	}

	/**
	 * Opens the previous file in the list
	 */
	public void openPreviousFile() {

		currentItem--;

		// cycle list
		if (currentItem < 0) {
			currentItem = (model.getSetList().getItems().size() - 1);
		}
		openFileByIndex(currentItem, true);
	}

	/**
	 * Opens the next file in the list
	 */
	public void openNextFile() {

		currentItem++;

		// cylce list
		if (currentItem >= model.getSetList().getItems().size()) {
			currentItem = 0;
		}
		openFileByIndex(currentItem, true);
	}

	/**
	 * Gets the entry name of the file list by index
	 * 
	 * @param index
	 *            The index
	 * @return the name of the file entry
	 */
	public String getEntryNameByIndex(int index) {
		try {
			return model.getSetList().getItems().get(index).getName();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the entry file path of the file list by index
	 * 
	 * @param index
	 *            The index
	 * @return the file path of the entry
	 */
	public String getEntryFilePathByIndex(int index) {
		try {
			return model.getSetList().getItems().get(index).getFilePath();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the midi listening signature for the file list index
	 * 
	 * @param index
	 *            The index in the file list
	 * @return The midi signature
	 */
	public String getMidiFileListListeningSignature(int index) {
		try {
			SetList setList = model.getSetList();
			List<SetListItem> list = setList.getItems();
			return list.get(index).getMidiListeningSignature();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the midi signature a file list index is sending
	 * 
	 * @param index
	 *            The index in the file list
	 * @return The midi signature
	 */
	public String getMidiFileListSendingSignature(int index) {
		try {
			SetList setList = model.getSetList();
			List<SetListItem> list = setList.getItems();
			return list.get(index).getMidiSendingSignature();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets a unique midi signature that is not used for any item yet.
	 * 
	 * @return The midi signature, <NULL> if there is no unique signature left
	 */
	public String getUniqueSendingMidiSignature() {

		List<String> signatures = model.getSetList().getMidiSendingSignatures();

		ShortMessage message = new ShortMessage();

		int controlNo = 1;
		boolean found = true;
		String signature = null;

		while (found && controlNo <= 127) {
			try {
				message.setMessage(
						ShortMessage.CONTROL_CHANGE,
						MidiItemChangeNotificationService.ITEM_SEND_MIDI_CHANNEL - 1,
						controlNo,
						MidiItemChangeNotificationService.ITEM_SEND_MIDI_VALUE);
			} catch (InvalidMidiDataException e) {
				log.error("Created invalid MIDI message for item", e);
			}
			signature = MidiUtils.messageToString(message);

			if (!signatures.contains(signature)) {
				found = false;
			}
			controlNo++;
		}

		return signature;
	}

	/**
	 * Resets the current item.
	 */
	public void resetCurrentItem() {
		currentItem = -1;
	}
}