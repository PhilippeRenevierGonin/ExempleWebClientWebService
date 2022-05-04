package example.data;


public class Message {

	private String message;

	public Message() {
	}

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Greeting{" +
				"message='" + message + '\'' +
				'}';
	}
}
