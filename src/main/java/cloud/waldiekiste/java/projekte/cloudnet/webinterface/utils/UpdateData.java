/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils;

public final class UpdateData {
    private final String version,path;
    private final VersionType versionType;

    public UpdateData(String version, String path, VersionType versionType) {
        this.version = version;
        this.path = path;
        this.versionType = versionType;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public VersionType getVersionType() {
        return versionType;
    }
}
