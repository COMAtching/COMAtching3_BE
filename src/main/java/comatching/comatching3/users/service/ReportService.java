package comatching.comatching3.users.service;

import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import comatching.comatching3.aws.AwsS3Service;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.dto.request.ReportReq;
import comatching.comatching3.users.entity.Report;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.users.repository.ReportRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final UsersRepository usersRepository;
	private final ReportRepository reportRepository;
	private final SecurityUtil securityUtil;
	private final AwsS3Service awsS3Service;

	/**
	 * 유저 신고 메서드
	 * @param reportReq
	 * @param reportImage
	 */

	@Transactional
	public void report(ReportReq reportReq, MultipartFile reportImage) {

		// 사진 파일 검증 (비어있는지, 허용된 이미지 타입인지 확인)
		validateImageFile(reportImage);

		// 신고 내용 검증 및 정제 (최대 길이 체크, HTML/스크립트 제거)
		String sanitizedContent = sanitizeReportContent(reportReq.getReportContent());

		Users reporter = securityUtil.getCurrentUsersEntity();
		Users reportedUser = usersRepository.findBySocialId(reportReq.getReportedUserSocialId())
			.orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

		String s3Key = awsS3Service.uploadFile(reportImage, "report_image");

		Report report = Report.builder()
			.reporter(reporter)
			.reportedUser(reportedUser)
			.reportCategory(reportReq.getReportCategory())
			.reportContent(sanitizedContent)
			.reportImage(s3Key)
			.processsing(false)
			.build();

		reportRepository.save(report);
	}

	private void validateImageFile(MultipartFile file) {
		// 파일이 비어있는 경우 예외 발생
		if (file.isEmpty()) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
		// 허용된 이미지 MIME 타입 리스트
		List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
		if (!allowedTypes.contains(file.getContentType())) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
	}

	private String sanitizeReportContent(String content) {
		int maxLength = 300;
		if (content != null && content.length() > maxLength) {
			throw new BusinessException(ResponseCode.BAD_REQUEST);
		}
		// Jsoup을 사용하여 HTML, 스크립트 등 제거 (안전한 텍스트만 남김)
		return Jsoup.clean(content, Safelist.none());
	}



}
