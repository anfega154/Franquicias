package co.com.anfega.api.dto;

import lombok.Data;

@Data
public class CreateProductDTO {
    private String name;
    private Long stock;
    private String branchName;
    private String franchiseName;
}
