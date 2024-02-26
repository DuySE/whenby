package request;

public class CreateMeetingRequest {
	private String name;
	private String startTime;
	private String endTime;
	
	public CreateMeetingRequest(String _name, String _startTime, String _endTime) {
		this.name = _name;
		this.startTime = _startTime;
		this.endTime = _endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


}
