package com.nathancorp.pabrik.dto.request;

import com.nathancorp.pabrik.model.Paddy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBatchRequest {
    private Map<String, Double> paddyAndQuantity;
    private Double producedQuantity;
}
