package scc.data;

import java.util.Objects;
import java.util.Optional;

public class Auction {

    private String auctionID;
    private String title;
    private String description;
    private String thumbnailId;
    private String ownerNickname;
    private boolean isClosed;
    private Long endTime;
    private Float minPrice;

    @SuppressWarnings("unused")
    public Auction() {}

    public Auction(String auctionID, String title, String description, String thumbnailId, String ownerNickname, long endTime, float minPrice,
                   boolean isClosed/*, List<Bid> bids, List<Question> questions*/) {
        super();
        this.auctionID = auctionID;
        this.title = title;
        this.description = description;
        this.thumbnailId = thumbnailId;
        this.ownerNickname = ownerNickname;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.isClosed = isClosed;
    }

    public String auctionID()
    {
        return auctionID;
    }

    public void setAuctionID(String auction_id) {
        this.auctionID = auction_id;
    }

    public String title() {
        return title;
    }
    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }
    public String description() {
        return description;
    }
    @SuppressWarnings("unused")
    public void setDescription(String desc) {
        this.description = desc;
    }
    public String photoId() {
        return thumbnailId;
    }
    @SuppressWarnings("unused")
    public void setThumbnailId(String thumbnailId) {
        this.thumbnailId = thumbnailId;
    }
    public String ownerNickname() {
        return ownerNickname;
    }
    @SuppressWarnings("unused")
    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }
    public long endTime() {
        return endTime;
    }
    @SuppressWarnings("unused")
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public float minPrice() {
        return minPrice;
    }
    @SuppressWarnings("unused")
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    public boolean isClosed() {
        return isClosed;
    }
    @SuppressWarnings("unused")
    public void setClosed(boolean state) {
        this.isClosed = state;
    }

    @Override
    public String toString() {
        return "Auction [title=" + title + ", desc=" + description + ", photoId=" + thumbnailId + ", owner=" + ownerNickname + ", endTime=" + endTime + ", minPrice=" + minPrice + ", isOpen=" + isClosed + " ]";
    }

    /**
     * Patches and returns this auction with new details
     *
     * @param auc the patching auction
     * @return the patched auction, or null if unable to patch
     */
    public Optional<Auction> patch(Auction auc)
    {
        Auction patching = this.copy();

        if (patching.isClosed)
            return Optional.empty();

        if (Objects.nonNull(auc))
        {
            if (Objects.nonNull(auc.title)) {
                patching.title = auc.title;
            }
            if (Objects.nonNull(auc.description)) {
                patching.description = auc.description;
            }
            if (Objects.nonNull(auc.thumbnailId)) {
                patching.thumbnailId = auc.thumbnailId;
            }
            if (auc.endTime >= patching.endTime) {
                patching.endTime = auc.endTime;
            }
            if (auc.minPrice >= 0) {
                patching.minPrice = auc.minPrice;
            }
            patching.isClosed = auc.isClosed;
        }

        return Optional.of(patching);
    }

    public Auction copy() {
        return new Auction(this.auctionID, this.title, this.description, this.thumbnailId, this.ownerNickname,
                this.endTime, this.minPrice, this.isClosed);
    }
}
