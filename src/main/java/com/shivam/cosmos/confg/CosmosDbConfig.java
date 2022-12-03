package com.shivam.cosmos.confg;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosAsyncDatabase;
import com.azure.cosmos.CosmosClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class CosmosDbConfig {

    @Value("${azure.documentdb.uri}")
    private String cosmosDatabaseEndpoint;

    @Value("${azure.documentdb.key}")
    private String cosmosDatabaseKey;

    private CosmosAsyncDatabase cosmosAsyncDatabase;

    public CosmosAsyncDatabase getCosmosAsyncDatabase() {
        return this.getCosmosAsyncClient().getDatabase("employeedb");
    }

    public CosmosAsyncClient getCosmosAsyncClient() {
        return (new CosmosClientBuilder()).endpoint(cosmosDatabaseEndpoint).key(cosmosDatabaseKey).consistencyLevel(ConsistencyLevel.EVENTUAL).contentResponseOnWriteEnabled(true).buildAsyncClient();
    }

    @Bean
    public CosmosAsyncContainer cosmosAsyncContainer(){
        return getCosmosAsyncContainer(cosmosAsyncDatabase, "employeedata");
    }

    private CosmosAsyncContainer getCosmosAsyncContainer(CosmosAsyncDatabase cosmosAsyncDatabase, String collectionName) {
        CosmosAsyncContainer cosmosAsyncContainerResponse = cosmosAsyncDatabase.getContainer(collectionName);
        if(cosmosAsyncContainerResponse == null){
            throw new IllegalArgumentException("Null cosmos container");
        } else {
            return cosmosAsyncContainerResponse;
        }
    }

    @PostConstruct
    void init() {
        cosmosAsyncDatabase = getCosmosAsyncDatabase();
    }

}
