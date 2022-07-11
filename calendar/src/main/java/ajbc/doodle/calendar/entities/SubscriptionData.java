package ajbc.doodle.calendar.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "SubscriptionData")
public class SubscriptionData {

	@Id
	@Column(name = "EndPoint")
	private String endPoint;
	@Column(name = "PublicKey")
	private String publicKey;
	@Column(name = "Auth")
	private String auth;
	
	@Column(name = "UserID",insertable = false, updatable = false)
	private Integer userId;
	
	@JsonIgnore
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "UserID")
	private User user;
	
}
