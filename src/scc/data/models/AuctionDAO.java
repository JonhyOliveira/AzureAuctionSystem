package scc.data.models;

import scc.data.Auction;


public final class AuctionDAO extends DAO {

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
        this(auc.auctionID(), auc.getTitle(), auc.getDescription(), auc.getPhotoId(), auc.getOwnerNickname(), auc.getEndTime(), auc.getMinPrice(), auc.getIsClosed());
    }

    public AuctionDAO(String auctionID, String title, String description, String thumbnailID, String owner_nickname, long endTime, float minPrice, boolean aucStatus){
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

    public String auctionID()
    {
        return auctionID;
    }

    @SuppressWarnings("unused")
    public void setAuctionID(String auction_id) {
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
    public String getThumbnailID() {
        return thumbnailID;
    }
    @SuppressWarnings("unused")
    public void setThumbnailID(String thumbnailID) {
        this.thumbnailID = thumbnailID;
    }
    @SuppressWarnings("unused")
    public String ownerNickname() {
        return owner_nickname;
    }
    @SuppressWarnings("unused")
    public void setOwnerNickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }
    @SuppressWarnings("unused")
    public long getEndTime() {
        return endTime;
    }
    @SuppressWarnings("unused")
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    @SuppressWarnings("unused")
    public String getMinPrice() {
        return Float.toHexString(minPrice);
    }
    @SuppressWarnings("unused")
    public void setMinPrice(String minPrice) {
        this.minPrice = Float.valueOf(minPrice);
    }
    @SuppressWarnings("unused")
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
