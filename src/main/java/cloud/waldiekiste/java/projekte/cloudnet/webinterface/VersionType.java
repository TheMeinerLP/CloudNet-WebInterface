package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

public enum VersionType {
    DEVELOPMENT_SECURITY("DEVELOPMENT_SECURITY"),
    SNAPSHOT_SECURITY("SNAPSHOT_SECURITY"),
    RELEASE_SECURITY("RELEASE_SECURITY"),
    DEVELOPMENT("DEVELOPMENT"),
    SNAPSHOT("SNAPSHOT"),
    RELEASE("RELEASE");

    private String type;

    VersionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
