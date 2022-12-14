package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Question {

    @JsonProperty("question_id")
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

    /**
     * Patches and returns this question with new details
     *
     * @param otherQuestion the patching question
     * @return the patched question
     */
    public Question patch(Question otherQuestion)
    {
        Question patched = this.copy();

        if (Objects.isNull(patched.answer) && Objects.nonNull(otherQuestion.answer))
            patched.answer = otherQuestion.answer;

        return patched;
    }

    /**
     * @return the question identifier
     */
    @JsonProperty("question_id")
    public String getQuestionID() {
        return questionID;
    }

    /**
     * sets the question identifier
     */
    @SuppressWarnings("unused")
    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    /**
     * @return the nickname of the user who asked the question
     */
    @SuppressWarnings("unused")
    public String getQuestioner(){
        return questioner;
    }

    /**
     * sets the nickname of the user who asked the question
     */
    @SuppressWarnings("unused")
    public void setQuestioner(String nickname){
        this.questioner = nickname;
    }

    /**
     * @return the question text
     */
    @SuppressWarnings("unused")
    public String getText(){
        return text;
    }

    /**
     * sets que question text
     */
    @SuppressWarnings("unused")
    public void setText(String text){
        this.text = text;
    }

    /**
     * @return the auction owner answer
     */
    @SuppressWarnings("unused")
    public String getAnswer() {
        return answer;
    }

    /**
     * sets the auction owner answer
     */
    @SuppressWarnings("unused")
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return a new object that is a copy of this question
     */
    public Question copy() {
        return new Question(this.questionID, this.questioner, this.text, this.answer);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionID='" + questionID + '\'' +
                ", questioner='" + questioner + '\'' +
                ", text='" + text + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionID, question.questionID) && Objects.equals(questioner, question.questioner) && Objects.equals(text, question.text) && Objects.equals(answer, question.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionID, questioner, text, answer);
    }
}
