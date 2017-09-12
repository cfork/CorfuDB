package org.corfudb.workflow;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Workflow {

    @Getter
    private final UUID workflowId = UUID.randomUUID();

    @Getter
    private final List<Step> workflow;

    private CompletableFuture workflowFuture = null;

    Workflow(WorkflowBuilder builder) {
        workflow = new ImmutableList.Builder<Step>()
                .addAll(builder.workflow)
                .build();
    }

    public Status getStatus() throws Exception {
        if (!workflowFuture.isDone()) {
            return Status.IN_PROGRESS;
        } else if (workflowFuture.isCancelled()) {
            return Status.ABORTED;
        } else if (workflowFuture.isCompletedExceptionally()) {
            workflowFuture.get();
        }
        return Status.COMPLETED;
    }

    public CompletableFuture executeAsync() {

        workflowFuture = CompletableFuture.supplyAsync(() -> {

            for (Step step : workflow) {
                if (!step.future.isDone()) {
                    try {
                        log.info("Executing workflow step: id:{}, name:{}", step.stepId, step.name);
                        if (step.getTimeout() == Long.MAX_VALUE) {
                            step.execute().get();
                        } else {
                            step.execute().get(step.getTimeout(), TimeUnit.MILLISECONDS);
                        }
                        log.info("Completed workflow step: id:{}, name:{}", step.stepId, step.name);
                    } catch (Exception e) {
                        log.error("Failed workflow step: id:{}, name:{} with exception:{}",
                                step.stepId, step.name, e);
                        workflowFuture.completeExceptionally(e);
                    }
                }
            }
            return true;
        });

        return workflowFuture;
    }


    @Data
    @Accessors(chain = true)
    public static class WorkflowBuilder {

        public List<Step> workflow = new ArrayList<>();

        public WorkflowBuilder addStep(Step step) {
            workflow.add(step);
            return this;
        }

        public Workflow build() {
            return new Workflow(this);
        }
    }

}
