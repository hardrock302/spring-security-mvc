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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.jboss.logging.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@DynamicUpdate
public class AppUser implements UserDetails{

	private static final long serialVersionUID = 8331847341093973612L;
	@Transient
	private Logger logger = Logger.getLogger(getClass().getName());
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

	@NotNull(message="A membership is required.")
	@Column(name="membership_level")
	@Pattern(regexp="UNPAID|GOLD|DIAMOND", message="A membership can either be UNPAID, GOLD or DIAMOND.")
	private String membershipLevel;
	

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name="membership_expiry_date")
	@JsonFormat(pattern="yyyy-MM-dd")
	private String membershipExpiryDate;
	
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_roles",
            joinColumns =@JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Collection<Role> roles;
    
    
    
	public Collection<Role> getRoles() {
		return roles;
	}
	
	public enum UserStatus {
		INACTIVE,
		ACTIVE,
		BANNED
	}
	
	public AppUser() {}
	

	public AppUser(
			@NotNull @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up tp 45 alphabetic characters.") String firstName,
			@NotNull @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up tp 45 alphabetic characters.") String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public AppUser(
			@NotNull @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", message = "Phone number must match the following: xxx-xxx-xxxx.") String phoneNumber,
			@NotEmpty @Email(message = "Email address is not well-formed.") String email,
			@NotNull @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up tp 45 alphabetic characters.") String firstName,
			@NotNull @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up tp 45 alphabetic characters.") String lastName) {
		super();
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.membershipLevel = "UNPAID";
		this.membershipExpiryDate = null;
	}


	public AppUser(
			@NotNull(message = "Phone number is required.") @Pattern(regexp = "^\\d{1,3}-\\d{3}-\\d{3}-\\d{4}", message = "Phone number must match the following: x-xxx-xxx-xxxx.") String phoneNumber,
			@NotEmpty(message = "Email address is required.") @Email(message = "Email address is not well-formed.") String email,
			@NotNull(message = "First name is required.") @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up to 45 alphabetic characters.") String firstName,
			@NotNull(message = "Last name is required.") @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up to 45 alphabetic characters.") String lastName,
			@NotNull(message = "Password is required") String password) {
		
		super();
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.membershipLevel = "UNPAID";
		this.membershipExpiryDate = null;
	}



	public AppUser(
			@NotNull(message = "Phone number is required.") @Pattern(regexp = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$", message = "Phone number must match the following: xxx-xxx-xxxx.") String phoneNumber,
			@NotEmpty(message = "Email address is required.") @Email(message = "Email address is not well-formed.") String email,
			@NotNull(message = "First name is required.") @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up to 45 alphabetic characters.") String firstName,
			@NotNull(message = "Last name is required.") @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up to 45 alphabetic characters.") String lastName,
			@NotNull(message = "Password is required.") String password, @NotNull String membershipLevel,
			@NotNull String membershipExpiryDate) {
		super();
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.membershipLevel = membershipLevel;
		this.membershipExpiryDate = membershipExpiryDate;
	}


	public AppUser(
			@NotEmpty(message = "Email address is required.") @Email(message = "Email address is not well-formed.") String email,
			@NotNull(message = "First name is required.") @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up to 45 alphabetic characters.") String firstName,
			@NotNull(message = "Last name is required.") @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up to 45 alphabetic characters.") String lastName,
			UserStatus enabled, @NotNull String membershipLevel, String membershipExpiryDate) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.membershipLevel = membershipLevel;
		this.membershipExpiryDate = membershipExpiryDate;
	}

	public AppUser(
			@NotEmpty(message = "Email address is required.") @Email(message = "Email address is not well-formed.") String email,
			@NotNull(message = "First name is required.") @NotEmpty(message = "First name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "First name must contain up to 45 alphabetic characters.") String firstName,
			@NotNull(message = "Last name is required.") @NotEmpty(message = "Last name is required.") @Pattern(regexp = "[A-Za-z]{0,45}", message = "Last name must contain up to 45 alphabetic characters.") String lastName,
			UserStatus enabled, @NotNull(message = "Password is required.") String password,
			@NotNull(message = "A membership is required.") @Pattern(regexp = "UNPAID|GOLD|DIAMOND", message = "A membership can either be UNPAID, GOLD or DIAMOND.") String membershipLevel,
			String membershipExpiryDate) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.enabled = enabled;
		this.password = password;
		this.membershipLevel = membershipLevel;
		this.membershipExpiryDate = membershipExpiryDate;
	}


	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}

	
	public int getFailedLogins() {
		return failedLogins;
	}


	public void setFailedLogins(int failedLogins) {
		this.failedLogins = failedLogins;
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


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getMembershipLevel() {
		return membershipLevel;
	}


	public void setMembershipLevel(String membershipLevel) {
		this.membershipLevel = membershipLevel;
	}


	public String getMembershipExpiryDate() {
		return membershipExpiryDate;
	}


	public void setMembershipExpiryDate(String membershipExpiryDate) {
		this.membershipExpiryDate = membershipExpiryDate;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + this.getMembershipLevel());
		ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(authority);
		return authorities;
	}


	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.email;
	}


	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return this.failedLogins > 3;
	}


	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isEnabled() {
		return this.enabled == UserStatus.ACTIVE;
	}
	
}
	


