package scc.data.models;

import scc.data.Bid;

import java.util.Objects;

public final class BidDAO extends DAO {

    private String id;
    private String auctionID;
    private String bidder;
    private Double amount;

    public BidDAO(String auctionID, Bid bid)
    {
        this(auctionID, bid.bidderNickname(), bid.amount());
    }

    public BidDAO(String auctionID, String bidder, Double amount) {
        this.auctionID = auctionID;
        this.bidder = bidder;
        this.amount = amount;
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    public String getBidderNickname() {
        return bidder;
    }

    public void setBidderNickname(String bidder) {
        this.bidder = bidder;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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

    public Bid toBid()
    {
        return new Bid(bidder, amount);
    }

}