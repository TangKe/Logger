package ke.tang.logger.io.out;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;

/**
 * @author tangke
 */

public class FileLogOutput implements LogOutput {
    private File mFile;
    private Writer mWriter;

    private boolean mIsFileCreated;

    public FileLogOutput() {
        this(null);
    }

    public FileLogOutput(File file) {
        setFile(file);
    }

    @Override
    public void write(Log log) throws LogIOException {
        if (null != mWriter) {
            if (mIsFileCreated && !isFileExist()) {
                throw new LogIOException("文件不存在");
            }

            if (null != log.getContent()) {
                try {
                    mWriter.write(log.getContent());
                    mWriter.flush();
                    mIsFileCreated = true;
                } catch (IOException e) {
                    throw new LogIOException("文件写入出错");
                }
            }
        }
    }

    protected void setFile(File file) {
        mFile = file;
        connect();
    }

    public boolean isFileExist() {
        return mFile.exists();
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public void connect() {
        ensureFileClose();
        if (null != mFile) {
            try {
                mIsFileCreated = false;
                mWriter = new FileWriter(mFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        ensureFileClose();
    }

    private void ensureFileClose() {
        if (null != mWriter) {
            try {
                mWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
