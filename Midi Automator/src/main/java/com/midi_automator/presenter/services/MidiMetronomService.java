package com.midi_automator.presenter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.midi_automator.view.frames.MainFrame;

/**
 * Handles all metronom functions.
 * 
 * @author aguelle
 *
 */
@Service
public class MidiMetronomService {

	public static final String METRONOM_FIRST_CLICK_MIDI_SIGNATURE = "channel 16: NOTE ON A4";
	public static final String METRONOM_CLICK_MIDI_SIGNATURE = "channel 16: NOTE ON E4";

	@Autowired
	private MainFrame mainFrame;

	/**
	 * Executes the midi metronom's click
	 * 
	 * @beat the current clicked beat
	 */
	public void metronomClick(int beat) {
		mainFrame.blinkMetronom(beat);
	}
}