package com.mood.user.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.jboss.logging.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mood.user.entity.AppUser.UserStatus;

@Entity
@Table(name="employees")
@DynamicUpdate
public class Employee implements UserDetails{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@NotNull(message="Phone number is required.")
	@Pattern(regexp="^\\d{1,3}-\\d{3}-\\d{3}-\\d{4}", message="Phone number must match the following: x-xxx-xxx-xxxx.")
	@Column(name="phone_number", unique=true)
	protected String phoneNumber;

	@NotEmpty(message="Email address is required.")
	@Email(message="Email address is not well-formed.")
	@Column(name="email", unique=true)
	@Pattern(regexp="^[A-Za-z0-9\\.]{1,}\\@venchurave\\.com$", message="Administrator emails address must use the venchurave domain")
	protected String email;
	
	@NotNull(message="First name is required.")
	@Column(name="first_name")
	@NotEmpty(message="First name is required.")
	@Pattern(regexp="[A-Za-z]{0,45}", message="First name must contain up to 45 alphabetic characters.")
	protected String firstName;
	
	@NotNull(message="Last name is required.")
	@NotEmpty(message="Last name is required.")
	@Pattern(regexp="[A-Za-z]{0,45}", message="Last name must contain up to 45 alphabetic characters.")
	@Column(name="last_name")
	protected String lastName;
	
	@Column(name="enabled")
	@ColumnDefault("0")
	@Enumerated(EnumType.ORDINAL)
	protected UserStatus enabled;
	
	@Column(name="failed_logins")
	@ColumnDefault("0")
	protected int failedLogins;
	
	@Column(name="password")
	@NotNull(message="Password is required.")
	protected String password;


	@Transient
	private Logger logger = Logger.getLogger(getClass().getName());
	
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "employee_roles",
            joinColumns =@JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    
    private Collection<Role> roles;

    @NotNull
    private String access;
    
	public Employee(String email, String firstName, String lastName, String access) {
		super();
		this.access = access;
	}

	
	public Employee(
			@NotNull @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", message = "Phone number must match the following: xxx-xxx-xxxx.") String phoneNumber,
			@NotEmpty @Email(message = "Email address is not well-formed.") String email,
			@NotNull @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up tp 45 alphabetic characters.") String firstName,
			@NotNull @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up tp 45 alphabetic characters.") String lastName, @NotNull String access) {
		super();
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.access = access;
		
	}
	


	public Employee(Collection<Role> roles) {
		super();
		this.roles = roles;
	}

	public Employee() {
		// TODO Auto-generated constructor stub
	}


	public long getId() {
		return id;
	}





	public void setId(long id) {
		this.id = id;
	}





	public String getAccess() {
		return access;
	}





	public void setAccess(String access) {
		this.access = access;
	}

	

	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public UserStatus getEnabled() {
		return enabled;
	}


	public void setEnabled(UserStatus enabled) {
		this.enabled = enabled;
	}


	public int getFailedLogins() {
		return failedLogins;
	}


	public void setFailedLogins(int failedLogins) {
		this.failedLogins = failedLogins;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Logger getLogger() {
		return logger;
	}


	public void setLogger(Logger logger) {
		this.logger = logger;
	}


	public Collection<Role> getRoles() {
		return roles;
	}



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Role r : roles) {
			authorities.add(new SimpleGrantedAuthority(r.getName()));
		}
		return authorities;
	}


	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}


	@Override
	public String getUsername() {
		return email;
	}


	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isAccountNonLocked() {
		return this.failedLogins >=3;
	}


	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isEnabled() {
		return enabled == UserStatus.ACTIVE;
	}
	
}
