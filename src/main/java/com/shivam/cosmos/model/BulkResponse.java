package com.shivam.cosmos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkResponse {

    private int inputDocumentSize;
    private int documentInserted;

    @Override
    public String toString() {
        return "BulkResponse{" +
                "inputDocumentSize=" + inputDocumentSize +
                ", documentInserted=" + documentInserted +
                '}';
    }
}
