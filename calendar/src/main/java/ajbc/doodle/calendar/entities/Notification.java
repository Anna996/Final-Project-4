package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Entity
@Table(name = "Notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NotificationID")
	private int id;
	private LocalDateTime localDateTime;
	private String title;
	private String message;

	@Column(name = "EventID", insertable = false, updatable = false)
	private int eventId;

	@Column(name = "UserID", insertable = false, updatable = false)
	private int userId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "EventID")
	private Event event;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "UserID")
	private User user;

	public Notification(LocalDateTime localDateTime, String title, String message, int eventId, int userId, Event event,
			User user) {
		this.localDateTime = localDateTime;
		this.title = title;
		this.message = message;
		this.eventId = eventId;
		this.userId = userId;
		this.event = event;
		this.user = user;
	}
}
