import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.util.Date;

public class FileInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private long size;
    private Date modificationDate;

    public FileInfo() {
    }

    public FileInfo(String name, long size, FileTime modificationDate) {
        this.name = name;
        this.size = size;
        this.modificationDate = new Date(modificationDate.toMillis());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(FileTime modificationDate) {
        this.modificationDate = new Date(modificationDate.toMillis());
    }
}