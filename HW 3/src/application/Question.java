package application;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Question {
	private Integer id;
	private String title;
	private String text;
	private Integer authorId;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
	private List<String> comp;
	private int preferredAnswer;
	private User author;
	private String authorName;
	private List<String> relatedId;

	// Constructor mainly for when getAllQuestions() method is used in QAHelper.java
	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author, String authorName, List<String> relatedId) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
		this.preferredAnswer = preferredAnswer;
		this.author = author;
		this.authorName = authorName;
		this.relatedId = relatedId;
	}
	
	
	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author, String authorName) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
		this.preferredAnswer = preferredAnswer;
		this.author = author;
		this.authorName = authorName;
	}

	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
		this.preferredAnswer = preferredAnswer;
		this.author = author;
	}

	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, List<String> comp, int preferredAnswer) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
		this.preferredAnswer = preferredAnswer;
	}

	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn, List<String> comp) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.comp = comp;
	}
	
	public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
			LocalDateTime updatedOn) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.authorId = authorId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public Question(String title, String text, Integer authorId) {
		this.title = title;
		this.text = text;
		this.authorId = authorId;
	}

	public Question(String title, String text) {
		this.title = title;
		this.text = text;
	}

	public Question() {
		this.id = 00;
		this.title = "";
		this.text = "";
		this.authorId = 00;
		this.createdOn = null;
		this.updatedOn = null;
		this.comp = null;
		this.preferredAnswer = 0;
		this.author = null;
	}

	// Getters
	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public List<String> getComp() {
		return comp;
	}

	public int getPreferredAnswer() {
		return preferredAnswer;
	}
	
	public User getAuthor() {
		return author;
	}

	public int getDaysSinceCreated() {
		if (createdOn == null) {
			return 0;
		}
		return (int) ChronoUnit.DAYS.between(createdOn, LocalDateTime.now());
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public List<String> getRelatedId() {
		return relatedId;
	}
	
	// Setters
	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	public void setComp(List<String> comp) {
		this.comp = comp;
	}

	public void setPreferredAnswer(int preferredAnswer) {
		this.preferredAnswer = preferredAnswer;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	
	public void setRelatedId(List<String> relatedId) {
		this.relatedId = relatedId;
	}

	public String toString() {
		return String.format(
				"\nQUESTION: \nID:\n\t%s\nTitle:\n\t%s\nText:\n\t%s\nRelatedIds:\n\t%s\n\t%s\nAuthorId:\n\t%s\nAuthor Name:\n\t%s  \nCreated On:\n\t%s\nUpdated On:\n\t%s\nPreferred Answer Id:\n\t%s",
				id, title, text, relatedId, authorId, authorName, createdOn, updatedOn, preferredAnswer);
	}

	public String toDisplay() {
		String displayAuthor;
		int daysSinceCreated = getDaysSinceCreated();

		// If title is empty then return an empty string
		if (title == "") {
			return "";
		} else {
			// If author returns null(In case of test cases or populated database without
			// proper users)
			// then an empty sting is display for the author.
			if (author == null) {
				displayAuthor = "User";
			} else {
				displayAuthor = authorName;
			}

			return String.format("%s\n%s             	  %sd", title, displayAuthor, daysSinceCreated);
		}
	}
	
	public String toDisplayWithText() {
		String displayAuthor;
		int daysSinceCreated = getDaysSinceCreated();
		
		if (relatedId == null || relatedId.isEmpty()) {
			relatedId = List.of("");
		}

		// If title is empty then return an empty string
		if (title == "") {
			return "";
		} else {
			// If author returns null(In case of test cases or populated database without
			// proper users)
			// then an empty sting is display for the author.
			if (author == null) {
				displayAuthor = "User";
			} else {
				displayAuthor = author.getName();
			}

			return String.format("Question Id: %s\n\n%s\n\n%s\n\nRelated Ids: %s\n\n%s           									    %sd", id, title, text, relatedId,  displayAuthor, daysSinceCreated);
		}
	}

}
