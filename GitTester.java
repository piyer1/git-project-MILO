import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitTester
{
    public static void main(String [] args) throws IOException
    {    
        Git banana = new Git();
        banana.checkAndDeleteRepo();
        banana.initRepo();

        File test = new File("testFile.txt"); //these two chunks of code are only used if you want to create new files or reset old ones
        if(test.exists())
            test.delete(); //RESETS it if needed
        FileWriter fileWritter = new FileWriter(test,true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write("this is a test", 0, 14);
        bufferWritter.close();
    
        File test2 = new File("testFile2.txt");
        if(test2.exists()) 
            test2.delete(); //RESETS it if needed
        FileWriter fileWritter2 = new FileWriter(test2,true);
        BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);
        bufferWritter2.write("this is a second test for secret reasons", 0, 40);
        bufferWritter2.close();

        banana.stage(test.getPath());
        banana.stage(test2.getPath());

        String commit1Hash = banana.commit("Pranav Iyer", "testing commit method");

        File test3 = new File("testFolder/");
        if (!test3.exists()){
            boolean bool1 = test3.mkdir();
        }
        File test4 = new File("testFolder/testfile3.txt");
        if (!test4.exists()){
            boolean bool2 = test4.createNewFile();
        }
        Blob.writeData(test4, "another test for folders", true);

        banana.stage(test3.getPath());
        banana.stage(test4.getPath());

        String commit2Hash = banana.commit("Pranav Iyer", "testing commit method for folders");

        banana.checkout(commit1Hash);

        /*Blob bouba = new Blob("testFile.txt", Git.COMPRESS); // testing the compression features
        String compressedHashName1 = bouba.toSHA1(bouba.compress("this is a test").getBytes());
        String compressedHashName2 = bouba.getHashName();
        if(compressedHashName1.equals(compressedHashName2))
        {
            System.out.println("Compression seems to work as intended.");
        }
        Blob bouba2 = new Blob("testFile2.txt", Git.COMPRESS);

        Blob jacobISpentTwoHoursDebuggingThisCode = new Blob("test", Git.COMPRESS);
    
      //   deleteRepo deletes EVERYTHING in git directory, including the directory itself
      //   so only do it when you want to reset it all
    */
    }
}