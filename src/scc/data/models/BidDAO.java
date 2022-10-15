package scc.data.models;

import java.util.Objects;

public final class BidDAO extends DAO {
    private final String auctionID;
    private final String bidder;
    private final Double amount;

    public BidDAO(String auctionID, String bidder, Double amount) {
        this.auctionID = auctionID;
        this.bidder = bidder;
        this.amount = amount;
    }

    public String auctionID() {
        return auctionID;
    }

    public String bidder() {
        return bidder;
    }

    public Double amount() {
        return amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BidDAO) obj;
        return Objects.equals(this.auctionID, that.auctionID) &&
                Objects.equals(this.bidder, that.bidder) &&
                Objects.equals(this.amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, bidder, amount);
    }

    @Override
    public String toString() {
        return "BidDAO[" +
                "auctionID=" + auctionID + ", " +
                "bidder=" + bidder + ", " +
                "amount=" + amount + ']';
    }


}
