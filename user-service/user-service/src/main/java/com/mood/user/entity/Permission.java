package com.mood.user.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="permissions")
public class Permission {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="name")
	@NotEmpty(message="Permission must have a name value.")
	private String name;
	
	@Column(name="principal")
	@NotEmpty(message="Permission must have a principal value.")
	private String principal;
	
	@ManyToMany(mappedBy="permissions")
	private Collection<Role> roles;

	

	public Permission() {

	}


	public Permission(long id, @NotEmpty(message = "Permission must have a name value.") String name, String principal,
			Collection<Role> roles) {
		super();
		this.id = id;
		this.name = name;
		this.principal = principal;
		this.roles = roles;
	}

	
	public Permission(@NotEmpty(message = "Permission must have a name value.") String name, String principal) {
		super();
		this.name = name;
		this.principal = principal;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPrincipal() {
		return principal;
	}


	public void setPrincipal(String principal) {
		this.principal = principal;
	}





	
	
}
