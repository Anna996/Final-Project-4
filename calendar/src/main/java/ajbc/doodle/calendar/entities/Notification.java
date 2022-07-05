package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
