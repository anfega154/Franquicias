package co.com.anfega.api.mapper;

import co.com.anfega.api.dto.BranchDTO;
import co.com.anfega.api.dto.CreateBranchDTO;
import co.com.anfega.model.branch.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchDTOMapper {
    BranchDTO toResponse(Branch branch);
    Branch toModel(CreateBranchDTO createBranchDTO);
}
