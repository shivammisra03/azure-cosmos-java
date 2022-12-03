package com.shivam.cosmos.repository;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosBulkItemResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.cosmos.model.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Repository
@Log4j2
public class EmployeeRepository {

    @Autowired
    CosmosAsyncContainer cosmosAsyncContainer;

    @Autowired
    ObjectMapper objectMapper;

    public Employee getEmployeeById(String id) {
        String query = "select * from c where c.id = \":empId\"";
        query = query.replace(":empId", id);
        List<Employee> employeeList = cosmosAsyncContainer.queryItems(query, null, Employee.class).collectList().block();
        if (!CollectionUtils.isEmpty(employeeList)) {
            return employeeList.get(0);
        }
        return Employee.builder().message(MessageFormat.format("No Employee exist with id : {0}", id)).build();
    }

    public void createEmployee(Employee employee) {
        log.info("Saving employee object in cosmos with data : {}", employee);

        cosmosAsyncContainer.createItem(employee, null).block();
    }

    public int bulk(List<JsonNode> employeeList) {
        AtomicReference<Double> totalRequestCharges = new AtomicReference<>(0.0);
        AtomicLong failureDocument = new AtomicLong(0);
        AtomicLong successDocument = new AtomicLong(0);
        Flux<CosmosItemOperation> cosmosItemOperations = Flux.fromIterable(employeeList).map(document ->
                CosmosBulkOperations.getUpsertItemOperation(document, new PartitionKey(document.get("id").textValue())));

        cosmosAsyncContainer.executeBulkOperations(cosmosItemOperations).retryWhen(
                reactor.util.retry.Retry.backoff(5, Duration.ofSeconds(3))
                        .jitter(new SecureRandom().nextDouble())
                        .doAfterRetry((Retry.RetrySignal retrySpec) ->
                                log.warn("do after retry" + LocalDateTime.now()))
                        .onRetryExhaustedThrow(
                                (spec, retrySpec) -> {
                                    log.error("Retry exhausted");
                                    return retrySpec.failure();
                                }
                        )
        ).flatMap(cosmosBulkOperationResponse -> {
            CosmosBulkItemResponse cosmosBulkItemResponse = cosmosBulkOperationResponse.getResponse();
            CosmosItemOperation cosmosItemOperation = cosmosBulkOperationResponse.getOperation();

            log.debug("Bulk Create Response Success : [{}]", cosmosBulkOperationResponse.getResponse().isSuccessStatusCode());
            if (cosmosBulkOperationResponse.getException() != null) {
                failureDocument.getAndIncrement();
                log.error("Bulk operation Failed", cosmosBulkOperationResponse.getException());
            } else if (cosmosBulkOperationResponse.getResponse() != null && !cosmosBulkOperationResponse.getResponse().isSuccessStatusCode()) {
                failureDocument.getAndIncrement();
                log.error("Bulk operation for item : {}, and data : {} Failed with response code : {}",
                        cosmosItemOperation.<JsonNode>getItem().get("id"),
                        cosmosItemOperation.<JsonNode>getItem(),
                        cosmosBulkItemResponse.getStatusCode());
            } else {
                successDocument.getAndIncrement();
            }
            return Mono.just(cosmosBulkItemResponse);
        }).doOnError(Exception.class, exception -> failureDocument.getAndIncrement())
                .publishOn(Schedulers.boundedElastic()).blockLast();

        log.info("Bulk Item Upsert Completed - total success document [{}], Failure Documents [{}], Consumerd RUS [{}]",
                successDocument, failureDocument, totalRequestCharges);
        return successDocument.intValue();
    }
}
