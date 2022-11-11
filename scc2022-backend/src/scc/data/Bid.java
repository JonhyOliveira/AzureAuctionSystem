package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public final class Bid {
    private String bidder;
    private Double amount;

    public Bid() {}

    public Bid(String bidder, Double amount) {
        this.bidder = bidder;
        this.amount = amount;
    }

    /**
     * @return a new object witch is a copy of this bid
     */
    public Bid copy()
    {
        return new Bid(this.bidder, this.amount);
    }

    /**
     * @return the bidder nickname
     */
    @JsonProperty("bidder_nickname")
    public String getBidder() {
        return bidder;
    }

    /**
     * @return the amount in this bid
     */
    @JsonProperty(value = "bid_amount")
    public Double getAmount() {
        return amount;
    }

    /**
     * sets the bidder nickname
     */
    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    /**
     * sets the amount on this bid
     */
    public void setAmount(Double amount) {
        this.amount = amount;
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
