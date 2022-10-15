package scc.data;

import java.util.Objects;

public final class Bid {
    private final String bidder;
    private final Double amount;

    public Bid(String bidder, Double amount) {
        this.bidder = bidder;
        this.amount = amount;
    }

    public String bidderNickname() {
        return bidder;
    }

    public Double amount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Bid) obj;
        return Objects.equals(this.bidder, that.bidder) &&
                Objects.equals(this.amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bidder, amount);
    }

    @Override
    public String toString() {
        return "Bid[" +
                "bidder=" + bidder + ", " +
                "amount=" + amount + ']';
    }


}
