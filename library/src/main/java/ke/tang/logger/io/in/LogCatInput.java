package ke.tang.logger.io.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ke.tang.logger.Log;
import ke.tang.logger.LogIOException;

/**
 * @author tangke
 */

public class LogCatInput implements LogInput {
    private Process mProcess;
    private BufferedReader mReader;

    public LogCatInput() {
        connect();
    }

    @Override
    public Log read() throws LogIOException {
        try {
            String line = mReader.readLine();
            if (null != line /*&& line.contains(String.valueOf(Process.myPid()))*/) {
                //Android系统限制，执行的logcat命令只回返回当前进程的log，所以无需过滤
                return Log.obtain(String.format("%s\n", line));
            }
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public void connect() {
        try {
            mProcess = Runtime.getRuntime().exec("logcat");
            mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (null != mReader) {
            try {
                mReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != mProcess) {
            mProcess.destroy();
        }
    }
}
