package scc.data.models;

import scc.data.Question;

public class QuestionDAO extends DAO{

    private String id;
    private String auctionID;
    private String questioner;
    private String question;
    private String answer;

    public QuestionDAO(String auctionID, Question question)
    {
        this.id = question.getQuestionID();
        this.auctionID = auctionID;
        this.questioner = question.getQuestioner();
        this.question = question.getText();
        this.answer = question.getAnswer();
    }

    public QuestionDAO(String  auctionID, String questioner, String question){
        this.auctionID = auctionID;
        this.questioner = questioner;
        this.question = question;
    }

    public Question toQuestion(){
        return new Question(id, questioner, question);
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
