package org.jvmmemoryleak.case03.common.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryDetails {

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "beneficiary_account_number")
    private String accountNumber;

    @Column(name = "beneficiary_bank_name")
    private String bankName;

    @Column(name = "beneficiary_swift_bic")
    private String swiftBic;

    @Column(name = "beneficiary_routing_number")
    private String routingNumber;

    @Column(name = "beneficiary_country_code", length = 2)
    private String countryCode;

    @Column(name = "beneficiary_physical_address", length = 255)
    private String physicalAddress;
}