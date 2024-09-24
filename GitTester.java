import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitTester
{
    public static void main(String [] args) throws IOException
    {
        boolean compressed = true; // global variable
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
    
        // File test2 = new File("testFile2.txt");
        // if(test2.exists()) 
        //     test2.delete(); //RESETS it if needed
        // FileWriter fileWritter2 = new FileWriter(test2,true);
        // BufferedWriter bufferWritter2 = new BufferedWriter(fileWritter2);
        // bufferWritter2.write("this is a second test for secret reasons", 0, 40);
        // bufferWritter2.close();

        Blob bouba = new Blob("testFile.txt", compressed); // testing the compression features
        String compressedHashName1 = bouba.toSHA1(bouba.compress("this is a test").getBytes());
        String compressedHashName2 = bouba.getHashName();
        if(compressedHashName1.equals(compressedHashName2))
        {
            System.out.println("Compression seems to work as intended.");
        }
        // Blob bouba2 = new Blob("testFile2.txt", compressed);
    
      //   deleteRepo deletes EVERYTHING in git directory, including the directory itself
      //   so only do it when you want to reset it all

    }
}