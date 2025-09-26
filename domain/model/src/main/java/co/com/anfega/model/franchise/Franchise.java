package co.com.anfega.model.franchise;


import co.com.anfega.model.branch.Branch;

import java.util.Arrays;
import java.util.List;

public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches = Arrays.asList();

    public Franchise() {

    }

    public Franchise(String id, String name, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
