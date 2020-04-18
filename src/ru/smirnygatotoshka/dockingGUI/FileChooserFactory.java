/**
 * 
 */
package ru.smirnygatotoshka.dockingGUI;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author SmirnygaTotoshka
 *
 */
public class FileChooserFactory
{
	private static Configuration configuration;

	public static void setConfiguration(Configuration configuration) {
		FileChooserFactory.configuration = configuration;
	}

	private static FileChooser createFileChooser(String title) throws IOException
    {
		FileChooser fileChooser = new FileChooser();
		// Set title for FileChooser
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(configuration.getDefaultPath()));
		return fileChooser;
    }

    public static File createChooseDirectory(String title) throws IOException
    {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(title);
		dirChooser.setInitialDirectory(new File(configuration.getDefaultPath()));
		File file = dirChooser.showDialog(new Stage());
		if (file != null)
			configuration.setDefaultPath(file.getParent());
		return file;
    }

    public static File createChooseOneFile(String title) throws IOException
	{
		FileChooser fileChooser = createFileChooser(title);
		File file = fileChooser.showOpenDialog(new Stage());
		if (file != null)
			configuration.setDefaultPath(file.getParent());
		return file;
	}


	public static File createSaveFileChooser(String title) throws IOException
	{
		FileChooser fileChooser = createFileChooser(title);
		File file = fileChooser.showSaveDialog(new Stage());
		if (file != null)
			configuration.setDefaultPath(file.getParent());
		return file;
	}
    /*
     * public List<File> createMultipyFileChooser(String title) { FileChooser
     * fileChooser = new FileChooser(); // Set title for FileChooser
     * fileChooser.setTitle(title); fileChooser.setInitialDirectory(new
     * File(defaultPath)); // Add Extension Filters
     * fileChooser.getExtensionFilters().addAll(new
     * FileChooser.ExtensionFilter("All Files", "*.*"), new
     * FileChooser.ExtensionFilter("DLG", "*.dlg")); List<File> files =
     * fileChooser.showOpenMultipleDialog(new Stage()); defaultPath =
     * files.get(files.size() - 1).getParent(); return files; }
     * 
     * // Save Dialog return path to save }
     */
}
