package com.midi_automator.view.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Base class of dialogs with save and cancel buttons.
 * 
 * @author aguelle
 *
 */
public abstract class AbstractBasicDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private final String BUTTON_SAVE = "Save";
	private final String BUTTON_CANCEL = "Cancel";

	public static String NAME_SAVE_BUTTON = "save button";
	public static String NAME_CANCEL_BUTTON = "cancel button";

	protected JButton buttonSave;
	protected JButton buttonCancel;
	protected ActionListener saveAction;
	protected ActionListener cancelAction;

	protected JPanel middlePanel;
	protected JPanel footerPanel;
	// The number of rows in the dialogs global grid layout
	protected int gridRows = 0;

	/**
	 * Initializes some basic dialog features.
	 */
	public void init() {
		setResizable(false);

		// set layout
		getContentPane().setLayout(new BorderLayout());
		middlePanel = new JPanel(new GridBagLayout());
		add(middlePanel, BorderLayout.CENTER);
		footerPanel = new JPanel(new FlowLayout());
		add(footerPanel, BorderLayout.PAGE_END);

		buttonSave = new JButton(BUTTON_SAVE);
		buttonSave.setName(NAME_SAVE_BUTTON);
		buttonCancel = new JButton(BUTTON_CANCEL);
		buttonCancel.setName(NAME_CANCEL_BUTTON);
	}

	/**
	 * Creates a text field
	 * 
	 * @param metaLabel
	 *            The label value to describe the text field
	 * @param textFieldName
	 *            The name of the text field
	 * @param metaWidth
	 *            The width of the meta label
	 * @param textFieldWidth
	 *            The width of the text field
	 * @return the created text field
	 */
	protected JTextField createTextField(String metaLabel,
			String textFieldName, int metaWidth, int textFieldWidth) {

		JLabel label = new JLabel(metaLabel);
		label.setPreferredSize(new Dimension(metaWidth, label
				.getPreferredSize().height));
		addComponentAtPosition(label, 0, gridRows);

		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(textFieldWidth, textField
				.getPreferredSize().height));
		textField.setName(textFieldName);
		addComponentAtPosition(textField, 1, gridRows);
		gridRows++;

		return textField;
	}

	/**
	 * Creates a label with meta information
	 * 
	 * @param metaLabel
	 *            The meta information
	 * @param labelName
	 *            The name of the label
	 * @param metaWidth
	 *            The width of the meta label
	 * @param labelWidth
	 *            The width of the label
	 * @return A named meta label
	 */
	protected JLabel createMetaLabel(String metaLabel, String labelName,
			int metaWidth, int labelWidth) {

		JLabel nameLabel = new JLabel(metaLabel);
		nameLabel.setPreferredSize(new Dimension(metaWidth, nameLabel
				.getPreferredSize().height));
		addComponentAtPosition(nameLabel, 0, gridRows);

		JLabel label = new JLabel("x");
		label.setName(labelName);
		label.setPreferredSize(new Dimension(labelWidth, label
				.getPreferredSize().height));
		addComponentAtPosition(label, 1, gridRows);
		gridRows++;

		return label;
	}

	/**
	 * Sets the component to the desired position
	 * 
	 * @param component
	 *            The component
	 * @param x
	 *            The X coordinate
	 * @param y
	 *            Thy Y coordinate
	 */
	protected void addComponentAtPosition(Component component, int x, int y) {

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		middlePanel.add(component, c);
	}
}
