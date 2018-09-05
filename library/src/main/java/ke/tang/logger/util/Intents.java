package ke.tang.logger.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

import ke.tang.logger.R;

/**
 * 所有用到的{@link Intent}
 *
 * @author tangke
 */

public class Intents {
    public static Intent view(Context context, File file, String mimeType) {
        final Uri uri = FileProvider.getUriForFile(context, Common.getLoggerProviderAuthorities(context), file);
        return view(context, uri, mimeType);
    }

    public static Intent view(Context context, Uri file, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(file, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return Intent.createChooser(intent, context.getString(R.string.logger_title_view_file));
    }

    public static Intent send(Context context, File file) {
        final Uri uri = FileProvider.getUriForFile(context, Common.getLoggerProviderAuthorities(context), file);
        return send(context, uri);
    }

    public static Intent send(Context context, Uri file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_STREAM, file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return Intent.createChooser(intent, context.getString(R.string.logger_title_send_file));
    }

    public static Intent send(Context context, Uri... uris) {
        ArrayList<Uri> resources = new ArrayList<>();
        for (Uri uri : uris) {
            resources.add(uri);
        }
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("application/octet-stream");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, resources);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return Intent.createChooser(intent, context.getString(R.string.logger_title_send_file));
    }
}
