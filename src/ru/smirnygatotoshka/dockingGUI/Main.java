package ru.smirnygatotoshka.dockingGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;


/**

 * @author SmirnygaTotoshka
 * @version 1.0
 * @since 02.05.2019
 */
public class Main extends Application
{
	private static ConfigurationGUI configurationGUI;
	private static JobGUI job;
	private static InformationListGUI listGUI;

	public static InformationListGUI getListGUI() {
		return listGUI;
	}

	public static ConfigurationGUI getConfigurationGUI() {
        return configurationGUI;
    }

    public static JobGUI getJob() {
        return job;
    }

    public static WebGUI getWeb() {
        return web;
    }

    private static WebGUI web;

	@Override
	public void stop() throws Exception {
		try {
			job.configuration = configurationGUI.getFromFields();
			job.configuration.serialize();
		}catch (IOException e)
		{
			Dialog.getDialog("Error","Cannot serialize config,because "+e.getMessage(), Alert.AlertType.ERROR);
		}
		catch (NullPointerException e)
		{
			Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
		}
		super.stop();
	}

	@Override
    public void start(Stage primaryStage) {
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass().getResource("load_dock.fxml"));
			primaryStage.setTitle("Launch Docking");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
			//primaryStage.setMaximized(true);
			configurationGUI = new ConfigurationGUI(primaryStage);
			job = new JobGUI(primaryStage, configurationGUI);
			job.configuration = Configuration.getInstance();
			if (Configuration.HaveExistingConfiguration())
				configurationGUI.write(job.configuration);
			FileChooserFactory.setConfiguration(job.configuration);
			web = new WebGUI(primaryStage);
			listGUI = new InformationListGUI(primaryStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		public static void main(String[] args)
    {
		launch(args);
    }


}
