package comatching.comatching3.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private Users reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_id", nullable = false)
	private Users reportedUser;

	@Column(name = "report_category", nullable = false)
	private String reportCategory;

	@Column(name = "report_content")
	private String reportContent;

	@Column(name = "report_image")
	private String reportImage;

	private boolean processsing;

	@Builder
	public Report(Users reporter, Users reportedUser, String reportCategory, String reportContent, String reportImage, boolean processsing) {
		this.reporter = reporter;
		this.reportedUser = reportedUser;
		this.reportCategory = reportCategory;
		this.reportContent = reportContent;
		this.reportImage = reportImage;
		this.processsing = processsing;
	}

}
