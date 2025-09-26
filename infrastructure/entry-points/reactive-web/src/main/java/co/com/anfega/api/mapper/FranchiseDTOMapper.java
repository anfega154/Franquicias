package co.com.anfega.api.mapper;

import co.com.anfega.api.dto.CreateFranchiseDTO;
import co.com.anfega.api.dto.FranchiseDTO;
import co.com.anfega.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseDTOMapper {
    FranchiseDTO toResponse(Franchise franchise);
    Franchise toModel(CreateFranchiseDTO createFranchiseDTO);
}
