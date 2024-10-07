import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class Git {
    //global variable to enable or disable compressing
    public static final boolean COMPRESS = false;

    public Git () {

    }
    public void initRepo() throws IOException
    {
        if(new File("git/index").exists())
        {
            System.out.println("Git Repository already exists");
        }
        else{
            File objects = new File("git/objects");
            objects.mkdirs();
            File index = new File("git/index");
            index.createNewFile();
            File head = new File("git/HEAD");
            head.createNewFile();
        }
    }

    public static String commit(String author, String message) throws IOException{
        File index = new File ("git/index");
        String tree = Blob.generateName(index, false);
        File treeFile = new File("git/objects/"+ tree);
        Blob.writeData(treeFile, Blob.getData(index));
        String parent = Blob.getData(new File("git/HEAD"));
        String date = LocalDate.now().toString();
        String commitData = ("tree: " + tree + "\n" + "parent: " + parent + "\n" + "author: " + author + "\n" + "date: " + date + "\n" + "message: " + message + "\n");
        String commitHash = Blob.toSHA1(commitData.getBytes());
        File commit = new File("git/objects/" + commitHash);
        Blob.writeData(commit, commitData);
        if (index.delete()){
            index.createNewFile();
        }
        checkout(commitHash);
        return(commitHash);
    }

    public static void stage(String filePath) throws IOException{
        Blob blob = new Blob(filePath, false);
    }

    public static void checkout(String commitHash) throws IOException{
        File head = new File("git/HEAD");
        if (head.delete()){
            head.createNewFile();
        }
        Blob.writeData(head, commitHash);
        
    }

    public void checkAndDeleteRepo() // checks and deletes git directory and everything inside
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