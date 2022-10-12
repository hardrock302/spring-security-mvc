package com.mood.user.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.persistence.JoinColumn;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;

@Entity
@Data
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	@Column(name="name")
	@NotEmpty
	private String name;
	
	@ManyToMany(mappedBy="roles")
	private Collection<AppUser> users;
	
	@ManyToMany
	@JoinTable(
			name = "roles_permissions",
			joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
			)
	private Collection<Permission> permissions;

	
	public Role() {
		super();
	}

	public Role(@NotEmpty String name) {
		super();
		this.name = name;
	}

	public void setPermissions(Collection<Permission> permissions) {
		// TODO Auto-generated method stub
		this.permissions = permissions;
	}


	public Collection<Permission> getPermissions() {
		return permissions;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
