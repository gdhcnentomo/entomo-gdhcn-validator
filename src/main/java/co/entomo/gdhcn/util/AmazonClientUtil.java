package co.entomo.gdhcn.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.S3Object;

import jakarta.annotation.PostConstruct;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AmazonClientUtil {

	private AmazonS3 s3client;
	
	@Value("${aws.bucket.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.bucket.access.key}")
    private String accessKey;
    @Value("${aws.bucket.access.secret}")
    private String secretKey;
    
	@PostConstruct
    private void initializeAmazonClient() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1).build();
    }
	
	public void uploadFileToBucket(String fileName, String fileContent, String folderToUpload) {
        log.info("Uploading file {} to {}", fileName, folderToUpload);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        InputStream stream = new ByteArrayInputStream(fileContent.getBytes(Charset.forName("UTF-8")));
        s3client.putObject(new PutObjectRequest(bucketName, folderToUpload + "/" + fileName, stream, metadata));
    }
	
	public void uploadFileToBucket(String fileName, File file, String folderToUpload) {
        log.info("Uploading file {} to {}", fileName, folderToUpload);
        s3client.putObject(new PutObjectRequest(bucketName, folderToUpload + "/" + fileName, file));
    }
	
	public File getFileFromBucket(String filename, String folderName) 
	{
        InputStream inputStream = getFileInputStream(filename, folderName);
        File file = new File(filename);
        /**
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            log.error(e.getMessage());
            return file;
        }
        */
        return file;
    }
    public InputStream getFileInputStream(String filename, String folderName) {
        S3Object s3object = s3client.getObject(bucketName, folderName + "/" + filename);
        return s3object.getObjectContent();
    }
}
