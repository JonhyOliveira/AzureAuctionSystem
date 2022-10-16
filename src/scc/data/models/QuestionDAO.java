package scc.data.models;

public class QuestionDAO extends DAO{

    private String id; //DONT KNOW IF A QUESTION ID WILL BE NECESSARY
    private String auctionID;
    private String questioner;
    private String question;

    public QuestionDAO(String  auctionID, String questioner, String question){
        this.auctionID = auctionID;
        this.questioner = questioner;
        this.question = question;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getAuctionID(){
        return auctionID;
    }

    public void setAuctionID(String auctionID){
        this.auctionID = auctionID;
    }

    public String getQuestioner(){
        return questioner;
    }

    public void setQuestioner(String questioner){
        this.questioner = questioner;
    }

    public String getQuestion(){
        return question;
    }

    public void setQuestion(String question){
        this.question = question;
    }


}
