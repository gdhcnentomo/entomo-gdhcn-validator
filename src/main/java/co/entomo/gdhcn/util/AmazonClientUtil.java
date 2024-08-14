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

/**
 *  @author Uday Matta
 *  @organization entomo Labs
 * Utility class for interacting with Amazon S3.
 * This class provides methods for uploading and retrieving files from an S3 bucket.
 */
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

    /**
     * Initializes the Amazon S3 client with the provided credentials and region.
     * This method is automatically called after the bean's properties have been set.
     */
	@PostConstruct
    private void initializeAmazonClient() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1).build();
    }
    /**
     * Uploads a file as a string to the specified folder in the S3 bucket.
     *
     * @param fileName       the name of the file to be uploaded.
     * @param fileContent    the content of the file as a {@code String}.
     * @param folderToUpload the folder within the S3 bucket where the file will be uploaded.
     */
	public void uploadFileToBucket(String fileName, String fileContent, String folderToUpload) {
        log.info("Uploading file {} to {}", fileName, folderToUpload);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        InputStream stream = new ByteArrayInputStream(fileContent.getBytes(Charset.forName("UTF-8")));
        s3client.putObject(new PutObjectRequest(bucketName, folderToUpload + "/" + fileName, stream, metadata));
    }
    /**
     * Uploads a file to the specified folder in the S3 bucket.
     *
     * @param fileName       the name of the file to be uploaded.
     * @param file           the {@code File} object to be uploaded.
     * @param folderToUpload the folder within the S3 bucket where the file will be uploaded.
     */
	public void uploadFileToBucket(String fileName, File file, String folderToUpload) {
        log.info("Uploading file {} to {}", fileName, folderToUpload);
        s3client.putObject(new PutObjectRequest(bucketName, folderToUpload + "/" + fileName, file));
    }

    /**
     * Retrieves a file from the specified folder in the S3 bucket and returns it as a {@code File} object.
     *
     * @param filename   the name of the file to be retrieved.
     * @param folderName the folder within the S3 bucket where the file is stored.
     * @return a {@code File} object representing the retrieved file.
     */
	public File getFileFromBucket(String filename, String folderName) 
	{
        InputStream inputStream = getFileInputStream(filename, folderName);
        File file = new File(filename);
        return file;
    }
    /**
     * Retrieves a file from the specified folder in the S3 bucket and returns its content as an {@code InputStream}.
     *
     * @param filename   the name of the file to be retrieved.
     * @param folderName the folder within the S3 bucket where the file is stored.
     * @return an {@code InputStream} representing the content of the retrieved file.
     */
    public InputStream getFileInputStream(String filename, String folderName) {
        S3Object s3object = s3client.getObject(bucketName, folderName + "/" + filename);
        return s3object.getObjectContent();
    }
}
