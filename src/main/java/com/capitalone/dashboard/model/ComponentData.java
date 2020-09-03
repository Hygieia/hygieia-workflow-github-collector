package com.capitalone.dashboard.model;

import com.capitalone.dashboard.enums.CONCLUSION;
import com.capitalone.dashboard.enums.STATUS;

/*The Source of data specific to each chart component rendered on the screen*/
public class ComponentData {
	
	private STATUS status;
	private CONCLUSION conclusion;

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}
	
	public CONCLUSION getConclusion() {
		return conclusion;
	}

	public void setConclusion(CONCLUSION conclusion) {
		this.conclusion = conclusion;
	}

	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object object) {
		this.data = object;
	}
	
	

}
