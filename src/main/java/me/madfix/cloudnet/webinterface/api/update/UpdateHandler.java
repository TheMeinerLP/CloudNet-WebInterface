package me.madfix.cloudnet.webinterface.api.update;

import me.madfix.cloudnet.webinterface.WebInterface;

import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class UpdateHandler {

    private final TreeMap<Integer, UpdateTask> taskTreeMap = new TreeMap<>();

    private final WebInterface webInterface;

    public UpdateHandler(WebInterface webInterface) {
        this.webInterface = webInterface;
    }

    public CompletableFuture<UpdateTask> addTask(int order, UpdateTask task) {
        CompletableFuture<UpdateTask> taskCompletableFuture = new CompletableFuture<>();
        taskCompletableFuture.complete(this.taskTreeMap.putIfAbsent(order, task));
        this.webInterface.getLogger().log(Level.INFO, "The update {0} was added!", new Object[]{task.getVersion()});
        return taskCompletableFuture;
    }

    public CompletableFuture<Boolean> callUpdates() {
        CompletableFuture<Boolean> successful = new CompletableFuture<>();
        taskTreeMap.forEach((key, task) -> task.preUpdateStep(this.webInterface).thenAccept(stepOne -> {
            long start = System.currentTimeMillis();
            if (stepOne) {
                task.updateStep(this.webInterface).thenAccept(stepTwo -> {
                    if (stepTwo) {
                        task.postUpdateStep(this.webInterface).thenAccept(stepThree -> {
                            if (stepThree) {
                                long end = System.currentTimeMillis();
                                long diff = end - start;
                                this.webInterface.getLogger().log(Level.INFO, "The update {0} could be applied and took {1} milliseconds", new Object[]{task.getVersion(), diff});
                            } else {
                                this.webInterface.getLogger().log(Level.WARNING, "The update {0} could not be applied", new Object[]{task.getVersion()});
                            }
                        });
                    } else {
                        this.webInterface.getLogger().log(Level.WARNING, "The update {0} could not be applied", new Object[]{task.getVersion()});
                    }
                });
            } else {
                this.webInterface.getLogger().log(Level.WARNING, "The update {0} could not be applied", new Object[]{task.getVersion()});
            }
        }));
        successful.complete(true);
        return successful;
    }
}
