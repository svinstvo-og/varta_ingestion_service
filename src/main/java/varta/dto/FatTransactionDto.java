package varta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import varta.model.pgsql.CreditTransaction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FatTransactionDto {
    // base transaction
    private CreditTransaction transaction;
    // contextual data
    private EnrichedTransactionDto enrichedTransactionData;
}
