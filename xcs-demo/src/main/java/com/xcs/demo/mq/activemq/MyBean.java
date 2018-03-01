package com.xcs.demo.mq.activemq;

import java.io.Serializable;

public class MyBean implements Serializable {

	private static final long serialVersionUID = 4927485586747375074L;

	private Integer id;

	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
