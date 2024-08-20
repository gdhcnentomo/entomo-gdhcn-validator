package co.entomo.gdhcn.service.impl;/**
 * @author Uday Matta
 */

import co.entomo.gdhcn.service.GdhcnFileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Local file system implementation of the GdhcnFileSystem interface.
 *
 * This service handles the uploading and downloading of JSON files
 * to and from a local file system.
 *
 * @author Uday Matta
 * @organization entomo Labs
 */
@Profile("fileSystem")
@Service
public class LocalFileSystem implements GdhcnFileSystem
{
    @Value("${filesystem.baseLocation}")
    private String baseLocation;
    @Value("${filesystem.folderName}")
    private String jsonFolder;

    /**
     * Uploads a JSON file to the local file system using the provided file name and content.
     *
     * @param fileName    the name of the file to be uploaded.
     * @param fileContent the content of the file to be uploaded as a String.
     * @throws IOException if an I/O error occurs during the upload process.
     */
    @Override
    public void uploadJson(String fileName, String fileContent) throws IOException {
        String location = baseLocation.endsWith(File.separator)? baseLocation + jsonFolder +File.separator + fileName: baseLocation + File.separator + jsonFolder + File.separator + fileName;
        Path path = Paths.get(location);

        Files.writeString(path, fileContent, StandardOpenOption.CREATE_NEW);
    }

    /**
     * Uploads a JSON file to the local file system using the provided file name and File object.
     *
     * @param fileName the name of the file to be uploaded.
     * @param file     the File object representing the file to be uploaded.
     * @throws IOException if an I/O error occurs during the upload process.
     */
    @Override
    public void uploadJson(String fileName, File file) throws IOException {
        InputStream is = new FileInputStream(file);
        String location = baseLocation.endsWith(File.separator)? baseLocation + jsonFolder +File.separator + fileName: baseLocation + File.separator + jsonFolder + File.separator + fileName;
        Path path = Paths.get(location);
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Downloads a JSON file from the local file system using the provided JSON file ID.
     *
     * @param jsonId the ID of the JSON file to be downloaded.
     * @return an InputStream for reading the contents of the JSON file.
     */
    @Override
    public InputStream downloadJson(String jsonId) throws FileNotFoundException {
        String fileName = jsonId+".json";
        String fileLocation = getPath(jsonId);
        return new FileInputStream(fileLocation);
    }

    /**
     * Retrieves the full path of the file in the local file system using the provided file name.
     *
     * @param fileName the name of the file whose path is to be retrieved.
     * @return the full path of the file in the local file system as a String.
     */
    @Override
    public String getPath(String fileName) {
        return baseLocation.endsWith(File.separator) ? baseLocation  + jsonFolder + File.separator + fileName : baseLocation +File.separator + jsonFolder + File.separator + fileName;
    }
}
