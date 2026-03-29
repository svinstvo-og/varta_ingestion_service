package varta.job.processor;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import varta.model.mysql.RawCreditCard;
import varta.model.pgsql.CreditCard;
import varta.model.pgsql.CreditUser;
import varta.repository.pgsql.CreditUserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardProcessorTest {

    @Mock
    private CreditUserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    private CreditCardProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CreditCardProcessor(userRepository, entityManager);
    }

    @Test
    void testProcess_WithValidUser() throws Exception {
        RawCreditCard raw = new RawCreditCard();
        raw.setCardIdentifier("1234567890123456");
        raw.setOwnerType("PERSONAL");
        raw.setOwnerStableId("user123");
        raw.setAbnormal(0);
        raw.setAbnormalState("{\"normal\":1}");

        CreditUser user = new CreditUser();
        user.setInternalUserId(456L);

        when(userRepository.findUserByExternalUserId("user123")).thenReturn(user);
        when(entityManager.getReference(CreditUser.class, 456L)).thenReturn(user);

        CreditCard result = processor.process(raw);

        assertNotNull(result);
        assertEquals("1234567890123456", result.getExternalCardId());
        assertEquals(user, result.getCreditUser());
        verify(userRepository).findUserByExternalUserId("user123");
        verify(entityManager).getReference(CreditUser.class, 456L);
    }

    @Test
    void testProcess_WithNullOwnerStableId() throws Exception {
        RawCreditCard raw = new RawCreditCard();
        raw.setOwnerStableId(null);

        CreditCard result = processor.process(raw);

        assertNull(result);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(entityManager);
    }

    @Test
    void testProcess_WithUserNotFound() throws Exception {
        RawCreditCard raw = new RawCreditCard();
        raw.setCardIdentifier("1234567890123456");
        raw.setOwnerStableId("user123");
        raw.setAbnormal(0);
        raw.setAbnormalState("{\"normal\":1}");

        when(userRepository.findUserByExternalUserId("user123")).thenReturn(null);

        CreditCard result = processor.process(raw);

        assertNull(result);
        verify(userRepository).findUserByExternalUserId("user123");
        verifyNoInteractions(entityManager);
    }
}
