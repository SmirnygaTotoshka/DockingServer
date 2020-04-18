package ru.smirnygatotoshka.dockingGUI;

public class DockInfo {
    private String id;
    private String slaveName;
    private String path;

    public String getId() {
        return id;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public String getPath() {
        return path;
    }

    public boolean isStatus() {
        return status;
    }

    public DockInfo(String id, String slaveName, String path,boolean status) {
        this.id = id;
        this.slaveName = slaveName;
        this.path = path;
        this.status = status;
        if (status)
            this.statusName = "Успех";
        else
            this.statusName = "Провал";
    }

    public String getStatusName() {
        return statusName;
    }

    private String statusName;
    private boolean status;
}

