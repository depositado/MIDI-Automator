package com.midi_automator.view.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.midi_automator.guiautomator.GUIAutomation;
import com.midi_automator.model.MidiAutomatorProperties;
import com.midi_automator.presenter.MidiAutomator;
import com.midi_automator.presenter.PropertiesReloadThread;
import com.midi_automator.utils.GUIUtils;
import com.midi_automator.utils.MidiUtils;
import com.midi_automator.view.CacheableJTable;
import com.midi_automator.view.HTMLLabel;
import com.midi_automator.view.automationconfiguration.AutomationIndexDoesNotExistException;
import com.midi_automator.view.automationconfiguration.GUIAutomationConfigurationPanel;
import com.midi_automator.view.automationconfiguration.GUIAutomationConfigurationTable;

public class PreferencesFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(PreferencesFrame.class.getName());

	private final int PADDING_HORIZONTAL = 20;
	private final int PADDING_VERTICAL = 10;

	private final String TITLE = "Preferences";
	private final String LABEL_MIDI_IN_REMOTE_DEVICES = "Midi Remote IN:";
	private final String LABEL_MIDI_OUT_REMOTE_DEVICES = "Midi Master OUT:";
	private final String LABEL_MIDI_OUT_REMOTE_OPEN = "Master open: ch 1 CC 102 &lt;list entry - 1&gt;";
	private final String LABEL_MIDI_OUT_SWITCH_NOTIFIER_DEVICES = "Midi Switch Notifier OUT:";
	private final String LABEL_MIDI_OUT_SWITCH_NOTIFIER_INFO = "Notifier: ch 1 CC 103 value 127";
	private final String LABEL_MIDI_OUT_SWITCH_ITEM_DEVICES = "Midi Switch List Entry OUT:";
	private final String LABEL_MIDI_OUT_SWITCH_ITEM_INFO = "Switch item: ch 16 CC [1 - 127] value 127";
	private final String LABEL_MIDI_IN_METRONOM_DEVICES = "Midi Metronom IN:";
	private final String LABEL_MIDI_IN_METRONOM_INFO = "1st click: ch 16 NOTE ON A4<br/>"
			+ "Other clicks: ch 16 NOTE ON E4";
	private final String LABEL_GUI_AUTOMATION = "Mouse Automation:";
	private final String BUTTON_SEND_NOTIFIER = "Send...";
	private final String BUTTON_SAVE = "Save";
	private final String BUTTON_CANCEL = "Cancel";
	private final String MIDI_IN_REMOTE_DEVICE_COMBO_BOX_NAME = "midiINRemoteDeviceComboBox";
	private final String MIDI_OUT_REMOTE_DEVICE_COMBO_BOX_NAME = " midiOUTRemoteDeviceComboBox";
	private final String MIDI_OUT_SWITCH_NOTIFIER_DEVICE_COMBO_BOX_NAME = "midiOUTSwitchNotifierDeviceComboBox";
	private final String MIDI_OUT_SWITCH_ITEM_DEVICE_COMBO_BOX_NAME = "midiOUTSwitchItemDeviceComboBox";
	private final String MIDI_IN_METRONROM_DEVICE_COMBO_BOX_NAME = "midiINMetronomDeviceComboBox";

	private JPanel middlePanel;
	private JPanel footerPanel;
	private JLabel midiINRemoteDevicesLabel;
	private JLabel midiOUTRemoteDevicesLabel;
	private JLabel midiOUTSwitchNotifierDevicesLabel;
	private JLabel midiOUTSwitchItemDevicesLabel;
	private JLabel labelHeader;
	private JLabel guiAutomationLabel;
	private HTMLLabel midiOUTSwitchNotifierInfoLabel;
	private HTMLLabel midiOUTSwitchItemInfoLabel;
	private HTMLLabel midiINMetronomInfoLabel;
	private JComboBox<String> midiINRemoteDeviceComboBox;
	private JComboBox<String> midiOUTRemoteDeviceComboBox;
	private JComboBox<String> midiOUTSwitchNotifierDeviceComboBox;
	private JComboBox<String> midiINMetronomDeviceComboBox;
	private JComboBox<String> midiOUTSwitchItemDeviceComboBox;
	private final GUIAutomationConfigurationPanel GUI_AUTOMATION_PANEL;

	private JButton buttonSendNotify;
	private JButton buttonSave;
	private JButton buttonCancel;

	private final Insets INSETS_LABEL_HEADER = new Insets(0, 5, 0, 10);
	private final Insets INSETS_COMBO_BOX = new Insets(0, 0, 0, 10);
	private final Insets INSETS_LABEL_INFO = new Insets(0, 5, 0, 10);
	private final int CONSTRAINT_FILL_COMBO_BOX = GridBagConstraints.NONE;
	private final int CONSTRAINT_ANCHOR_COMBO_BOX = GridBagConstraints.WEST;

	private final MidiAutomator APPLICATION;
	private final JFrame PARENT_FRAME;

	/**
	 * Constructor
	 * 
	 * @param application
	 *            The main application
	 * @param programFrame
	 *            The main programFrame
	 * @throws HeadlessException
	 *             If no GUI available
	 */
	public PreferencesFrame(MidiAutomator application, JFrame programFrame)
			throws HeadlessException {
		super();

		this.setResizable(false);
		this.APPLICATION = application;
		this.PARENT_FRAME = programFrame;

		setTitle(TITLE);
		setLocation(this.PARENT_FRAME.getLocationOnScreen());

		// set layout
		JPanel contentPanel = new JPanel();
		Border padding = BorderFactory.createEmptyBorder(PADDING_VERTICAL,
				PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL);
		contentPanel.setBorder(padding);
		contentPanel.setLayout(new BorderLayout());
		setContentPane(contentPanel);
		middlePanel = new JPanel(new GridBagLayout());
		footerPanel = new JPanel(new FlowLayout());

		// MIDI IN Remote
		createRemoteMidiInDevices();

		// MIDI OUT Remote
		createRemoteMidiOutDevices();

		// MIDI IN Metronom
		createMetronomMidiInDevices();

		// MIDI OUT Switch Item
		createSwitchItemOutDevices();

		// MIDI OUT Switch Notifier
		createSwitchNotifierMidiOutDevices();

		// GUI Automation
		GUI_AUTOMATION_PANEL = new GUIAutomationConfigurationPanel(APPLICATION);
		createGUIAutomation();
		application.setGUIAutomationsToActive(false);

		// Save
		buttonSave = new JButton(BUTTON_SAVE);
		buttonSave.addActionListener(new SaveAction());
		footerPanel.add(buttonSave);

		// Cancel
		buttonCancel = new JButton(BUTTON_CANCEL);
		buttonCancel.addActionListener(new CancelAction());
		footerPanel.add(buttonCancel);

		add(middlePanel, BorderLayout.CENTER);
		add(footerPanel, BorderLayout.PAGE_END);

		// load data
		reload();

		setAlwaysOnTop(true);
		pack();
		setVisible(true);

		// action on close
		addWindowListener(new WindowCloseListener());
	}

	/**
	 * Creates the midi in devices combo box for the remote switch with a label
	 */
	private void createRemoteMidiInDevices() {

		GridBagConstraints c = new GridBagConstraints();
		midiINRemoteDevicesLabel = new JLabel(LABEL_MIDI_IN_REMOTE_DEVICES);
		c.insets = INSETS_LABEL_HEADER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		middlePanel.add(midiINRemoteDevicesLabel, c);

		List<String> midiINDevices = MidiUtils.getMidiDeviceSignatures("IN");
		midiINDevices.add(0, MidiAutomatorProperties.VALUE_NULL);
		midiINRemoteDeviceComboBox = new JComboBox<String>(
				(String[]) midiINDevices.toArray(new String[0]));
		midiINRemoteDeviceComboBox
				.setName(MIDI_IN_METRONROM_DEVICE_COMBO_BOX_NAME);

		c = new GridBagConstraints();
		c.insets = INSETS_COMBO_BOX;
		c.fill = CONSTRAINT_FILL_COMBO_BOX;
		c.anchor = CONSTRAINT_ANCHOR_COMBO_BOX;
		c.gridx = 0;
		c.gridy = 1;
		middlePanel.add(midiINRemoteDeviceComboBox, c);

	}

	/**
	 * Creates the midi out devices combo box for the remote switch with a label
	 */
	private void createRemoteMidiOutDevices() {

		GridBagConstraints c = new GridBagConstraints();
		midiOUTRemoteDevicesLabel = new JLabel(LABEL_MIDI_OUT_REMOTE_DEVICES);
		c.insets = INSETS_LABEL_HEADER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		middlePanel.add(midiOUTRemoteDevicesLabel, c);

		List<String> midiOutDevices = MidiUtils.getMidiDeviceSignatures("OUT");
		midiOutDevices.add(0, MidiAutomatorProperties.VALUE_NULL);
		midiOUTRemoteDeviceComboBox = new JComboBox<String>(
				(String[]) midiOutDevices.toArray(new String[0]));
		midiOUTRemoteDeviceComboBox
				.setName(MIDI_OUT_REMOTE_DEVICE_COMBO_BOX_NAME);

		c = new GridBagConstraints();
		c.insets = INSETS_COMBO_BOX;
		c.fill = CONSTRAINT_FILL_COMBO_BOX;
		c.anchor = CONSTRAINT_ANCHOR_COMBO_BOX;
		c.gridx = 1;
		c.gridy = 1;
		middlePanel.add(midiOUTRemoteDeviceComboBox, c);

		// Info label
		c = new GridBagConstraints();
		c.insets = INSETS_LABEL_INFO;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		midiOUTSwitchNotifierInfoLabel = new HTMLLabel(
				"<span style='font-family:Arial; font-size:8px'>"
						+ LABEL_MIDI_OUT_REMOTE_OPEN + "</span>");
		middlePanel.add(midiOUTSwitchNotifierInfoLabel, c);
	}

	/**
	 * Creates the midi out devices combo box for the switch notifier with a
	 * label
	 */
	private void createSwitchNotifierMidiOutDevices() {

		GridBagConstraints c = new GridBagConstraints();
		midiOUTSwitchNotifierDevicesLabel = new JLabel(
				LABEL_MIDI_OUT_SWITCH_NOTIFIER_DEVICES);
		c.insets = INSETS_LABEL_HEADER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		middlePanel.add(midiOUTSwitchNotifierDevicesLabel, c);

		List<String> midiOutDevices = MidiUtils.getMidiDeviceSignatures("OUT");
		midiOutDevices.add(0, MidiAutomatorProperties.VALUE_NULL);
		midiOUTSwitchNotifierDeviceComboBox = new JComboBox<String>(
				(String[]) midiOutDevices.toArray(new String[0]));
		midiOUTSwitchNotifierDeviceComboBox
				.setName(MIDI_OUT_SWITCH_NOTIFIER_DEVICE_COMBO_BOX_NAME);

		c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = CONSTRAINT_FILL_COMBO_BOX;
		c.anchor = CONSTRAINT_ANCHOR_COMBO_BOX;
		c.gridx = 2;
		c.gridy = 1;
		middlePanel.add(midiOUTSwitchNotifierDeviceComboBox, c);

		// Button test notification
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 3;
		c.gridy = 1;
		buttonSendNotify = new JButton(BUTTON_SEND_NOTIFIER);
		buttonSendNotify.addActionListener(new SendNotificationAction());
		buttonSendNotify.setMaximumSize(new Dimension(7, 10));
		middlePanel.add(buttonSendNotify, c);

		// Info label
		c = new GridBagConstraints();
		c.insets = INSETS_LABEL_INFO;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		midiOUTSwitchNotifierInfoLabel = new HTMLLabel(
				"<span style='font-family:Arial; font-size:8px'>"
						+ LABEL_MIDI_OUT_SWITCH_NOTIFIER_INFO + "</span>");
		middlePanel.add(midiOUTSwitchNotifierInfoLabel, c);
	}

	/**
	 * Creates the midi out devices combo box for item switch with a label.
	 */
	private void createSwitchItemOutDevices() {

		GridBagConstraints c = new GridBagConstraints();
		midiOUTSwitchItemDevicesLabel = new JLabel(
				LABEL_MIDI_OUT_SWITCH_ITEM_DEVICES);
		c.insets = INSETS_LABEL_HEADER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		middlePanel.add(midiOUTSwitchItemDevicesLabel, c);

		List<String> devices = MidiUtils.getMidiDeviceSignatures("OUT");
		devices.add(0, MidiAutomatorProperties.VALUE_NULL);
		midiOUTSwitchItemDeviceComboBox = new JComboBox<String>(
				(String[]) devices.toArray(new String[0]));
		midiOUTSwitchItemDeviceComboBox
				.setName(MIDI_OUT_SWITCH_ITEM_DEVICE_COMBO_BOX_NAME);

		c = new GridBagConstraints();
		c.insets = INSETS_COMBO_BOX;
		c.fill = CONSTRAINT_FILL_COMBO_BOX;
		c.anchor = CONSTRAINT_ANCHOR_COMBO_BOX;
		c.gridx = 0;
		c.gridy = 4;
		middlePanel.add(midiOUTSwitchItemDeviceComboBox, c);

		// Info label
		c = new GridBagConstraints();
		c.insets = INSETS_LABEL_INFO;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		midiOUTSwitchItemInfoLabel = new HTMLLabel(
				"<span style='font-family:Arial; font-size:8px'>"
						+ this.LABEL_MIDI_OUT_SWITCH_ITEM_INFO + "</span>");
		middlePanel.add(midiOUTSwitchItemInfoLabel, c);
	}

	/**
	 * Creates the midi in devices combo box for metronom with a label.
	 */
	private void createMetronomMidiInDevices() {

		GridBagConstraints c = new GridBagConstraints();
		labelHeader = new JLabel(LABEL_MIDI_IN_METRONOM_DEVICES);
		c.insets = INSETS_LABEL_HEADER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		middlePanel.add(labelHeader, c);

		List<String> midiInDevices = MidiUtils.getMidiDeviceSignatures("IN");
		midiInDevices.add(0, MidiAutomatorProperties.VALUE_NULL);
		midiINMetronomDeviceComboBox = new JComboBox<String>(
				(String[]) midiInDevices.toArray(new String[0]));
		midiINMetronomDeviceComboBox
				.setName(MIDI_IN_REMOTE_DEVICE_COMBO_BOX_NAME);

		c = new GridBagConstraints();
		c.insets = INSETS_COMBO_BOX;
		c.fill = CONSTRAINT_FILL_COMBO_BOX;
		c.anchor = CONSTRAINT_ANCHOR_COMBO_BOX;
		c.gridx = 1;
		c.gridy = 4;
		middlePanel.add(midiINMetronomDeviceComboBox, c);

		// Info label
		c = new GridBagConstraints();
		c.insets = INSETS_LABEL_INFO;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 5;
		midiINMetronomInfoLabel = new HTMLLabel(
				"<span style='font-family:Arial; font-size:8px'>"
						+ LABEL_MIDI_IN_METRONOM_INFO + "</span>");
		middlePanel.add(midiINMetronomInfoLabel, c);
	}

	/**
	 * Creates the GUI automation configuration panel
	 */
	private void createGUIAutomation() {

		GridBagConstraints c = new GridBagConstraints();
		guiAutomationLabel = new JLabel(LABEL_GUI_AUTOMATION);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 7;
		middlePanel.add(guiAutomationLabel, c);

		GUI_AUTOMATION_PANEL.setOpaque(true);

		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 4;
		middlePanel.add(GUI_AUTOMATION_PANEL, c);
	}

	/**
	 * Sets a learned midi signature.
	 * 
	 * @param signature
	 *            The signature
	 * @param component
	 *            The learned component
	 */
	public void setMidiSignature(String signature, Component component) {

		// learning for automation list
		GUIAutomationConfigurationTable table = GUI_AUTOMATION_PANEL
				.getConfigurationTable();
		try {
			table.setMidiSignature(signature, table.getSelectedRow());
		} catch (AutomationIndexDoesNotExistException e) {
			log.error("The automation for the MIDI signature does not exist", e);
		}
	}

	/**
	 * Reloads the data of the frame
	 */
	public void reload() {

		// midi in remote opener
		String midiInRemoteDeviceName = APPLICATION
				.getMidiDeviceName(MidiAutomatorProperties.KEY_MIDI_IN_REMOTE_DEVICE);

		if (midiInRemoteDeviceName != null) {
			midiINRemoteDeviceComboBox.setSelectedItem(midiInRemoteDeviceName);
		}

		// midi out remote opener
		String midiOUTRemoteDevice = APPLICATION
				.getMidiDeviceName(MidiAutomatorProperties.KEY_MIDI_OUT_REMOTE_DEVICE);

		if (midiOUTRemoteDevice != null) {
			midiOUTRemoteDeviceComboBox.setSelectedItem(midiOUTRemoteDevice);
		}

		// midi in metronom
		String midiINMetronomDevice = APPLICATION
				.getMidiDeviceName(MidiAutomatorProperties.KEY_MIDI_IN_METRONOM_DEVICE);

		if (midiINMetronomDevice != null) {
			midiINMetronomDeviceComboBox.setSelectedItem(midiINMetronomDevice);
		}

		// midi out switch notifier
		String midiOUTSwitchNotifierDevice = APPLICATION
				.getMidiDeviceName(MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_NOTIFIER_DEVICE);

		if (midiOUTSwitchNotifierDevice != null) {
			midiOUTSwitchNotifierDeviceComboBox
					.setSelectedItem(midiOUTSwitchNotifierDevice);
		}

		// midi out item switch
		String midiOUTSwitchItemDevice = APPLICATION
				.getMidiDeviceName(MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_ITEM_DEVICE);

		if (midiOUTSwitchItemDevice != null) {
			midiOUTSwitchItemDeviceComboBox
					.setSelectedItem(midiOUTSwitchItemDevice);
		}

		// gui automations
		GUIAutomation[] guiAutomations = APPLICATION.getGUIAutomations();
		GUIAutomationConfigurationTable table = GUI_AUTOMATION_PANEL
				.getConfigurationTable();

		for (int i = 0; i < guiAutomations.length; i++) {
			table.setAutomation(guiAutomations[i].getImagePath(),
					guiAutomations[i].getType(),
					guiAutomations[i].getTrigger(),
					guiAutomations[i].getMinDelay(),
					guiAutomations[i].getMidiSignature(),
					guiAutomations[i].getMinSimilarity(),
					guiAutomations[i].isMovable(), i);
		}
	}

	/**
	 * Puts the frame to midi learn mode
	 * 
	 * @param learningComponent
	 *            The component to learn for
	 */
	public void midiLearnOn(JComponent learningComponent) {

		// disable inputs
		GUIUtils.disEnableAllInputs(this, false);

		GUI_AUTOMATION_PANEL.getPopupMenu().midiLearnOn();
		CacheableJTable table = GUI_AUTOMATION_PANEL.getConfigurationTable();

		if (learningComponent.getName() != null) {
			if (learningComponent.getName().equals(
					GUIAutomationConfigurationPanel.NAME_CONFIGURATION_TABLE)) {

				GUIUtils.deHighlightTableRow(table, true);

				log.info("Learning midi for automation index: "
						+ table.getSelectedRow());

			}
		}
	}

	/**
	 * Puts the frame to normal mode.
	 */
	public void midiLearnOff() {

		// enable inputs
		GUIUtils.disEnableAllInputs(this, true);

		// change menu item
		GUI_AUTOMATION_PANEL.getPopupMenu().midiLearnOff();

		GUIUtils.deHighlightTableRow(
				GUI_AUTOMATION_PANEL.getConfigurationTable(), false);
	}

	public GUIAutomationConfigurationPanel getGuiAutomationPanel() {
		return GUI_AUTOMATION_PANEL;
	}

	/**
	 * Closes the preferences window, saves and loads the configuration.
	 * 
	 * @author aguelle
	 * 
	 */
	class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			String midiINRemoteDeviceName = (String) midiINRemoteDeviceComboBox
					.getSelectedItem();
			String midiOUTRemoteDeviceName = (String) midiOUTRemoteDeviceComboBox
					.getSelectedItem();
			String midiINMetronomDeviceName = (String) midiINMetronomDeviceComboBox
					.getSelectedItem();
			String midiOUTSwitchNotifierDeviceName = (String) midiOUTSwitchNotifierDeviceComboBox
					.getSelectedItem();
			String midiOUTSwitchItemDeviceName = (String) midiOUTSwitchItemDeviceComboBox
					.getSelectedItem();
			GUIAutomation[] guiAutomations = GUI_AUTOMATION_PANEL
					.getGUIAutomations();

			APPLICATION.setMidiDeviceName(midiINRemoteDeviceName,
					MidiAutomatorProperties.KEY_MIDI_IN_REMOTE_DEVICE);
			APPLICATION.setMidiDeviceName(midiINMetronomDeviceName,
					MidiAutomatorProperties.KEY_MIDI_IN_METRONOM_DEVICE);
			APPLICATION.setMidiDeviceName(midiOUTRemoteDeviceName,
					MidiAutomatorProperties.KEY_MIDI_OUT_REMOTE_DEVICE);
			APPLICATION
					.setMidiDeviceName(
							midiOUTSwitchNotifierDeviceName,
							MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_NOTIFIER_DEVICE);
			APPLICATION.setMidiDeviceName(midiOUTSwitchItemDeviceName,
					MidiAutomatorProperties.KEY_MIDI_OUT_SWITCH_ITEM_DEVICE);
			APPLICATION.setGUIAutomations(guiAutomations);

			new CancelAction().actionPerformed(e);
			new PropertiesReloadThread(APPLICATION).start();
		}
	}

	/**
	 * Closes the preferences window.
	 * 
	 * @author aguelle
	 * 
	 */
	class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Component component = (Component) e.getSource();
			JFrame frame = (JFrame) SwingUtilities.getRoot(component);
			WindowEvent windowClosing = new WindowEvent(frame,
					WindowEvent.WINDOW_CLOSING);
			frame.dispatchEvent(windowClosing);
		}
	}

	/**
	 * Sends a notification through the chosen midi device.
	 * 
	 * @author aguelle
	 * 
	 */
	class SendNotificationAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {

			String deviceName = (String) midiOUTSwitchNotifierDeviceComboBox
					.getSelectedItem();
			try {
				MidiDevice device = MidiUtils.getMidiDevice(deviceName, "OUT");
				APPLICATION.sendItemChangeNotifier(device);
			} catch (MidiUnavailableException ex) {
				log.error(
						"The MIDI device for the switch notification is unavailable",
						ex);
			}
		}
	}

	/**
	 * All actions that shall be done when window is closed
	 * 
	 * @author aguelle
	 * 
	 */
	class WindowCloseListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
			APPLICATION.setGUIAutomationsToActive(true);
		}
	}
}
