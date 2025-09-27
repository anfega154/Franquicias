package co.com.anfega.api.dto;

import co.com.anfega.model.product.Product;

import java.util.List;

public record BranchDTO (String id, String name, List<Product> products) {
}
