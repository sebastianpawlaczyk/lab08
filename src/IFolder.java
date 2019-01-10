import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;
import java.util.TreeMap;

public class IFolder {
    private static Properties control = new Properties();

    private static String[] inputFolders = null;
    private static String output = null;
    private static String input = null;
    private static String[] operationName = null;
    private static String[] files2search = null;

    private static TreeMap<String, FileInfo> oldFile = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
        System.out.println("FileSnapShot");
        loadControlFile();
        FileController.Initialize(inputFolders);
        loadOldFile();

        for(int i=0; i<operationName.length; i++) handleOperation(i);

        FileController.serializeFile(output, SearchFiles.currentFiles);
    }

    private static void loadControlFile() throws IOException {

        try {
            FileInputStream in = new FileInputStream("cont.txt");
            control.load(in);
        } catch (FileNotFoundException ex) {
            System.out.println("The control file doesn't exits");
            System.exit(0);
        }

        inputFolders = control.getProperty("InputFolders").split(";");
        output = control.getProperty("Output");
        input = control.getProperty("Input");
        operationName = control.getProperty("Operation").split(";");
        files2search = control.getProperty("Files2search").split(";");
    }

    private static void loadOldFile() throws IOException {
        try {
            oldFile = FileController.deserializeFile(input);
        } catch (FileNotFoundException ex) {
            System.out.println("The control file didn't exits. It will be created.");
            FileController.serializeFile(output, SearchFiles.currentFiles);
            input = output;
            loadOldFile();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void handleOperation(int idx) {
        if (operationName == null || operationName[idx].length() == 0) {
            System.out.println("\nNo operation provided!");
            return;
        }

        System.out.println("\nOperation: " + operationName[idx]);

        switch (operationName[idx].toLowerCase()) {
            case "new":
                FileController.compareNew(oldFile);
                break;
            case "modified":
                FileController.compareModified(oldFile);
                break;
            case "old":
                FileController.compareOld(oldFile);
                break;
            case "deleted":
                FileController.compareDeleted(oldFile);
                break;
            case "statistics":
                FileController.printStatistics();
                break;
            case "treeview":
                FileController.printTree();
                break;
            case "search":
                if (files2search!=null && (files2search.length > 1 || (files2search.length == 1 && files2search[0].length() > 0))) {
                    FileController.search(files2search[0], files2search.length > 1 && files2search[1].length() > 0 ? files2search[1] : null,
                            files2search.length > 2 && files2search[2].length() > 0 ? Long.parseLong(files2search[2]) : -1);
                }
                else System.out.println("No search parameters specified");
                break;
            default:
                System.out.println("No matching operation");
        }

    }
}
