//package ajbc.doodle.calendar.entities;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.EmbeddedId;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.MapsId;
//import javax.persistence.Table;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//
//@Entity
//@Table(name = "UserEvent")
//@IdClass(UserEventPK.class)
//public class UserEvent {
//
////	@JsonIgnore
////	@EmbeddedId
////	private UserEventPK pk;
//	
//	@JsonIgnore
//	@Id
//	@Column(name = "UserID")
//	private int userId;
//	@JsonIgnore
//	@Id
//	@Column(name = "EventID")
//	private int eventId;
//	
//	
////	@Enumerated(EnumType.STRING)
////	private UserType userType;
//	
//	@JsonIgnore
//	@ManyToOne(cascade = { CascadeType.MERGE })
//	@MapsId("userId")
//	@JoinColumn(name = "UserID")
//	private User user;
//	
//	
//	@ManyToOne(cascade = { CascadeType.MERGE })
//	@MapsId("eventId")
//	@JoinColumn(name = "EventID")
//	private Event event;
//}
