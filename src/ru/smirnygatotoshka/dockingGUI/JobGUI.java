package ru.smirnygatotoshka.dockingGUI;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JobGUI extends Tab {
    public Button start, stop, saveLog, cleanLog,state;
    public TextArea log;
    public ProgressBar progressDocking, progressAnalysis;
    public Label procentDocking, procentAnalysis;
    private DockingServer dockingServer;
    protected Configuration configuration;
    private TaskInfo taskInfo;
    private Process process;
    private Thread docking,server,writerOutput,writerError;
    private ConfigurationGUI gui;

    public JobGUI(Stage stage, ConfigurationGUI c) {
        super(stage);
        this.configuration = Configuration.getInstance();
        this.gui = c;
        this.docking = new Thread();
        this.server = new Thread();
        this.writerOutput = new Thread();
        this.writerError = new Thread();
        //this.taskInfo = new TaskInfo();
    }

    @Override
    protected void init() {

        executor = new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    switchStart(true);
                    task = new Job<>();
                    taskInfo = new TaskInfo();
                    task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            String date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
                            log.appendText("Docking was cancelled at " + date + "\n");
                            switchStart(false);
                            stopAllThreads();
                        }
                    });
                    task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {

                            String date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
                            log.appendText("Docking was failed at " + date + "\n");
                            log.appendText("Cause " + task.getException().getMessage() + "\n");
                            stopAllThreads();
                            switchStart(false);
                        }
                    });
                    task.setOnRunning(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            String date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
                            log.appendText("Docking run at " + date + "\n");

                        }
                    });
                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            String date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date());
                            log.appendText("Docking was succeeded at " + date + "\n");
                            stopAllThreads();
                            switchStart(false);
                        }
                    });
                    docking = new Thread(task);
                    docking.setName("Task");
                    docking.setDaemon(true);
                    docking.start();

                }
                catch (Exception e)
                {
                    stopAllThreads();
                    docking.interrupt();
                    e.printStackTrace();
                    Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };

        /*stopper = new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    switchStart(false);
                    task.cancel();
                    if (taskInfo.getJobID() != null) {
                        Runtime rt = Runtime.getRuntime();
                        Process p = rt.exec(configuration.getHadoop() + File.separator + "bin/hadoop -kill " + taskInfo.getJobID());
                        if (p.waitFor() != 0) throw new Exception("Cannot kill job. Stop it manually.");
                    }
                    //TODO - make "yarn application -kill <appId>"
                }
                catch (Exception e)
                {
                    Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };*/
        saver = new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    File file = FileChooserFactory.createSaveFileChooser("Сохранить журнал");
                    if (file != null) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(log.getText());
                        writer.close();
                    }
                } catch (IOException e) {
                    Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        };

        cleaner = new EventHandler() {
            @Override
            public void handle(Event event) {
                log.setText("");
            }
        };
        start = (Button) stage.getScene().lookup("#start");
        start.setOnAction(executor);

        /*stop = (Button) stage.getScene().lookup("#stop");
        stop.setOnAction(stopper);*/

        saveLog = (Button) stage.getScene().lookup("#saveLog");
        saveLog.setOnAction(saver);

        cleanLog = (Button) stage.getScene().lookup("#cleanLog");
        cleanLog.setOnAction(cleaner);

        state = (Button) stage.getScene().lookup("#state");
        state.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 Dialog.getDialog("Info","Docking = " + docking.isAlive() + "\n" +
                            "Server = " + server.isAlive() + "\n" +
                            "wrOutput = " + writerOutput.isAlive() + "\n" +
                            "wrError = " + writerError.isAlive() + "\n", Alert.AlertType.INFORMATION);
            }
        });

        log = (TextArea) stage.getScene().lookup("#log");

        progressDocking = (ProgressBar) stage.getScene().lookup("#progressDocking");
        progressAnalysis = (ProgressBar) stage.getScene().lookup("#progressAnalysis");

        procentDocking = (Label) stage.getScene().lookup("#procentDocking");
        procentAnalysis = (Label) stage.getScene().lookup("#procentAnalysis");

    }

    private void stopAllThreads()
    {
        try {
            dockingServer.close();
            taskInfo.closeService();
        } catch (IOException e) {
            Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
        }
        if (process.isAlive())
            process.destroy();
        if (docking.isAlive())
            docking.interrupt();
        if (server.isAlive())
            server.interrupt();
        if (writerError.isAlive())
            writerError.interrupt();
        if (writerOutput.isAlive())
            writerOutput.interrupt();
    }
    public Label getProcentDocking() {
        return procentDocking;
    }//TODO - count flag meesages from client

    public Label getProcentAnalysis() {
        return procentAnalysis;
    }

    public TextArea getLog() {
        return log;
    }

    public ProgressBar getProgressDocking() {
        return progressDocking;
    }

    public ProgressBar getProgressAnalysis() {
        return progressAnalysis;
    }
    /**
     * @param disable_start true - start = off*/
    private void switchStart(boolean disable_start)
    {
        start.setDisable(disable_start);
        //stop.setDisable(!disable_start);
    }

    private EventHandler executor;

    private EventHandler stopper;
    private EventHandler saver;

    private EventHandler cleaner;


    private Job<Integer> task;

    public class Job<V extends Integer> extends Task<V>
    {
        @Override
        protected V call() throws Exception {
            this.running();

            dockingServer = new DockingServer(null, 4445);
            Runnable run_server = new Runnable() {
                @Override
                public void run() {
                    try {
                        Platform.runLater(() -> log.appendText("Server is started\n"));
                        dockingServer.listen();//TODO - Server fall after 1 connection closed. Repair + send one message not many

                    } catch (Exception e) {
                        Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
                    }
                    Platform.runLater(() -> log.appendText("Server is closed\n"));
                }
            };
            server = new Thread(run_server);
            configuration = gui.getFromFields();
            Runtime rt = Runtime.getRuntime();
            String[] arguments = configuration.getArguments();
            String args = "";
            for (String s : arguments)
                args += s + " ";
            String command = configuration.getHadoop() + File.separator + Configuration.LAUNCH_COMMAND + configuration.getJar() + " " + args;
            process = rt.exec(command);
            Platform.runLater(() -> log.appendText(command + "\n"));
            Platform.runLater(() -> log.appendText("Task is started\n"));
            server.setName("Message server");
            server.start();

            writerOutput = new Thread(new StreamWriter(process.getInputStream(), "INPUT"));
            writerOutput.setName("writer output");

            writerError = new Thread(new StreamWriter(process.getErrorStream(), "INFO"));
            writerOutput.setName("writer error");

            writerOutput.start();
            writerError.start();

            int result = process.waitFor();
            Integer i = new Integer(result);
            log.appendText("Task is finished with code = " + result + "\n");
            if (result != 0) throw new Exception("Something went wrong. Task haven`t done. Error code = " + result);
            return (V) i;
        }
    }
    /**
     * @author DeepSidhu1313
     * */
    private class StreamWriter implements Runnable{
        private InputStream stream;
        private String type;

        public StreamWriter(InputStream stream, String type) {
            this.stream = stream;
            this.type = type;
        }

        @Override
        public void run() {
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    String s = line;
                    Platform.runLater(() -> log.appendText(type + ": " + s + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class DockingServer {
        private ServerSocket server;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        public DockingServer(String ipAddress,int port) throws Exception {
            if (ipAddress != null && !ipAddress.isEmpty())
                this.server = new ServerSocket(port, 1, InetAddress.getByName(ipAddress));
            else
                this.server = new ServerSocket(port, 1, InetAddress.getLocalHost());
        }
        public void listen() throws Exception {
            server.setSoTimeout(0);
            while (process.isAlive())
            {
                try {
                    Socket client = server.accept();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                writeToLog(client);
                                taskInfo.update();
                                Main.getListGUI().update(taskInfo.getDockInfo());
                                double progress = (taskInfo.getFailed() + taskInfo.getSuccessful()) / ((double)taskInfo.getAll());
                                progressDocking.setProgress(progress);
                                long ready = taskInfo.getFailed() + taskInfo.getSuccessful();
                                Platform.runLater(() ->procentDocking.setText(ready + "/" + taskInfo.getAll()));

                            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                                Platform.runLater(() -> log.appendText(e.getMessage()));
                            }
                        }
                    });
                }
                catch (SocketException e)
                {
                    Platform.runLater(() -> log.appendText("Connection has closed."));
                }
            }
            executorService.shutdown();
        }
        private void writeToLog(Socket client) throws IOException {
          /*  synchronized (log)
            {*/
                String clientName = client.getInetAddress().getHostName();
                Platform.runLater(() -> log.appendText("\r\nNew connection from " + clientName));
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                try{
                    String data = "";
                    while ((data = in.readLine()) != null ) {
                        String s = data;
                        if (isServiceString(s))
                            taskInfo.addMessage(s);
                      //else
                            Platform.runLater(()-> log.appendText("\r\n" + s+ "\r\n"));
                    }
                    in.close();
                }
                catch (IOException e) {
                     Platform.runLater(()-> Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR));
                }
           // }
        }

        private boolean isServiceString(String data) {
            return data.contains(TaskInfo.SERVICE_STRING);
        }

        public void close() throws IOException {
            this.server.close();
        }
        public InetAddress getSocketAddress() {
            return this.server.getInetAddress();
        }

        public int getPort() {
            return this.server.getLocalPort();
        }

    }
}

