
package com.vozhe.jwt.payload.response;

import com.vozhe.jwt.models.warehouse.Distribution;
import com.vozhe.jwt.models.warehouse.Inventory;
import com.vozhe.jwt.models.warehouse.Processing;
import com.vozhe.jwt.models.warehouse.Receiving;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportsData {
    private List<Receiving> receivingRecords;
    private List<Processing> processingRecords;
    private List<Distribution> distributionRecords;
    private List<Inventory> inventory;
}
