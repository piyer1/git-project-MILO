import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Git {
    public Git () {

    }
    public void initRepo() throws IOException
    {
        if(new File("git/objects/index").exists())
        {
            System.out.println("Git Repository already exists");
        }
        else{
            File objects = new File("git/objects");
            objects.mkdirs();
            File index = new File("git/objects/index");
            index.createNewFile();
        }
    }
    public void checkAndDeleteRepo() // checks and deletes git directory and everythibg inside
    {
        File git = new File("git");
        if(git.exists())
        {
            deleteDirectory(git);
        }
    }
    public void deleteDirectory(File file) // recursively deletes all the directories and files in a directory
    //adapted from https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
    {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}