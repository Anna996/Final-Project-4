package ajbc.doodle.calendar.entities;

import java.io.Serializable;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class UserEventPK implements Serializable {

	private int userId;
	private int eventId;
}
