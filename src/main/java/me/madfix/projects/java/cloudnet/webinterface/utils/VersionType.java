package me.madfix.projects.java.cloudnet.webinterface.utils;

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