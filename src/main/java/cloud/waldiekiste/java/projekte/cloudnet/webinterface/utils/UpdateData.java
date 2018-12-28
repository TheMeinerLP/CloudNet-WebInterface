/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils;

public final class UpdateData {
    private String UrlPath;
    private String FilePath;
    private String FileName;
    private String name;
    private int version;
    private VersionType branch;
    private String extension;
    private String ENVIRONMENT;

    public UpdateData(String urlPath, String filePath, String fileName, String name, int version, VersionType branch, String extension, String ENVIRONMENT) {
        UrlPath = urlPath;
        FilePath = filePath;
        FileName = fileName;
        this.name = name;
        this.version = version;
        this.branch = branch;
        this.extension = extension;
        this.ENVIRONMENT = ENVIRONMENT;
    }

    public String getUrlPath() {
        return UrlPath;
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getFileName() {
        return FileName;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public VersionType getBranch() {
        return branch;
    }

    public String getExtension() {
        return extension;
    }

    public String getENVIRONMENT() {
        return ENVIRONMENT;
    }
}
