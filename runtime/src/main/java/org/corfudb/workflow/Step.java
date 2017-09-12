package org.corfudb.workflow;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;

public class Step {

    public final UUID stepId = UUID.randomUUID();
    public final String name;


    private final Callable process;
    @Getter
    // Timeout in milliseconds
    private final long timeout;

    // Status of the Step
    public final CompletableFuture<Boolean> future = new CompletableFuture<>();

    public Step(String name, Callable process) {
        this(name, process, Long.MAX_VALUE);
    }

    public Step(String name, Callable process, long timeout) {
        this.name = name;
        this.process = process;
        this.timeout = timeout;
    }

    public CompletableFuture execute() {
        try {
            process.call();
            future.complete(true);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
