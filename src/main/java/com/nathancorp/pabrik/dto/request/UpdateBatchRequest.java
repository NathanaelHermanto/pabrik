package com.nathancorp.pabrik.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBatchRequest {
    private boolean isProcessed;
    private Double producedQuantity;
}