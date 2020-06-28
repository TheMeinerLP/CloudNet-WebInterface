package me.madfix.cloudnet.webinterface.model;

import java.util.Objects;

public final class DatabaseConfiguration {

  private final String jdbcUrl;
  private final String username;
  private final String password;
  private final int minimumIdle;
  private final int maximumPoolSize;
  private final int prepStmtCacheSize;
  private final int prepStmtCacheSqlLimit;
  private final boolean cachePrepStmts;
  private final long connectionTimeout;
  private final long idleTimeout;
  private final long maxLifetime;

  public DatabaseConfiguration(String jdbcUrl, String username, String password, int minimumIdle,
      int maximumPoolSize,
      int prepStmtCacheSize, int prepStmtCacheSqlLimit, boolean cachePrepStmts,
      long connectionTimeout,
      long idleTimeout,
      long maxLifetime) {
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
    this.minimumIdle = minimumIdle;
    this.maximumPoolSize = maximumPoolSize;
    this.prepStmtCacheSize = prepStmtCacheSize;
    this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    this.cachePrepStmts = cachePrepStmts;
    this.connectionTimeout = connectionTimeout;
    this.idleTimeout = idleTimeout;
    this.maxLifetime = maxLifetime;
  }

  public String getJdbcUrl() {
    return jdbcUrl;
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

  public int getPrepStmtCacheSize() {
    return prepStmtCacheSize;
  }

  public int getPrepStmtCacheSqlLimit() {
    return prepStmtCacheSqlLimit;
  }

  public boolean isCachePrepStmts() {
    return cachePrepStmts;
  }

  @Override
  public String toString() {
    return "DatabaseConfiguration{" +
        "jdbcUrl='" + jdbcUrl + '\'' +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", minimumIdle=" + minimumIdle +
        ", maximumPoolSize=" + maximumPoolSize +
        ", prepStmtCacheSize=" + prepStmtCacheSize +
        ", prepStmtCacheSqlLimit=" + prepStmtCacheSqlLimit +
        ", cachePrepStmts=" + cachePrepStmts +
        ", connectionTimeout=" + connectionTimeout +
        ", idleTimeout=" + idleTimeout +
        ", maxLifetime=" + maxLifetime +
        '}';
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
        prepStmtCacheSize == that.prepStmtCacheSize &&
        prepStmtCacheSqlLimit == that.prepStmtCacheSqlLimit &&
        cachePrepStmts == that.cachePrepStmts &&
        connectionTimeout == that.connectionTimeout &&
        idleTimeout == that.idleTimeout &&
        maxLifetime == that.maxLifetime &&
        Objects.equals(jdbcUrl, that.jdbcUrl) &&
        Objects.equals(username, that.username) &&
        Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(jdbcUrl, username, password, minimumIdle, maximumPoolSize, prepStmtCacheSize,
            prepStmtCacheSqlLimit, cachePrepStmts, connectionTimeout, idleTimeout, maxLifetime);
  }
}
