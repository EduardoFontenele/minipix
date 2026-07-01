package dev.eduardo.minipix.api.entity;

import dev.eduardo.minipix.common.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pix_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PixTransactionEntity {

    @Id
    private String endToEndId;

    private Instant initiationTimestamp;
    private BigDecimal amount;
    private String senderDocument;
    private String receiverKey;
    private String receiverName;
    private String receiverDocument;
    private String receiverIspb;

    @Enumerated(EnumType.STRING)
    private AccountType receiverAccountType;

    private String description;
}
