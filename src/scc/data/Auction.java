package scc.data;

public class Auction {

    private String auctionID;
    private String title;
    private String desc;
    private String photoId;
    private String ownerNickname;
    private boolean isClosed;
    private Long endTime;
    private Float minPrice;
   //private List<Bid> bids;
    //private List<Question> questios;

    public Auction() {}

    public Auction(String auctionID, String title, String desc, String photoId, String ownerNickname, long endTime, float minPrice,
                   boolean isClosed/*, List<Bid> bids, List<Question> questions*/) {
        super();
        this.auctionID = auctionID;
        this.title = title;
        this.desc = desc;
        this.photoId = photoId;
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
    public void setTitle(String title) {
        this.title = title;
    }
    public String description() {
        return desc;
    }
    public void setDescription(String desc) {
        this.desc = desc;
    }
    public String photoId() {
        return photoId;
    }
    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
    public String ownerNickname() {
        return ownerNickname;
    }
    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }
    public long endTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public float minPrice() {
        return minPrice;
    }
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    public boolean isClosed() {
        return isClosed;
    }
    public void setClosedStatus(boolean state) {
        this.isClosed = state;
    }

    @Override
    public String toString() {
        return "Auction [title=" + title + ", desc=" + desc + ", photoId=" + photoId + ", owner=" + ownerNickname + ", endTime=" + endTime + ", minPrice=" + minPrice + ", isOpen=" + isClosed + " ]";
    }

}
