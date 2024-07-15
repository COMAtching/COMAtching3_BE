package comatching.comatching3.match_message.entity;

import java.util.ArrayList;
import java.util.List;

import comatching.comatching3.users.entity.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageMap {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_map_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Users sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="receiver_id")
	private Users receiver;

	@OneToMany(mappedBy = "messageMap", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Message> messages = new ArrayList<Message>();

	@Builder
	public MessageMap(Users sender, Users receiver, List<Message> messages) {
		this.sender = sender;
		this.receiver = receiver;
		this.messages = messages;
	}
}
