package co.com.anfega.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "branches")
public class BranchEntity {
    @Id
    private String id;

    private String name;

    private String franchiseId;
}
