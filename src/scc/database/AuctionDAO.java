package scc.database;

import scc.utils.Hash;
import java.util.Objects;

public class AuctionDAO {

    private String _rid;
    private String _ts;
    private String title;
    private String desc;
    private String photoId;
    private String owner_nickname;
    private long endTime;
    private float minPrice;
    private String aucStatus;
    /*private List<AuctionResource.Bid> bids;
    private List<AuctionResource.Question> questions;*/


    public AuctionDAO(){ }

    public AuctionDAO (Auction auc){
        this(auc.getTitle(), auc.getDesc(), auc.getPhotoId(), auc.getOwner_nickname(), auc.getEndTime(), auc.getMinPrice(), auc.getAucStatus());
    }

    public AuctionDAO(String title, String desc, String photoId, String owner_nickname, long endTime, float minPrice, String aucStatus){
        super();
        this.title = title;
        this.desc = desc;
        this.photoId = photoId;
        this.owner_nickname = owner_nickname;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.aucStatus = aucStatus;
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

    public Auction toAuction(){
        return new Auction(title, desc, photoId, owner_nickname, endTime, minPrice, aucStatus);
    }

    public AuctionDAO update(Auction auc)
    {
        if (Objects.nonNull(auc))
        {
            if (Objects.nonNull(auc.getTitle()))
                this.setTitle(auc.getTitle());
            if (Objects.nonNull(auc.getDesc()))
                this.setDesc(Hash.of(auc.getDesc()));
            if (Objects.nonNull(auc.getPhotoId()))
                this.setPhotoId(auc.getPhotoId());
            if (Objects.nonNull(auc.getOwner_nickname()))
                this.setOwner_nickname(auc.getOwner_nickname());
            if (auc.getEndTime() >= 0)
                this.setEndTime(auc.getEndTime());
            if (auc.getMinPrice() >= 0)
                this.setMinPrice(auc.getMinPrice());
            if (Objects.nonNull(auc.getAucStatus()))
                this.setAucStatus(auc.getAucStatus());
        }

        return this;
    }



}
