package org.ead.inventorymanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(value = "inventory")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Inventory {
    @Id
    private ObjectId id;
    private String product_name;
    private String product_brand;
    private Double price;
    private Double available_quantity;

}
