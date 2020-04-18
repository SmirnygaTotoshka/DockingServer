package ru.smirnygatotoshka.dockingGUI;

import javafx.scene.control.Alert;

import java.io.*;

public class Configuration implements Serializable{
    private static final long serialVersionUID = -6218275384808837394L;

    public static final String LAUNCH_COMMAND = "bin" + File.separator + "hadoop jar ";

    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    public static final int NAME = 2;
    public static final int MGLTOOLS = 3;
    public static final int WORKSPACE = 4;
    public static final int HOST = 5;
    public static final int REDUCERS = 6;
    public static final int PREPARE_GPF = 7;

    private static Configuration configuration;

    public static Configuration getInstance()
    {
        if (configuration == null & HaveExistingConfiguration()) {
            try {
                configuration = deserialize();
            } catch (IOException  | ClassNotFoundException e) {
                Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
            }

        }
        else if (configuration == null)
            configuration = new Configuration();
        return configuration;
    }

    static boolean HaveExistingConfiguration()
    {
        return new File(PATH).exists();
    }
    private Configuration() {
        this.defaultPath = System.getProperty("user.home");
        this.arguments = new String[PREPARE_GPF + 1];
        this.hadoop = "";
        this.jar = "";
    }

    @Override
    public String toString() {
        return "Arguments:input = " + arguments[INPUT] + "\n"+
                "Output = " + arguments[OUTPUT] + "\n" +
                "Name = " + arguments[NAME] + "\n" +
                "MGLTools = " + arguments[MGLTOOLS] + "\n" +
                "Workspace = " + arguments[WORKSPACE] + "\n" +
                "Host HDFS = " + arguments[HOST] + "\n" +
                "Reducers = " + arguments[REDUCERS] + "\n" +
                "Prepare_gpf = " + arguments[PREPARE_GPF] + "\n" +
                "MapReduceDocking = " + jar + "\n" +
                "Hadoop = " + hadoop;
    }

    private static final String PATH = System.getProperty("user.home") + File.separator + "docking_conf.ser";

    private String[] arguments;
    private String hadoop;
    private String defaultPath;

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }


    public String getHadoop() {
        return hadoop;
    }

    public void setHadoop(String hadoop) {
        this.hadoop = hadoop;
    }

    private String jar;

    public String[] getArguments() {

        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }
    public void serialize() throws IOException
    {
        File file = new File(PATH);
        if (!file.exists())
        {
            boolean success = false;
            if (!file.exists())
                success = file.createNewFile();
            if (!success) throw new IOException("Cannot create file!");
        }
        else {
            FileOutputStream outputStream = new FileOutputStream(PATH);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(configuration);
            objectOutputStream.close();
        }
    }



    private static Configuration deserialize() throws IOException, ClassNotFoundException {

        File file = new File(PATH);
        if (!file.exists())
        {
            boolean success = false;
            if (!file.exists())
                success = file.createNewFile();
            if (!success) throw new IOException("Cannot create file!");
        }
        else {
            FileInputStream inputStream = new FileInputStream(PATH);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            return (Configuration) objectInputStream.readObject();
        }
        return new Configuration();
    }

}
