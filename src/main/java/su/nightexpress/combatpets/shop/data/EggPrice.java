package su.nightexpress.combatpets.shop.data;

import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.currency.Currency;

public class EggPrice {

    private final Currency currency;
    private final double   price;

    public EggPrice(@NonNull Currency currency, double price) {
        this.currency = currency;
        this.price = price;
    }

    @NonNull
    public Currency getCurrency() {
        return currency;
    }

    public double getPrice() {
        return price;
    }
}
