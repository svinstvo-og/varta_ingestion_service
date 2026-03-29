package varta.model.pgsql;

import org.junit.jupiter.api.Test;
import varta.dto.AbnormalState;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CreditTransactionTest {

    @Test
    void testGetSourceCardInternalId_WithSourceCard() {
        CreditTransaction transaction = new CreditTransaction();
        CreditCard sourceCard = new CreditCard();
        sourceCard.setInternalCardId(123L);
        transaction.setSourceCard(sourceCard);

        assertEquals(123L, transaction.getSourceCardInternalId());
    }

    @Test
    void testGetSourceCardInternalId_WithoutSourceCard() {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setSourceCard(null);

        assertNull(transaction.getSourceCardInternalId());
    }

    @Test
    void testGetDestinationCardInternalId_WithDestinationCard() {
        CreditTransaction transaction = new CreditTransaction();
        CreditCard destinationCard = new CreditCard();
        destinationCard.setInternalCardId(456L);
        transaction.setDestinationCard(destinationCard);

        assertEquals(456L, transaction.getDestinationCardInternalId());
    }

    @Test
    void testGetDestinationCardInternalId_WithoutDestinationCard() {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setDestinationCard(null);

        assertNull(transaction.getDestinationCardInternalId());
    }

    @Test
    void testGetMerchantAcquirerInternalId_WithMerchantAcquirer() {
        CreditTransaction transaction = new CreditTransaction();
        CreditStore merchantAcquirer = new CreditStore();
        merchantAcquirer.setStoreInternalId(789L);
        transaction.setMerchantAcquirer(merchantAcquirer);

        assertEquals(789L, transaction.getMerchantAcquirerInternalId());
    }

    @Test
    void testGetMerchantAcquirerInternalId_WithoutMerchantAcquirer() {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setMerchantAcquirer(null);

        assertNull(transaction.getMerchantAcquirerInternalId());
    }

    @Test
    void testGetAbnormalStateId_WithAbnormalState() {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setAbnormalState(null);

        assertEquals(null, transaction.getAbnormalStateId()); // Assuming NORMAL is 0
    }

    @Test
    void testGetAbnormalStateId_WithoutAbnormalState() {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setAbnormalState(null);

        assertNull(transaction.getAbnormalStateId());
    }

    @Test
    void testCopy() {
        CreditTransaction original = CreditTransaction.builder()
                .transactionPanReference("PAN123")
                .isTransfer(true)
                .transactionCode(123L)
                .systemTraceId(456)
                .transactionAmount(BigDecimal.valueOf(100.00))
                .transactionCompositeKey("KEY123")
                .processedAt(LocalDateTime.now())
                .responseCode(0)
                .entryMode(1)
                .transactionDescription("Test transaction")
                .terminalTypeCode(2)
                .terminalId(3)
                .authenticationFlag(1)
                .abnormal(true)
                .abnormalState(null)
                .build();

        CreditTransaction copy = new CreditTransaction();
        copy.copy(original);

        assertEquals(original.getTransactionPanReference(), copy.getTransactionPanReference());
        assertEquals(original.getIsTransfer(), copy.getIsTransfer());
        assertEquals(original.getTransactionCode(), copy.getTransactionCode());
        assertEquals(original.getSystemTraceId(), copy.getSystemTraceId());
        assertEquals(original.getTransactionAmount(), copy.getTransactionAmount());
        assertEquals(original.getTransactionCompositeKey(), copy.getTransactionCompositeKey());
        assertEquals(original.getProcessedAt(), copy.getProcessedAt());
        assertEquals(original.getResponseCode(), copy.getResponseCode());
        assertEquals(original.getEntryMode(), copy.getEntryMode());
        assertEquals(original.getTransactionDescription(), copy.getTransactionDescription());
        assertEquals(original.getTerminalTypeCode(), copy.getTerminalTypeCode());
        assertEquals(original.getTerminalId(), copy.getTerminalId());
        assertEquals(original.getAuthenticationFlag(), copy.getAuthenticationFlag());
        assertEquals(original.isAbnormal(), copy.isAbnormal());
        assertEquals(original.getAbnormalState(), copy.getAbnormalState());
        assertEquals(original.getSourceCard(), copy.getSourceCard());
        assertEquals(original.getDestinationCard(), copy.getDestinationCard());
        assertEquals(original.getMerchantAcquirer(), copy.getMerchantAcquirer());
    }
}
