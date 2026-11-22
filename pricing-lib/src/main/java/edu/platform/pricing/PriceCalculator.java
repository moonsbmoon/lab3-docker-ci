package edu.platform.pricing;

public class PriceCalculator {
    public static double calculateDiscount(double price, int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        return price - (price * discountPercent / 100.0);
    }

    // ВАРИАНТ 3: Правило для PREMIUM подписки (скидка 20%)
    public static double calculatePremiumPrice(double basePrice) {
        return calculateDiscount(basePrice, 20);
    }
}