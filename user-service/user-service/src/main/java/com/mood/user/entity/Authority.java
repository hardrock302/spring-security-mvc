package com.mood.user.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

@Entity
@Data
@Table(name="authorities")
@Getter
@Setter
@NoArgsConstructor
public class Authority {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name="authority")
	@NotEmpty
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Privilege> privileges;
	
	
	
}
