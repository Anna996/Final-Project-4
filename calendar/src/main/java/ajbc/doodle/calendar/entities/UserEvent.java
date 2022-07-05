package ajbc.doodle.calendar.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "UserEvent")
@IdClass(UserEventPK.class)
public class UserEvent {

	@Id
	@Column(name = "UserID")
	private int userId;
	@Id
	@Column(name = "EventID")
	private int eventId;
	@Enumerated(EnumType.STRING)
	private UserType userType;
}
