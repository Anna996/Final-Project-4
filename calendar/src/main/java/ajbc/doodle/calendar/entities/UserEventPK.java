package ajbc.doodle.calendar.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserEventPK implements Serializable {

	private int userId;
	private int eventId;
}
