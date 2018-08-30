package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

public class UpdateData {
    private String version,path;
    private VersionType versionType;

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
