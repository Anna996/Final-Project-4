package ajbc.doodle.calendar.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ErrorMessage {

	private String message;
	private Object data;

	public static ErrorMessage getErrorMessage(String data, String message) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setData(data);
		errorMessage.setMessage(message);
		return errorMessage;
	}
}
