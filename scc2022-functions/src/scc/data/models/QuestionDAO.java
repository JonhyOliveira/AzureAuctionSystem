package scc.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class QuestionDAO extends BaseDAO {

    public static final String AuctionID = "auction_id", Questioner = "questioner_id";
    @JsonProperty(AuctionID)
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
    @JsonProperty("auction_id")
    public String getAuctionID(){
        return auctionID;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAuctionID(String auctionID){
        this.auctionID = auctionID;
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonProperty("questioner_id")
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
