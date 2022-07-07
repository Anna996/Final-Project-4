package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "Events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EventID")
	private int id;
	private String title;
	private String description;
	private String address;
	private boolean isAllDay;
	@Column(name = "StartEvent")
	private LocalDateTime start;
	@Column(name = "EndEvent")
	private LocalDateTime end;
	@Column(name = "RepeatEvent")
	@Enumerated(EnumType.STRING)
	private Repeat repeat;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "UserEvent", joinColumns = @JoinColumn(name = "EventID"), inverseJoinColumns = @JoinColumn(name = "UserID"))
	private Set<User> users = new HashSet<>();

	@OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	private Set<Notification> notifications = new HashSet<>();

	public void addUser(User user) {
		users.add(user);
	}

	public Notification createDefaultNotification(User user) {
		return new Notification(start, title, description, this.id, user.getId(), this, user);
	}
}
