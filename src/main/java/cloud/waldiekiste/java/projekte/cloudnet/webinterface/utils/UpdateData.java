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
    private Long version;
    private VersionType branch;
    private String extension;
    private String environment;

    public UpdateData(String urlPath, String filePath, String fileName, String name, Long version, VersionType branch, String extension, String environment) {
        UrlPath = urlPath;
        FilePath = filePath;
        FileName = fileName;
        this.name = name;
        this.version = version;
        this.branch = branch;
        this.extension = extension;
        this.environment = environment;
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

    public Long getVersion() {
        return version;
    }

    public VersionType getBranch() {
        return branch;
    }

    public String getExtension() {
        return extension;
    }

    public String getEnvironment() {
        return environment;
    }
}
