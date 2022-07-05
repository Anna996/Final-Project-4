package ajbc.doodle.calendar.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
