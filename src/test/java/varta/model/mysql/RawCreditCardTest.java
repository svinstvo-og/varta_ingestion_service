package varta.model.mysql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RawCreditCardTest {

    @Test
    void testGettersAndSetters() {
        RawCreditCard card = new RawCreditCard();
        card.setCardId(123L);
        card.setOwnerType("PERSONAL");
        card.setOwnerId(456L);
        card.setCardIdentifier("1234567890123456");
        card.setCardType("DEBIT");
        card.setCardProductCode("VISA");
        card.setCardNickname("My Card");
        card.setCardFeatureFlag("ACTIVE");
        card.setLocationId("LOC001");
        card.setBranchCode("BR001");
        card.setFullLocationCode("FULL001");
        card.setAbnormal(0);

        assertEquals(123L, card.getCardId());
        assertEquals("PERSONAL", card.getOwnerType());
        assertEquals(456L, card.getOwnerId());
        assertEquals("1234567890123456", card.getCardIdentifier());
        assertEquals("DEBIT", card.getCardType());
        assertEquals("VISA", card.getCardProductCode());
        assertEquals("My Card", card.getCardNickname());
        assertEquals("ACTIVE", card.getCardFeatureFlag());
        assertEquals("LOC001", card.getLocationId());
        assertEquals("BR001", card.getBranchCode());
        assertEquals("FULL001", card.getFullLocationCode());
        assertEquals(0, card.getAbnormal());
    }

    @Test
    void testDefaultValues() {
        RawCreditCard card = new RawCreditCard();
        assertNull(card.getCardId());
        assertNull(card.getOwnerType());
        assertNull(card.getOwnerId());
        assertNull(card.getCardIdentifier());
        assertNull(card.getCardType());
        assertNull(card.getCardProductCode());
        assertNull(card.getCardNickname());
        assertNull(card.getCardFeatureFlag());
        assertNull(card.getLocationId());
        assertNull(card.getBranchCode());
        assertNull(card.getFullLocationCode());
        assertNull(card.getAbnormal());
    }
}
