package ke.tang.logger.util;

import java.io.File;
import java.util.Comparator;

public class FileSortComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        long diff = o1.lastModified() - o2.lastModified();
        return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
    }
}
