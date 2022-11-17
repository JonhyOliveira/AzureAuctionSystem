package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Optional;

public class Auction {

    @JsonProperty("id")
    private String auctionID;
    private String title;
    private String description;
    @JsonProperty("image_id")
    private String imageId;
    @JsonProperty("owner_nickname")
    private String ownerNickname;
    @JsonProperty("closed")
    private boolean isClosed;
    @JsonProperty("end_time")
    private Long endTime;
    @JsonProperty("min_price")
    private Float minPrice;

    @SuppressWarnings("unused")
    public Auction() {}

    public Auction(String auctionID, String title, String description, String imageId, String ownerNickname, Long endTime, Float minPrice,
                   boolean isClosed/*, List<Bid> bids, List<Question> questions*/) {
        super();
        this.auctionID = auctionID;
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.ownerNickname = ownerNickname;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.isClosed = isClosed;
    }

    public String getAuctionID()
    {
        return auctionID;
    }

    public void setAuctionID(String auction_id) {
        this.auctionID = auction_id;
    }

    public String getTitle() {
        return title;
    }
    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    @SuppressWarnings("unused")
    public void setDescription(String desc) {
        this.description = desc;
    }
    public String getImageId() {
        return imageId;
    }
    @SuppressWarnings("unused")
    public void setImagedId(String thumbnailId) {
        this.imageId = thumbnailId;
    }
    public String getOwnerNickname() {
        return ownerNickname;
    }
    @SuppressWarnings("unused")
    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }
    public Long getEndTime() {
        return endTime;
    }
    @SuppressWarnings("unused")
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public Float getMinPrice() {
        return minPrice;
    }
    @SuppressWarnings("unused")
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    public boolean getIsClosed() {
        return isClosed;
    }
    @SuppressWarnings("unused")
    public void setIsClosed(boolean state) {
        this.isClosed = state;
    }

    @Override
    public String toString() {
        return "Auction [id=" + auctionID + ", title=" + title + ", desc=" + description + ", photoId=" + imageId +
                ", owner=" + ownerNickname + ", endTime=" + endTime + ", minPrice=" + minPrice + ", isClosed=" + isClosed + " ]";
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
            if (Objects.nonNull(auc.imageId)) {
                patching.imageId = auc.imageId;
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
        return new Auction(this.auctionID, this.title, this.description, this.imageId, this.ownerNickname,
                this.endTime, this.minPrice, this.isClosed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return isClosed == auction.isClosed && Objects.equals(auctionID, auction.auctionID) && Objects.equals(title, auction.title) && Objects.equals(description, auction.description) && Objects.equals(imageId, auction.imageId) && Objects.equals(ownerNickname, auction.ownerNickname) && Objects.equals(endTime, auction.endTime) && Objects.equals(minPrice, auction.minPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionID, title, description, imageId, ownerNickname, isClosed, endTime, minPrice);
    }
}
