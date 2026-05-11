package org.jvmmemoryleak.case03.common.mapper;

import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.common.model.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "beneficiary.beneficiaryName", target = "beneficiaryName")
    @Mapping(source = "beneficiary.bankName", target = "beneficiaryBankName")
    @Mapping(source = "beneficiary.countryCode", target = "beneficiaryCountryCode")
    TransactionDto toDto(TransactionEntity entity);
}