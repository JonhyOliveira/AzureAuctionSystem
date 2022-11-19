package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionDAO extends DAO{

    private String id;
    private String auctionID;
    private String questioner;
    private String question;
    private String answer;

    public QuestionDAO() {}

    public QuestionDAO(String  auctionID, String questioner, String question){
        this.auctionID = auctionID;
        this.questioner = questioner;
        this.question = question;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId(){
        return this.id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setId(String id){
        this.id = id;
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonProperty("auction_id")
    public String getAuctionID(){
        return auctionID;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAuctionID(String auctionID){
        this.auctionID = auctionID;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getQuestioner(){
        return questioner;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setQuestioner(String questioner){
        this.questioner = questioner;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getQuestion(){
        return question;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setQuestion(String question){
        this.question = question;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getAnswer() {
        return answer;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
