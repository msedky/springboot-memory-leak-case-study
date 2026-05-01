package org.jvmmemoryleak.case01.common.mapper;

import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.common.entity.ProductEntity;
import org.jvmmemoryleak.case01.common.payload.request.ProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(ProductEntity entity);

    ProductEntity toEntity(ProductDto dto);

    ProductEntity toEntity(ProductRequest request);

    void toEntity(ProductRequest request, @MappingTarget ProductEntity entity);
}
