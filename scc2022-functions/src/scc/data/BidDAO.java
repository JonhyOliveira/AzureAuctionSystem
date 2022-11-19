package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class BidDAO extends DAO {

    private String id;
    private String auctionID;
    private String bidder;
    private Double amount;

    public BidDAO() {}

    public BidDAO(String id, String auctionID, String bidder, Double amount) {
        this.id = id;
        this.auctionID = auctionID;
        this.bidder = bidder;
        this.amount = amount;
    }

    @SuppressWarnings("unused")
    public String getId()
    {
        return this.id;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    @JsonProperty("auction_id")
    public String getAuctionID() {
        return auctionID;
    }

    @SuppressWarnings("unused")
    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    @SuppressWarnings("unused")
    @JsonProperty("bidder_nickname")
    public String getBidderNickname() {
        return bidder;
    }

    @SuppressWarnings("unused")
    public void setBidderNickname(String bidder) {
        this.bidder = bidder;
    }

    @SuppressWarnings("unused")
    public Double getAmount() {
        return amount;
    }

    @SuppressWarnings("unused")
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        BidDAO that = (BidDAO) obj;
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