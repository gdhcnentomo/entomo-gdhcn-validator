package co.entomo.gdhcn.service;/**
 * @author Uday Matta
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @organization Entomo Labs
 * Interface for handling JSON files within a file system.
 *
 * This interface provides methods for uploading, downloading, and retrieving
 * the path of JSON files.
 */
public interface GdhcnFileSystem
{
    /**
     * Uploads a JSON file to the file system using the provided file name and content.
     *
     * @param fileName    the name of the file to be uploaded.
     * @param fileContent the content of the file to be uploaded as a String.
     * @throws IOException if an I/O error occurs during the upload process.
     */
    public void uploadJson(String fileName, String fileContent) throws IOException;
    /**
     * Uploads a JSON file to the file system using the provided file name and File object.
     *
     * @param fileName the name of the file to be uploaded.
     * @param file     the File object representing the file to be uploaded.
     * @throws IOException if an I/O error occurs during the upload process.
     */
    public void uploadJson(String fileName, File file) throws IOException;
    /**
     * Downloads a JSON file from the file system using the provided JSON file ID.
     *
     * @param jsonId the ID of the JSON file to be downloaded.
     * @return an InputStream for reading the contents of the JSON file.
     */
    public InputStream downloadJson(String jsonId) throws FileNotFoundException;
    /**
     * Retrieves the file system path of the file with the provided name.
     *
     * @param fileName the name of the file whose path is to be retrieved.
     * @return the path of the file as a String.
     */
    public String getPath(String fileName);
}
