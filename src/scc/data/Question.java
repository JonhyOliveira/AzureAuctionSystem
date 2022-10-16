package scc.data;

import scc.data.models.QuestionDAO;

public class Question {
    private String auctionID;
    private String questioner;
    private String question;

    public Question(){}

    public Question(String auctionID, String questioner, String question){
        super();
        this.auctionID = auctionID;
        this.questioner = questioner;
        this.question = question;
    }

    public String auctionId(){
        return auctionID;
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
