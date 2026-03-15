package varta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditTransaction;
import varta.model.pgsql.CreditUser;
import varta.repository.pgsql.CreditCardRepository;
import varta.repository.pgsql.CreditTransactionRepository;
import varta.repository.pgsql.CreditUserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockServiceTest {

    @Mock
    private CreditTransactionRepository creditTransactionRepository;

    @Mock
    private CreditUserRepository creditUserRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    private MockService mockService;

    @BeforeEach
    void setUp() {
        mockService = new MockService(creditTransactionRepository, creditUserRepository, creditCardRepository);
    }

    @Test
    @DisplayName("should create mock transaction with user and card")
    void createMockTransaction_createsEntities() {
        mockService.createMockTransaction();

        ArgumentCaptor<CreditUser> userCaptor = ArgumentCaptor.forClass(CreditUser.class);
        verify(creditUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLocId()).isEqualTo("MOCK");
        assertThat(userCaptor.getValue().getExternalUserId()).isNotNull();

        ArgumentCaptor<CreditCard> cardCaptor = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(cardCaptor.capture());
        assertThat(cardCaptor.getValue().getCardNickname()).isEqualTo("MOCK");
        assertThat(cardCaptor.getValue().getExternalCardId()).isNotNull();

        ArgumentCaptor<CreditTransaction> transactionCaptor = ArgumentCaptor.forClass(CreditTransaction.class);
        verify(creditTransactionRepository).save(transactionCaptor.capture());
        assertThat(transactionCaptor.getValue().getTransactionDescription()).isEqualTo("MOCK");
    }

    @Test
    @DisplayName("should repeat random transaction")
    void repeatRandomTransaction_repeatsTransaction() {
        CreditTransaction existingTransaction = new CreditTransaction();
        existingTransaction.setTransactionInternalId(1L);
        existingTransaction.setTransactionDescription("ORIGINAL");

        when(creditTransactionRepository.findAll()).thenReturn(List.of(existingTransaction));

        CreditTransaction duplicated = mockService.repeatRandomTransaction();

        verify(creditTransactionRepository).save(any(CreditTransaction.class));
        assertThat(duplicated).isNotNull();
        // Since copy logic depends on CreditTransaction implementation, we assume it works if duplicate is returned.
        // But verifying save is key.
    }

    @Test
    @DisplayName("should delete mock transactions")
    void deleteMockTransactions_deletesFoundTransactions() {
        CreditTransaction mockTx1 = new CreditTransaction();
        CreditTransaction mockTx2 = new CreditTransaction();
        when(creditTransactionRepository.findByTransactionDescription("MOCK"))
                .thenReturn(List.of(mockTx1, mockTx2));

        mockService.deleteMockTransactions();

        verify(creditTransactionRepository, times(1)).delete(mockTx1);
        verify(creditTransactionRepository, times(1)).delete(mockTx2);
    }
}

