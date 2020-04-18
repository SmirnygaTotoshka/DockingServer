package ru.smirnygatotoshka.dockingGUI;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TaskInfo
{
    public static final String SERVICE_STRING = "SERVICE:";
    private volatile long all,successful,failed;
    private BufferedWriter writerService;

    public ArrayList<DockInfo> getDockInfo() {
        return dockInfo;
    }

    private ArrayList<DockInfo> dockInfo;
    /*public String getJobID() {
        return jobID;
    }*/

   // private String jobID;
    private ArrayList<String> allServiceMessages;

    public synchronized void addMessage(String msg) {
        allServiceMessages.add(msg);
    }

    public void saveService(String path) {
       /* synchronized (allServiceMessages) {*/
            try {
                for (String i : allServiceMessages) {
                    writerService.append(i);
                    writerService.newLine();
                }
            } catch (IOException e) {
                Dialog.getDialog("Error", "Cannot save service state cause " + e.getMessage(), Alert.AlertType.ERROR);
            }
       // }
    }

    public TaskInfo() {
        this.dockInfo = new ArrayList<>();
        this.allServiceMessages = new ArrayList<>();
        all = successful = failed = 0;
        String path = Main.getConfigurationGUI().getFromFields().getArguments()[Configuration.WORKSPACE] + File.separator + "service.txt";
        try {
            writerService = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            Dialog.getDialog("Error", "Cannot create service.txt " + e.getMessage(), Alert.AlertType.ERROR);

        }
        //jobID = null;
    }

    public long getAll() {
        return all;
    }

    public long getSuccessful() {
        return successful;
    }

    public long getFailed() {
        return failed;
    }

    public synchronized void update() {
        if (allServiceMessages.size() > 0) {
            for (int i = 0; i < allServiceMessages.size(); i++) {
           /* if (allServiceMessages.get(i).contains("JOB ID:"))
                jobID = allServiceMessages.get(i).split(":")[2];*/
                if (allServiceMessages.get(i).contains("ALL="))
                    all = Long.parseLong(allServiceMessages.get(i).split("=")[1]);
                if (allServiceMessages.get(i).contains("REDUCE TASK")) {
                    String[] s = allServiceMessages.get(i).split(":");
                    String idSlave = s[1].split("=")[1];
                    String idTask = s[2].split("=")[1];
                    String path = s[3].split("=")[1];
                    String status = s[4].split("=")[1];
                    DockInfo inf = new DockInfo(idTask, idSlave, path, Boolean.parseBoolean(status));
                    if (inf.isStatus())
                        successful += 1;
                    else
                        failed += 1;
                    dockInfo.add(inf);
                }
            }
            saveService(Main.getConfigurationGUI().getFromFields().getArguments()[Configuration.WORKSPACE] + File.separator + "service.txt");
            allServiceMessages.removeAll(allServiceMessages);
        }
    }
public void closeService() throws IOException {
    writerService.close();
}

}
