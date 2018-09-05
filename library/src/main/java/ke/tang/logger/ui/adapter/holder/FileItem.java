
package ke.tang.logger.ui.adapter.holder;

import android.widget.Checkable;

import java.io.File;

/**
 * @author tangke
 */
public class FileItem implements Checkable {
    boolean mIsChecked;
    File mFile;

    public FileItem(File file) {
        mFile = file;
    }

    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public File getFile() {
        return mFile;
    }
}