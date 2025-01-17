import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.FileReader;

public class Git implements GitInterface{
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

    //returns commit hash
    public String commit(String author, String message){
        try {
            updateIndex();
            File head = new File("git/HEAD");
            File index = new File ("git/index");
            String tree = Blob.generateName(index, false);
            File treeFile = new File("git/objects/"+ tree);
            Blob.writeData(treeFile, Blob.getData(index), true);
            String parent = Blob.getData(new File("git/HEAD"));
            String date = LocalDate.now().toString();
            String commitData = ("tree: " + tree + "\n" + "parent: " + parent + "\n" + "author: " + author + "\n" + "date: " + date + "\n" + "message: " + message + "\n");
            String commitHash = Blob.toSHA1(commitData.getBytes());
            Blob.writeData(head, commitHash, true);
            File commit = new File("git/objects/" + commitHash);
            Blob.writeData(commit, commitData, true);
            if (index.delete()){
                index.createNewFile();
            }
            return(commitHash);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return("Commit failed.");
    }

    public void stage(String filePath){
        try {
            Blob blob = new Blob(filePath, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //go to head; go to commit file; read what is after tree: ; read what is in tree file, add that to index file;
    public static void updateIndex() throws IOException{
        File head = new File("git/HEAD");
        File index = new File ("git/index");
        if (Blob.getData(head) != ""){
            File currentCommit = new File("git/objects/" + Blob.getData(head));
            BufferedReader br = new BufferedReader(new FileReader(currentCommit));
            String line;
            String tree = "";
            while ((line = br.readLine()) != null) {
             if (line.contains("tree:")){
                tree = line.substring(6);
               break;
             }
            }
            br.close();
            File treeFile = new File("git/objects/" + tree);
            BufferedReader brTree = new BufferedReader(new FileReader(treeFile));
            String treeLine = brTree.readLine();
            while (treeLine != null){
                BufferedReader brIndex = new BufferedReader(new FileReader(index));
                String indexLine = brIndex.readLine();
                boolean isDuplicate = false;
                while (indexLine != null){
                    String fileIndexName = indexLine.substring(46);
                    String fileTreeName = treeLine.substring(46);
                    if (fileIndexName.equals(fileTreeName)){
                        isDuplicate = true;
                    }
                    indexLine = brIndex.readLine();
                }
                if (!isDuplicate){
                    Blob.writeData(index, treeLine + "\n", true);
                }
                brIndex.close();
                treeLine = brTree.readLine();
            }
            brTree.close();
        }     
    }

    public void checkout(String commitHash){
        File head = new File("git/HEAD");
        File index = new File ("git/index");
        try {
            if (head.delete()){
                head.createNewFile();
            }
            Blob.writeData(head, commitHash, true);
            File commit = new File("git/objects/" + commitHash);
            BufferedReader br = new BufferedReader(new FileReader(commit));
            String line;
            String tree = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("tree:")){
                    tree = line.substring(6);
                    break;
                }
            }
        br.close();
        File treeFile = new File("git/objects/" + tree);
        Blob.writeData(index, Blob.getData(treeFile), false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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