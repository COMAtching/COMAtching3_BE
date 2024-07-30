package comatching.comatching3.aws;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class AwsS3Service {

    private S3Client s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @PostConstruct
    public void initializeAmazon() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }

    public String uploadFile(MultipartFile file, String category) {
        String key = category + "/" + UUID.randomUUID();
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, tempFile.toPath());

        } catch (IOException e) {
            log.error("S3에 파일 업로드 실패: {}", e.getMessage(), e);
            return null;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException e) {
                    log.error("임시 파일 삭제 실패: {}", e.getMessage(), e);
                }
            }
        }
        return key;
    }

    /**
     * S3 버킷의 파일 삭제
     * @param fileKey
     */
    public void deleteFile(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("File deleted from S3: " + fileKey);
    }

    /**
     * S3 키로부터 공개 URL을 생성하는 메소드
     * @param s3Key
     * @return 1시간 동안 유효한 공개 URL
     */
    public String getPublicUrl(String s3Key) {
        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .build();

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(b -> b.getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofHours(1)));

            return presignedRequest.url().toString();
        } finally {
            presigner.close();
        }
    }
}
