package comatching.comatching3.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import comatching.comatching3.admin.dto.request.UniversityReq;
import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.aws.AwsS3Service;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.Users;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityService {

	private final AwsS3Service awsS3Service;
	private final UniversityRepository universityRepository;
	private final SecurityUtil securityUtil;

	/**
	 * 대학 정보와 로고를 입력받아 등록하는 메소드
	 * @param form 대학 정보
	 * @param image 대학 로고
	 */
	@Transactional
	public void createUniversity(UniversityReq form, MultipartFile image) {

		String s3Key = awsS3Service.uploadFile(image, "university_logo");

		University university = University.builder()
			.universityName(form.getName())
			.mailDomain(form.getMailDomain())
			.s3Key(s3Key)
			.build();

		universityRepository.save(university);
	}

	public boolean checkEmailDomain(String schoolEmail, String schoolName) {
		University university = universityRepository.findByUniversityName(schoolName)
			.orElseThrow(() -> new BusinessException(ResponseCode.SCHOOL_NOT_EXIST));

		String[] emailParts = schoolEmail.split("@");

		if (emailParts.length != 2) {
			return false;
		}

		String domain = emailParts[1];
		return domain.equalsIgnoreCase(university.getMailDomain());
	}

	public void checkUniversity(Users user, Admin admin) {
		University university = securityUtil.getAdminFromContext().getUniversity();

		if (user != null) {
			if (!university.equals(user.getUniversity())) {
				throw new BusinessException(ResponseCode.NO_PERMISSION);
			}
		} else if (admin != null) {
			if (!university.equals(admin.getUniversity())) {
				throw new BusinessException(ResponseCode.NO_PERMISSION);
			}
		}
	}
}
