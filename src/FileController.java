import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TreeMap;

public class FileController {
    private static SearchFiles sf = null;

    public static void Initialize(String[] inputFolders) {
        sf = new SearchFiles();
        for (String folder : inputFolders) {
            try {
                Files.walkFileTree(Paths.get(folder), sf);
            } catch (Exception ex) {
                System.out.println("Error occured: " + ex.getMessage());
                System.exit(0);
            }
        }
        calculateTotalSizes();
    }

    public static void serializeFile(String fileName, TreeMap<String, FileInfo> map) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (Exception ex) {
            System.out.println("Error occured while serializing");
        }
    }

    @SuppressWarnings("unchecked")
    public static TreeMap<String, FileInfo> deserializeFile(String fileName)
            throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        TreeMap<String, FileInfo> output = (TreeMap<String, FileInfo>) ois.readObject();
        ois.close();
        fis.close();
        return output;
    }

    private static void calculateTotalSizes() {
        Iterator<String> its = SearchFiles.directorySizes.keySet().iterator();
        while (its.hasNext()) {
            String dir = its.next();

            Iterator<String> its2 = SearchFiles.directorySizes.keySet().iterator();
            while (its2.hasNext()) {
                String dir2 = its2.next();
                if ((dir2+"\\").contains(dir+"\\") && !(dir2+"\\").equals(dir+"\\")) {
                    long size = SearchFiles.directorySizes.get(dir);
                    size += SearchFiles.directorySizes.get(dir2);
                    SearchFiles.directorySizes.replace(dir, size);
                }
            }
        }
    }

    public static void printStatistics() {
        System.out.println("File extension statistics:");
        Iterator<String> its = SearchFiles.fileExtensionStatistics.keySet().iterator();
        while (its.hasNext()) {
            String extension = its.next();
            System.out.println("." + extension + ": " + SearchFiles.fileExtensionStatistics.get(extension));
        }
    }

    public static void printTree() {
        System.out.println("File tree (with sizes):");
        Iterator<String> its = SearchFiles.directorySizes.keySet().iterator();
        int tabCount = 0;
        while (its.hasNext()) {
            String dir = its.next();

            Iterator<String> its2 = SearchFiles.directorySizes.keySet().iterator();
            while (its2.hasNext()) {
                String dir2 = its2.next();
                if (dir.contains(dir2) && !dir.equals(dir2))
                    tabCount++;
            }
            for (int i = 0; i < tabCount; i++)
                System.out.print("\t");
            System.out.println(dir + " size: " + SearchFiles.directorySizes.get(dir));
            tabCount = 0;
        }
    }

    public static void compareNew(TreeMap<String, FileInfo> oldMap) {
        try {
            System.out.println("New files:");
            Iterator<String> its = SearchFiles.currentFiles.keySet().iterator();
            while (its.hasNext()) {
                String dir = its.next();
                if (!oldMap.containsKey(dir)) {
                    System.out.println(dir);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void compareModified(TreeMap<String, FileInfo> oldMap) {
        try {
            System.out.println("Modified files:");
            Iterator<String> its = oldMap.keySet().iterator();
            while (its.hasNext()) {
                String dir = its.next();
                if (SearchFiles.currentFiles.containsKey(dir) && !SearchFiles.currentFiles.get(dir)
                        .getModificationDate().equals(oldMap.get(dir).getModificationDate())) {
                    System.out.println(dir + " modified: " + oldMap.get(dir).getModificationDate());
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void compareOld(TreeMap<String, FileInfo> oldMap) {
        try {
            System.out.println("Old files:");
            Iterator<String> its = oldMap.keySet().iterator();
            while (its.hasNext()) {
                String dir = its.next();
                if (SearchFiles.currentFiles.containsKey(dir)) {
                    System.out.println(dir);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    static void compareDeleted(TreeMap<String, FileInfo> oldMap) {
        try {
            System.out.println("Deleted files:");
            Iterator<String> its = oldMap.keySet().iterator();
            while (its.hasNext()) {
                String dir = its.next();
                if (!SearchFiles.currentFiles.containsKey(dir)) {
                    System.out.println(dir);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void search(String name, String date, long size) {
        try {
            System.out.println("Found files:");
            Iterator<String> its = SearchFiles.currentFiles.keySet().iterator();
            while (its.hasNext()) {
                String dir = its.next();
                FileInfo file = SearchFiles.currentFiles.get(dir);
                String outDir = "";

                if ((name == null || name.length()==0 || file.getName().equals(name)) && (size < 0 || file.getSize() == size)
                        && (date == null || new SimpleDateFormat("dd/MM/yyyy HH:mm").format(file.getModificationDate())
                        .equals(date))) {
                    outDir = dir;
                }

                if (outDir.length() != 0)
                    System.out.println(outDir);
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
