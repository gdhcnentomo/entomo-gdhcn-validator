package co.entomo.gdhcn.service.impl;/**
 * @author Uday Matta
 */

import co.entomo.gdhcn.service.GdhcnFileSystem;
import co.entomo.gdhcn.util.AmazonClientUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * AWS S3 implementation of the GdhcnFileSystem interface.
 *
 * This service handles the uploading and downloading of JSON files
 * to and from an AWS S3 bucket.
 *
 * @author Uday Matta
 * @organization entomo Labs
 */
@Profile("aws")
@Service
public class AwsFileSystem implements GdhcnFileSystem
{

     @Value("${aws.bucket.s3.json.folder}")
     private String jsonFolder;
     @Value("${aws.bucket.s3.bucket.name}")
     private String bucketName;


     @Autowired
     private AmazonClientUtil amazonClientUtil;

     /**
      * Uploads a JSON file to the AWS S3 bucket using the provided file name and content.
      *
      * @param fileName    the name of the file to be uploaded.
      * @param fileContent the content of the file to be uploaded as a String.
      */
     @Override
     public void uploadJson(String fileName, String fileContent) {
          amazonClientUtil.uploadFileToBucket(fileName,fileContent,jsonFolder);
     }

     /**
      * Uploads a JSON file to the AWS S3 bucket using the provided file name and File object.
      *
      * @param fileName the name of the file to be uploaded.
      * @param file     the File object representing the file to be uploaded.
      */
     @Override
     public void uploadJson(String fileName, File file) {
          amazonClientUtil.uploadFileToBucket(fileName,file,jsonFolder);
     }

     /**
      * Downloads a JSON file from the AWS S3 bucket using the provided JSON file ID.
      *
      * @param jsonId the ID of the JSON file to be downloaded.
      * @return an InputStream for reading the contents of the JSON file.
      */
     @Override
     public InputStream downloadJson(String jsonId) {
          return amazonClientUtil.getFileInputStream(jsonId,jsonFolder);
     }

     /**
      * Retrieves the full path of the file in the AWS S3 bucket using the provided file name.
      *
      * @param fileName the name of the file whose path is to be retrieved.
      * @return the full path of the file in the AWS S3 bucket as a String.
      */
     @Override
     public String getPath(String fileName) {
         return bucketName + File.separator + jsonFolder + File.separator + fileName;
     }
}
