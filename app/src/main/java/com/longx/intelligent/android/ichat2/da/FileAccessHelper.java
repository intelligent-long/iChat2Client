package com.longx.intelligent.android.ichat2.da;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by LONG on 2024/1/21 at 9:23 PM.
 */
public class FileAccessHelper {
    public static File createFile(String path) throws IOException {
        File file = new File(path);
        int number = 1;
        while (file.exists()){
            String pathWithoutExtension = path.substring(0, path.lastIndexOf('.'));
            String extension = path.substring(path.lastIndexOf('.'));
            file = new File(pathWithoutExtension + " (" + number + ")" + extension);
            number ++;
        }
        Objects.requireNonNull(file.getParentFile()).mkdirs();
        file.createNewFile();
        return file;
    }

    public static String save(InputStream contentStream, String path) throws IOException {
        File file = createFile(path);
        try (FileOutputStream outputStream = new FileOutputStream(file)){
            FileUtil.transfer(contentStream, outputStream);
            return file.getAbsolutePath();
        }
    }

    public static String save(byte[] bytes, String path) throws IOException {
        File file = createFile(path);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
            return file.getAbsolutePath();
        }
    }

    public static InputStream streamOf(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            ErrorLogger.log(FileAccessHelper.class, e);
            return null;
        }
    }

    public static byte[] bytesOf(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(file);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            ErrorLogger.log(FileAccessHelper.class, e);
            return null;
        }
    }

    public static String detectFileExtension(byte[] bytes) {
        Tika tika = new Tika();
        String mimeType;
        mimeType = tika.detect(bytes);
        MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeTypeObj;
        try {
            mimeTypeObj = defaultMimeTypes.forName(mimeType);
        } catch (MimeTypeException e) {
            ErrorLogger.log(e);
            return "";
        }
        String extension = mimeTypeObj.getExtension();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        return extension;
    }

    public static String detectFileExtension(File file) {
        Tika tika = new Tika();
        String mimeType;
        try {
            mimeType = tika.detect(file);
        } catch (IOException e) {
            ErrorLogger.log(e);
            return "";
        }
        MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
        MimeType mimeTypeObj;
        try {
            mimeTypeObj = defaultMimeTypes.forName(mimeType);
        } catch (MimeTypeException e) {
            ErrorLogger.log(e);
            return "";
        }
        String extension = mimeTypeObj.getExtension();
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        return extension;
    }

    @SuppressLint("Range")
    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getFileExtensionFromUri(Context context, Uri uri){
        String fileName = getFileNameFromUri(context, uri);
        return FileUtil.getFileExtension(fileName);
    }

    public static String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    public static void openFile(Context context, Uri fileUri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent = Intent.createChooser(intent, "选择打开此文件的应用");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            MessageDisplayer.autoShow(context, "没有找到能够打开该文件的应用", MessageDisplayer.Duration.LONG);
        }
    }

    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            String mimeType = getMimeType(filePath);
            openFile(context, fileUri, mimeType);
        } else {
            MessageDisplayer.autoShow(context, "文件不存在", MessageDisplayer.Duration.LONG);
        }
    }

    public static File detectAndRenameFile(File photoFile){
        String fileExtension = detectFileExtension(photoFile);
        File parentFile = photoFile.getParentFile();
        String fileName;
        if(parentFile != null) {
            fileName = parentFile.getAbsolutePath() + File.separator
                    + FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
        }else {
            fileName = FileUtil.getFileBaseName(photoFile) + "." + fileExtension;
        }
        File renamedFile = new File(fileName);
        boolean success = photoFile.renameTo(renamedFile);
        if(success) {
            return renamedFile;
        }else {
            return null;
        }
    }
}
