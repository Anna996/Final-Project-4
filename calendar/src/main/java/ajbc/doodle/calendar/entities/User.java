package ajbc.doodle.calendar.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@Table(name = "Users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserID")
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private LocalDate birthdate;
	private boolean isLoggedIn;
	private boolean isActive;

	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Set<Event> events = new HashSet<Event>();

	@JsonIgnore
	@OneToMany(mappedBy = "user" , fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	private Set<Notification> notifications = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	private Set<SubscriptionData> subscriptions = new HashSet<>();
	
	public void addEvent(Event event) {
		events.add(event);
	}
	
	public void copyUser(User user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
		this.birthdate = user.birthdate;
		this.isLoggedIn = user.isLoggedIn;
		this.isActive = user.isActive;
	}
}
