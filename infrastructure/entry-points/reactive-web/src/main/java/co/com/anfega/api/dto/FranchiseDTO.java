package co.com.anfega.api.dto;

import co.com.anfega.model.branch.Branch;

import java.util.List;

public record FranchiseDTO (String id, String name, List<Branch> branches) {
}
