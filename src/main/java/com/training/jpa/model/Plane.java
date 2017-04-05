package com.training.jpa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Jakub Kubrynski
 */
@Entity
public class Plane {

	@Id
	private Long id;

	private String name;

	public Plane() {
	}

	public Plane(final Long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
