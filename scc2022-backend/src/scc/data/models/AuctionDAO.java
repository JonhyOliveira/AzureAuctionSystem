package scc.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import scc.data.Auction;


public final class AuctionDAO extends DAO {

    @JsonProperty("id")
    private String auctionID;
    private String title;
    private String description;
    private String thumbnailID;
    private String owner_nickname;
    private Long endTime;
    private Float minPrice;
    private boolean isClosed;

    @SuppressWarnings("unused")
    public AuctionDAO(){ }

    public AuctionDAO (Auction auc){
        this(auc.getAuctionID(), auc.getTitle(), auc.getDescription(), auc.getImageId(), auc.getOwnerNickname(), auc.getEndTime(), auc.getMinPrice(), auc.getIsClosed());
    }

    public AuctionDAO(String auctionID, String title, String description, String thumbnailID, String owner_nickname, Long endTime, Float minPrice, boolean aucStatus){
        super();
        this.auctionID = auctionID;
        this.title = title;
        this.description = description;
        this.thumbnailID = thumbnailID;
        this.owner_nickname = owner_nickname;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.isClosed = aucStatus;
    }

    public String getAuctionID()
    {
        return auctionID;
    }

    @SuppressWarnings("unused")
    public void setId(String auction_id) {
        this.auctionID = auction_id;
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return title;
    }
    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }
    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }
    @SuppressWarnings("unused")
    public void setDescription(String description) {
        this.description = description;
    }
    @SuppressWarnings("unused")
    @JsonProperty("thumbnail_id")
    public String getThumbnailID() {
        return thumbnailID;
    }
    @SuppressWarnings("unused")
    public void setThumbnailID(String thumbnailID) {
        this.thumbnailID = thumbnailID;
    }
    @SuppressWarnings("unused")
    @JsonProperty("owner_nickname")
    public String getOwnerNickname() {
        return owner_nickname;
    }
    @SuppressWarnings("unused")
    public void setOwnerNickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }
    @SuppressWarnings("unused")
    @JsonProperty("end_time")
    public long getEndTime() {
        return endTime;
    }
    @SuppressWarnings("unused")
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    @SuppressWarnings("unused")
    @JsonProperty("min_price")
    public String getMinPrice() {
        return Float.toHexString(minPrice);
    }
    @SuppressWarnings("unused")
    public void setMinPrice(String minPrice) {
        this.minPrice = Float.valueOf(minPrice);
    }
    @SuppressWarnings("unused")
    @JsonProperty("closed")
    public boolean isClosed() {
        return isClosed;
    }
    @SuppressWarnings("unused")
    public void setClosed(boolean isOpen) {
        this.isClosed = isOpen;
    }

    public Auction toAuction(){
        return new Auction(auctionID, title, description, thumbnailID, owner_nickname, endTime, minPrice, isClosed);
    }

}
