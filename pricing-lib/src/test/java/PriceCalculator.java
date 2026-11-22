package edu.platform.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {
    @Test
    void testDiscounts() {
        assertEquals(5000, PriceCalculator.calculateDiscount(5000, 0));
        assertEquals(4500, PriceCalculator.calculateDiscount(5000, 10));
        assertEquals(0, PriceCalculator.calculateDiscount(5000, 100));
        assertEquals(4000, PriceCalculator.calculatePremiumPrice(5000)); // Тест варианта
    }

    @Test
    void testInvalidDiscounts() {
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.calculateDiscount(5000, -1));
        assertThrows(IllegalArgumentException.class, () -> PriceCalculator.calculateDiscount(5000, 101));
    }
}