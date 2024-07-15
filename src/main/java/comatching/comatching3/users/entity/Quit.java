package comatching.comatching3.users.entity;

import comatching.comatching3.admin.entity.University;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Quit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "quit_id")
	private Long id;

	private String socialId;

	private Integer quitCount;

	private Integer reportCount;
}
