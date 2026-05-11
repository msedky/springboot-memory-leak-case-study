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
public class IntermediaryBankDetails {

    @Column(name = "intermediary_bank_name")
    private String intermediaryBankName;

    @Column(name = "intermediary_swift_bic")
    private String intermediarySwiftBic;

    @Column(name = "intermediary_routing_number")
    private String intermediaryRoutingNumber;
}