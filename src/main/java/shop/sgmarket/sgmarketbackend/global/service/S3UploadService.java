package shop.sgmarket.sgmarketbackend.global.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.sgmarket.sgmarketbackend.global.error.ErrorCode;
import shop.sgmarket.sgmarketbackend.global.error.exception.CustomException;
import shop.sgmarket.sgmarketbackend.global.properties.S3Properties;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private static final String FILE_NAME_DELIMITER = "/";
    private static final String FILE_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";
    private static final String FILE_EXTENSION_DELIMITER = ".";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public String uploadImage(MultipartFile file, String keyPrefix) {
        validateS3Config();

        String extension = getFileExtension(file.getOriginalFilename());
        String key = String.format("%s%s%s.%s", keyPrefix, FILE_NAME_DELIMITER, UUID.randomUUID(), extension);

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            return String.format(FILE_URL_FORMAT, s3Properties.bucket(), s3Properties.region(), key);

        } catch (IOException | SdkClientException e) {
            log.error("S3 이미지 업로드 실패", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED, e.getMessage());
        }
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(FILE_EXTENSION_DELIMITER)) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED, originalFilename);
        }
        return originalFilename.substring(originalFilename.lastIndexOf(FILE_EXTENSION_DELIMITER) + 1);
    }

    private void validateS3Config() {
        if (s3Properties.bucket() == null || s3Properties.bucket().isBlank()) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
        if (s3Properties.region() == null || s3Properties.region().isBlank()) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}
