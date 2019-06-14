/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils;

public final class UpdateData {

    private String filePath;
    private String fileName;
    private String name;
    private long version;
    private String extension;

    public UpdateData(String filePath, String fileName, String name, long version, String extension) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.name = name;
        this.version = version;
        this.extension = extension;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public long getVersion() {
        return version;
    }

    public String getExtension() {
        return extension;
    }
}