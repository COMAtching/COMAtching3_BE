package comatching.comatching3.admin.service;

import comatching.comatching3.admin.dto.request.UniversityReq;
import comatching.comatching3.admin.entity.University;
import comatching.comatching3.admin.repository.UniversityRepository;
import comatching.comatching3.aws.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityService {

    private final AwsS3Service awsS3Service;
    private final UniversityRepository universityRepository;


    /**
     * 대학 정보와 로고를 입력받아 등록하는 메소드
     * @param form 대학 정보
     * @param image 대학 로고
     */
    public void createUniversity(UniversityReq form, MultipartFile image) {

        String s3Key = awsS3Service.uploadFile(image, "university_logo");

        University university = University.builder()
                .universityName(form.getName())
                .mailDomain(form.getMailDomain())
                .s3Key(s3Key)
                .appName(form.getAppName())
                .build();

        universityRepository.save(university);
    }
}
