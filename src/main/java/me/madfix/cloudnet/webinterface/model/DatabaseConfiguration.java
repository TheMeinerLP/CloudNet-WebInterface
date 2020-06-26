package me.madfix.cloudnet.webinterface.model;

import java.util.Objects;

public final class DatabaseConfiguration {

  private final String dataSourceClassName;
  private final String username;
  private final String password;
  private final int minimumIdle;
  private final int maximumPoolSize;
  private final long connectionTimeout;
  private final long idleTimeout;
  private final long maxLifetime;

  public DatabaseConfiguration(String dataSourceClassName, String username, String password,
      int minimumIdle, int maximumPoolSize, long connectionTimeout, long idleTimeout,
      long maxLifetime) {
    this.dataSourceClassName = dataSourceClassName;
    this.username = username;
    this.password = password;
    this.minimumIdle = minimumIdle;
    this.maximumPoolSize = maximumPoolSize;
    this.connectionTimeout = connectionTimeout;
    this.idleTimeout = idleTimeout;
    this.maxLifetime = maxLifetime;
  }

  public String getDataSourceClassName() {
    return dataSourceClassName;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public int getMinimumIdle() {
    return minimumIdle;
  }

  public int getMaximumPoolSize() {
    return maximumPoolSize;
  }

  public long getConnectionTimeout() {
    return connectionTimeout;
  }

  public long getIdleTimeout() {
    return idleTimeout;
  }

  public long getMaxLifetime() {
    return maxLifetime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatabaseConfiguration that = (DatabaseConfiguration) o;
    return minimumIdle == that.minimumIdle &&
        maximumPoolSize == that.maximumPoolSize &&
        connectionTimeout == that.connectionTimeout &&
        idleTimeout == that.idleTimeout &&
        maxLifetime == that.maxLifetime &&
        Objects.equals(dataSourceClassName, that.dataSourceClassName) &&
        Objects.equals(username, that.username) &&
        Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSourceClassName, username, password, minimumIdle, maximumPoolSize,
        connectionTimeout, idleTimeout, maxLifetime);
  }

  @Override
  public String toString() {
    return "DatabaseConfiguration{" +
        "dataSourceClassName='" + dataSourceClassName + '\'' +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", minimumIdle=" + minimumIdle +
        ", maximumPoolSize=" + maximumPoolSize +
        ", connectionTimeout=" + connectionTimeout +
        ", idleTimeout=" + idleTimeout +
        ", maxLifetime=" + maxLifetime +
        '}';
  }
}
