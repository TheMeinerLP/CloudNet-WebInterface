/*
 * Copyright (c) 2018.
 * Creative Commons Lizenzvertrag
 * CloudNet-Service-WebSocket-Extension von Phillipp Glanz ist lizenziert unter einer Creative Commons
 *  Namensnennung - Nicht kommerziell - Keine Bearbeitungen 4.0 International Lizenz.
 */

package cloud.waldiekiste.java.projekte.cloudnet.webinterface.utils;

public enum VersionType {

  BETA("Beta"),
  SNAPSHOT("Snapshot"),
  RELEASE("Release");

  private String type;

  VersionType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}