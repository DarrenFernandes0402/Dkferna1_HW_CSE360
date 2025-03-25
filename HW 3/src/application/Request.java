package application;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Request {
	private StringProperty request;
	private User user;
	private boolean requestTOF;

	public Request(String request, User user) {
		this.request = new SimpleStringProperty(request);
		this.user = user;
	}
	
	public Request(String request, User user, boolean requestTOF) {
		this.request = new SimpleStringProperty(request);
		this.user = user;
		this.requestTOF = requestTOF;
	}
	
	public StringProperty requestProperty() {
        return request;
    }
	
	public String getRequest() {
		return request.get();
	}
	
	public boolean getRequestTOF() {
		return requestTOF;
	}
	
	public void setRequest(String request) {
		this.request.set(request);
	}
	
	public String getUserName() {
        return user.getUsername();
    }

	
	public void setRequestTOF(boolean tof) {
		this.requestTOF = tof;
	}
	
	public User getUser() {
		return user;
	}
	}