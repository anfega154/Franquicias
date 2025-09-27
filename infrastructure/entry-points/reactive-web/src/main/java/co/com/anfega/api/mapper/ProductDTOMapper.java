package co.com.anfega.api.mapper;

import co.com.anfega.api.dto.CreateProductDTO;
import co.com.anfega.api.dto.ProductDTO;
import co.com.anfega.model.product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDTOMapper {
    ProductDTO toResponse(Product product);
    Product toModel(CreateProductDTO createProductDTO);
}
