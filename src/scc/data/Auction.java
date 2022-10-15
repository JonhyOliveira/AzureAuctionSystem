package scc.data;

public class Auction {
    private String title;
    private String desc;
    private String photoId;
    private String owner_nickname;
    private String aucStatus;
    private long endTime;
    private float minPrice;
   //private List<Bid> bids;
    //private List<Question> questios;

    public Auction() {}

    public Auction(String title, String desc, String photoId, String owner_nickname, long endTime, float minPrice,
                   String aucStatus/*, List<Bid> bids, List<Question> questions*/) {
        super();
        this.title = title;
        this.desc = desc;
        this.photoId = photoId;
        this.owner_nickname = owner_nickname;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.aucStatus = aucStatus;
       /* this.bids = bids;
        this.questions = questions;*/
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getPhotoId() {
        return photoId;
    }
    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
    public String getOwner_nickname() {
        return owner_nickname;
    }
    public void setOwner_nickname(String owner_nickname) {
        this.owner_nickname = owner_nickname;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public float getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }
    public String getAucStatus() {
        return aucStatus;
    }
    public void setAucStatus(String aucStatus) {
        this.aucStatus = aucStatus;
    }
    /*public List<Bid> getBids() {
        return bids;
    }
    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
    public List<Question> getQuestions() {
        return questions;
    }
    public void setQuestions(List<Question> bids) {
        this.questions = questions;
    }*/

    @Override
    public String toString() {
        return "Auction [title=" + title + ", desc=" + desc + ", photoId=" + photoId + ", owner=" + owner_nickname + ", endTime=" + endTime + ", minPrice=" + minPrice + ", aucStatus=" + aucStatus + " ]";
    }

}
