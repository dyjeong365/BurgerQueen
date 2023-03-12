package app.discount.discountCondition;

public interface DiscountCondition {
    int applyDiscount(int price);

    void checkDiscountCondition();

    boolean isSatisfied();
}
