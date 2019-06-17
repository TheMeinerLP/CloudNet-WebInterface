package cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils;

public final class UpdateData {

  private String filePath;
  private String fileName;
  private String name;
  private long version;
  private String extension;

  /**
   * A model of datas for the update service.
   * @param filePath The web path to the file
   * @param fileName The name of the file
   * @param name The update name
   * @param version The version of the update
   * @param extension The file extension
   */
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