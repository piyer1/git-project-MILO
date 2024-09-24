import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;

public class Blob
{
    String pathName;
    String fileName;
    String hashName;
    public Blob(String pathName, boolean compressed) throws IOException{
        File blobFile = new File(pathName);
        this.pathName = pathName;
        if(blobFile.exists()) // checks to see if this file even exists
        {
            String hashName = generateName(blobFile, compressed);
            this.hashName = hashName;
            String fileName = blobFile.getName();
            this.fileName = fileName;
            File backup = new File("git/objects/" + hashName);
            if(!backup.exists()) //checks to see if it is already backed up
            {
                backup.createNewFile(); //this part creates the backup
                FileWriter fileWritter = new FileWriter(backup,true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                String contents = compress(getData(blobFile));
                bufferWritter.write(contents, 0, contents.length());
                bufferWritter.close();

                File index = new File("git/objects/index"); // this part adds it to index
                FileWriter fileWritter2 = new FileWriter(index,true);
                BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);
                bufferWritter2.write(hashName + " " + fileName + "\n", 0, hashName.length() + fileName.length() + 2);
                bufferWritter2.close();
            }
        }
        else{
            throw new NoSuchFileException(pathName); //how to get fileName if it doesn't exist?
        }
    }
    public String generateName(File file, boolean compressed) throws IOException // takes a String of data and uses SHA1 to turn it into a unique filename String; all me baby
    {
        String data = getData(file); //first we get the data inside the file
        if(compressed) // then we may or may not compress the data
            data = compress(data);
        byte[] bytes = data.getBytes(); //then turn the data into bytes
        return toSHA1(bytes); //then turn the bytes into SHA1
    }
    public String getData(File file) throws IOException // reads and returns the data from a file; written all by myself :)
    {
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder data = new StringBuilder("");
        while(bReader.ready())
        {
            data.append((char)bReader.read());
        }
        bReader.close();
        return data.toString();
    }
    public String toSHA1(byte[] convertme) { //converts a byte array into SHA1; https://stackoverflow.com/questions/4895523/java-string-to-sha1
        MessageDigest md = null; //don't really understand what any of this does, but it works
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        return byteArrayToHexString(md.digest(convertme));
    }   
    public String byteArrayToHexString(byte[] b) { //converts byte array into a hex string. part of the SHA1 conversion process; https://stackoverflow.com/questions/4895523/java-string-to-sha1
        String result = "";
        for (int i=0; i < b.length; i++) {
          result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    public String compress(String str) throws IOException { //compresses a string. source is https://stackoverflow.com/questions/3649485/how-to-compress-a-string-in-java
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("ISO-8859-1");
    }
    public String getPathName()
    {
        return pathName;
    }
    public String getFileName()
    {
        return fileName;
    }
    public String getHashName()
    {
        return hashName;
    }
}