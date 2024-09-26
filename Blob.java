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
    private String pathName;
    private String fileName;
    private String hashName;
    private boolean isDirectory;
    
    public Blob(String pathName, boolean compressed) throws IOException{
        File blobFile = new File(pathName);
        this.pathName = pathName;
        if(blobFile.exists()) // checks to see if this file even exists
        {
            isDirectory = blobFile.isDirectory();
            // if file is in main directory
            if (blobFile.getParentFile() == null)
                fileName = blobFile.getName();
            else
                fileName = blobFile.getParentFile().getName() + "/" + blobFile.getName();
            
            if (isDirectory){
                File tempFile = File.createTempFile("directoryStorage", null);
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                for (File child : blobFile.listFiles()){
                    if (!blobFile.equals(child)){
                        Blob tempBlob = new Blob (child.toPath().toString(), Git.COMPRESS);
                        if (tempBlob.isDirectory())
                            writer.write ("tree " + tempBlob.getHashName() + " " + child.getName() + "\n");
                        else
                            writer.write ("blob " + tempBlob.getHashName() + " " + child.getName() + "\n");
                    }
                }
                writer.close();
                blobFile = tempFile;
            }
            hashName = generateName(blobFile, compressed);
            File backup = new File("git/objects/" + hashName);
            if(!backup.exists()) //checks to see if it is already backed up
            {
                backup.createNewFile(); //this part creates the backup
                BufferedWriter bufferWritter = new BufferedWriter(new FileWriter(backup,true));
                bufferWritter.write(getData(blobFile));
                bufferWritter.close();
            }

            boolean isInIndex = false;
            String index;
            if (isDirectory)
                index = "tree " + hashName + " " + fileName;
            else
                index = "blob " + hashName + " " + fileName;
            BufferedReader reader = new BufferedReader(new FileReader("./git/index"));
            while (reader.ready()){
                if (index.equals(reader.readLine()))
                    isInIndex = true;
            }
            reader.close();
            if (!isInIndex){
                File indexFile = new File("git/index"); // this part adds it to index
                BufferedWriter bufferWritter2 = new BufferedWriter(new FileWriter(indexFile,true));
                if (isDirectory)
                    bufferWritter2.write("tree " + hashName + " " + fileName + "\n");
                else
                    bufferWritter2.write("blob " + hashName + " " + fileName + "\n");
                bufferWritter2.close();
            }
        }
        else{
            throw new NoSuchFileException(pathName); //how to get fileName if it doesn't exist?
        }
    }

    // takes a String of data and uses SHA1 to turn it into a unique filename String; all me baby
    public String generateName(File file, boolean compressed) throws IOException 
    {
        String data = getData(file); //first we get the data inside the file
        if(compressed) // then we may or may not compress the data
            data = compress(data);
        byte[] bytes = data.getBytes(); //then turn the data into bytes
        return toSHA1(bytes); //then turn the bytes into SHA1
    }

    // reads and returns the data from a file; written all by myself :)
    public String getData(File file) throws IOException 
    {
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder data = new StringBuilder("");
        while (bReader.ready()) {
            data.append((char) bReader.read());
        }
        bReader.close();
        return data.toString();
    }

    //converts a byte array into SHA1; 
    //https://stackoverflow.com/questions/4895523/java-string-to-sha1
    public String toSHA1(byte[] convertme) { 
        MessageDigest md = null; //don't really understand what any of this does, but it works
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        return byteArrayToHexString(md.digest(convertme));
    }   

    //converts byte array into a hex string. part of the SHA1 conversion process; 
    // https://stackoverflow.com/questions/4895523/java-string-to-sha1
    public String byteArrayToHexString(byte[] b) { 
        String result = "";
        for (int i=0; i < b.length; i++) {
          result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    //compresses a string. source is 
    // https://stackoverflow.com/questions/3649485/how-to-compress-a-string-in-java
    public String compress(String str) throws IOException { 
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
    public boolean isDirectory()
    {
        return isDirectory;
    }
}