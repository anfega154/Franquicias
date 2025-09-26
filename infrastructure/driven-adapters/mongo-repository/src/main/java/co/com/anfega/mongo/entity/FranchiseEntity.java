package co.com.anfega.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "franchises")
public class FranchiseEntity {

    @Id
    private String id;

    private String name;

}
