package cs209a.finalproject_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class StackOverflowThread {
    private Question question;
    private List<Answer> answers;

    @JsonProperty("question_comments")
    private List<Comment> questionComments;

    @JsonProperty("answer_comments")
    private Map<String, List<Comment>> answerComments;

    public boolean notSolvable () {
        return !isSolvable();
    }
    public boolean isSolvable () {
        if (this.getAnswers() == null) return false;
        for (Answer answer : this.getAnswers()) {
            if (answer.getIsAccepted()) return true;
        }
        return false;
    }
    // Getters and Setters
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    public List<Comment> getQuestionComments() { return questionComments; }
    public void setQuestionComments(List<Comment> questionComments) {
        this.questionComments = questionComments;
    }

    public Map<String, List<Comment>> getAnswerComments() { return answerComments; }
    public void setAnswerComments(Map<String, List<Comment>> answerComments) {
        this.answerComments = answerComments;
    }
}
