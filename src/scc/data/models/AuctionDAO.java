package scc.data.models;

import scc.data.Auction;

import java.util.Objects;
import java.util.Optional;

public final class AuctionDAO extends DAO {

    private String auctionID;
    private String title;
    private String description;
    private String thumbnailID;
    private String owner_nickname;
    private Long endTime;
    private Float minPrice;
    private boolean isClosed;

    public AuctionDAO(){ }

    public AuctionDAO (Auction auc){
        this(auc.auctionID(), auc.title(), auc.description(), auc.photoId(), auc.ownerNickname(), auc.endTime(), auc.minPrice(), auc.isClosed());
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

    public void setAuctionID(String auction_id) {
        this.auctionID = auction_id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getThumbnailID() {
        return thumbnailID;
    }
    public void setThumbnailID(String thumbnailID) {
        this.thumbnailID = thumbnailID;
    }
    public String ownerNickname() {
        return owner_nickname;
    }
    public void setOwnerNickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public String getMinPrice() {
        return Float.toHexString(minPrice);
    }
    public void setMinPrice(String minPrice) {
        this.minPrice = Float.valueOf(minPrice);
    }
    public boolean isClosed() {
        return isClosed;
    }
    public void setClosed(boolean isOpen) {
        this.isClosed = isOpen;
    }

    public Auction toAuction(){
        return new Auction(auctionID, title, description, thumbnailID, owner_nickname, endTime, minPrice, isClosed);
    }

    /**
     * Patches and returns this auction with new details
     *
     * @param auc the patching auction
     * @return the patched auction, or null if unable to patch
     */
    public Optional<AuctionDAO> patch(AuctionDAO auc)
    {
        if (this.isClosed)
            return Optional.empty();

        if (Objects.nonNull(auc))
        {
            if (Objects.nonNull(auc.title)) {
                this.title = auc.title;
            }
            if (Objects.nonNull(auc.description)) {
                this.description = auc.description;
            }
            if (Objects.nonNull(auc.thumbnailID)) {
                this.thumbnailID = auc.thumbnailID;
            }
            if (auc.endTime >= this.endTime) {
                this.endTime = auc.endTime;
            }
            if (auc.minPrice >= 0) {
                this.minPrice = auc.minPrice;
            }
            this.setClosed(auc.isClosed());
        }

        return Optional.of(this);
    }



}
