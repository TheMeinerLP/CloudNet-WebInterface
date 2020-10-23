package me.madfix.cloudnet.webinterface.api.update;

import me.madfix.cloudnet.webinterface.WebInterface;
import me.madfix.cloudnet.webinterface.api.sql.SQLInsertConstants;
import me.madfix.cloudnet.webinterface.api.sql.SQLSelectConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Handle some update/migations from previous versions
 * @version 1.0.0
 * @since 1.11.5
 */
public final class UpdateHandler {

    private final TreeMap<Integer, UpdateTask> taskTreeMap = new TreeMap<>();

    private final WebInterface webInterface;

    public UpdateHandler(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public void addTask(int order, UpdateTask task) {
        this.taskTreeMap.putIfAbsent(order, task);
        this.webInterface.getLogger().log(Level.INFO, "The update {0} was added!", new Object[]{task.getVersion()});
    }

    /**
     * Call some updates in the right order
     */
    public void callUpdates() {
        taskTreeMap.forEach((key, task) -> isUpdateInstalled(task.getVersion()).thenAccept(check -> {
            if (!check) {
                task.preUpdateStep(this.webInterface).thenAccept(stepOne -> {
                    long start = System.currentTimeMillis();
                    if (stepOne) {
                        task.updateStep(this.webInterface).thenAccept(stepTwo -> {
                            if (stepTwo) {
                                task.postUpdateStep(this.webInterface).thenAccept(stepThree -> {
                                    if (stepThree) {
                                        setUpdateInstalled(task.getVersion(), true).thenAccept(successfulInstalled -> {
                                            if (successfulInstalled) {
                                                long end = System.currentTimeMillis();
                                                long diff = end - start;
                                                this.webInterface.getLogger()
                                                                 .log(Level.INFO,
                                                                      "The update {0} could be applied and took {1} milliseconds",
                                                                      new Object[]{task.getVersion(), diff});
                                            } else {
                                                this.webInterface.getLogger()
                                                                 .log(Level.WARNING,
                                                                      "The update {0} could not be applied",
                                                                      new Object[]{task.getVersion()});
                                            }
                                        });
                                    } else {
                                        this.webInterface.getLogger()
                                                         .log(Level.WARNING,
                                                              "The update {0} could not be applied",
                                                              new Object[]{task.getVersion()});
                                    }
                                });
                            } else {
                                this.webInterface.getLogger()
                                                 .log(Level.WARNING,
                                                      "The update {0} could not be applied",
                                                      new Object[]{task.getVersion()});
                            }
                        });
                    } else {
                        this.webInterface.getLogger()
                                         .log(Level.WARNING,
                                              "The update {0} could not be applied",
                                              new Object[]{task.getVersion()});
                    }
                });
            } else {
                this.webInterface.getLogger()
                                 .log(Level.INFO,
                                      "Update {0} already installed. Skip Update!",
                                      new Object[]{task.getVersion()});
            }
        }));
    }

    /**
     * Set or insert a entry into database to mark is the update successfully installed
     * @param version to insert/set
     * @param apply to set was successfully or not
     * @return true if the operation successfully
     */
    private CompletableFuture<Boolean> setUpdateInstalled(String version, boolean apply) {
        CompletableFuture<Boolean> installed = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (Connection c = connection;
                     PreparedStatement statement = c.prepareStatement(SQLInsertConstants.INSERT_UPDATE)) {
                    statement.setString(1, version);
                    statement.setBoolean(2, apply);
                    installed.complete(statement.executeUpdate() > 0);
                } catch (SQLException e) {
                    this.webInterface.getLogger()
                                     .log(Level.SEVERE,
                                          "The update for the user could not be selected from the database",
                                          e);
                    installed.cancel(true);
                }
            });
        }
        return installed;
    }

    /**
     * Check bases on string version, is the update installed
     * @param version to check
     * @return a boolean with true is the update installed
     */
    private CompletableFuture<Boolean> isUpdateInstalled(String version) {
        CompletableFuture<Boolean> installed = new CompletableFuture<>();
        if (this.webInterface.getConfigurationService().getOptionalInterfaceConfiguration().isPresent()) {
            this.webInterface.getDatabaseService().getConnection().ifPresent(connection -> {
                try (Connection c = connection;
                     PreparedStatement statement = c.prepareStatement(SQLSelectConstants.SELECT_UPDATE)) {
                    statement.setString(1, version);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            installed.complete(resultSet.getBoolean("apply"));
                        } else {
                            installed.complete(false);
                        }
                    }
                } catch (SQLException e) {
                    this.webInterface.getLogger()
                                     .log(Level.SEVERE,
                                          "The update for the user could not be selected from the database ",
                                          e);
                    installed.cancel(true);
                }
            });
        }
        return installed;
    }

}
