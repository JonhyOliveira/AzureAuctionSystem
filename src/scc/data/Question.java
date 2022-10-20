package scc.data;

import java.util.Objects;

public class Question {

    private String questionID;
    private String questioner;
    private String text;
    private String answer;

    @SuppressWarnings("unused")
    public Question(){}

    public Question(String questionID, String questioner, String text, String answer) {
        super();
        this.questionID = questionID;
        this.questioner = questioner;
        this.text = text;
        this.answer = answer;
    }

    public Question patch(Question otherQuestion)
    {
        Question patched = this.copy();

        if (Objects.isNull(patched.answer) && Objects.nonNull(otherQuestion.answer))
            patched.answer = otherQuestion.answer;

        return patched;
    }

    public String getQuestionID() {
        return questionID;
    }

    @SuppressWarnings("unused")
    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    @SuppressWarnings("unused")
    public String getQuestioner(){
        return questioner;
    }

    @SuppressWarnings("unused")
    public void setQuestioner(String questioner){
        this.questioner = questioner;
    }

    @SuppressWarnings("unused")
    public String getText(){
        return text;
    }

    @SuppressWarnings("unused")
    public void setText(String text){
        this.text = text;
    }

    @SuppressWarnings("unused")
    public String getAnswer() {
        return answer;
    }

    @SuppressWarnings("unused")
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question copy() {
        return new Question(this.questionID, this.questioner, this.answer, this.answer);
    }
}
