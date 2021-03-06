package com.midi_automator.presenter.services;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.midi_automator.Resources;
import com.midi_automator.model.MidiAutomatorProperties;
import com.midi_automator.model.Model;
import com.midi_automator.presenter.Presenter;
import com.midi_automator.utils.FileUtils;

/**
 * Service for importing and exporting .midauto files
 * 
 * @author aguelle
 *
 */
@Service
public class ImportExportService {

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Autowired
	private Presenter presenter;

	@Autowired
	private Resources resources;

	@Autowired
	private Model model;

	@Autowired
	private MidiAutomatorProperties properties;

	@Autowired
	private ItemListService fileListService;

	private String loadedMidautoFilePath;

	public static final String[] MIDI_AUTOMATOR_FILE_EXTENSIONS = { "midauto",
			"MIDAUTO" };
	public static final String MIDI_AUTOMATOR_FILE_TYPE = "Midi Automator (midauto)";

	/**
	 * Loads the set list and the properties from a zip file.
	 * 
	 * @param file
	 *            The zip file
	 */
	public void importMidautoFile(File file) {

		loadedMidautoFilePath = file.getAbsolutePath();
		String unzipPath = resources.getPropertiesPath();

		if (unzipPath.equals(File.separator)) {
			unzipPath = ".";
		}

		try {
			FileUtils.unzipFile(new ZipFile(file), unzipPath);
			presenter.loadProperties();
			fileListService.reloadSetList();
		} catch (ZipException e) {
			log.error("Unzipping file " + file.getAbsolutePath() + " failed.",
					e);
		} catch (IOException e) {
			log.error("Unzipping file " + file.getAbsolutePath() + " failed.",
					e);
		}
	}

	/**
	 * Saves the set list and the properties to a zip file.
	 * 
	 * @param filePath
	 *            The path to store the zip file
	 */
	public void exportMidautoFile(String filePath) {
		File[] files = new File[] { new File(model.getPersistenceFileName()),
				new File(properties.getPropertiesFilePath()) };
		try {
			FileUtils.zipFiles(files, filePath);
			loadedMidautoFilePath = filePath;
		} catch (ZipException e) {
			log.error("Zipping file " + filePath + " failed.", e);
		} catch (IOException e) {
			log.error("Writing to file " + filePath + " failed.", e);
		}
	}

	public String getLoadedMidautoFilePath() {
		return loadedMidautoFilePath;
	}

	public String getLoadedMidautoFileName() {
		if (loadedMidautoFilePath != null) {
			String[] splitted = loadedMidautoFilePath.split(File.separator);
			return splitted[splitted.length - 1];
		}
		return null;
	}
}
