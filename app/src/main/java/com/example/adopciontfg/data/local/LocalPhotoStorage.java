package com.example.adopciontfg.data.local;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LocalPhotoStorage {

    private static final String PHOTOS_DIR = "photos";

    private final Context context;
    private final File photosRoot;

    public LocalPhotoStorage(Context context) {
        this.context = context.getApplicationContext();
        this.photosRoot = new File(this.context.getFilesDir(), PHOTOS_DIR);
    }

    public String copyPhotoIfNeeded(String photoUri, String folderName, String fileName) throws IOException {
        if (photoUri == null || photoUri.isBlank()) return "";

        Uri uri = Uri.parse(photoUri);
        if (isStoredPhotoUri(uri)) return photoUri;
        if (!isReadableLocalUri(uri)) return photoUri;

        File targetDir = new File(photosRoot, sanitizePathSegment(folderName));
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta local de fotos");
        }

        File targetFile = new File(targetDir, sanitizeFileName(fileName));
        try (InputStream input = openInputStream(uri);
             FileOutputStream output = new FileOutputStream(targetFile, false)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }

        return Uri.fromFile(targetFile).toString();
    }

    public void deletePhoto(String photoUri) {
        if (photoUri == null || photoUri.isBlank()) return;

        Uri uri = Uri.parse(photoUri);
        if (!isStoredPhotoUri(uri)) return;

        File file = new File(uri.getPath());
        if (isInsidePhotosRoot(file)) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    public void deletePhotos(Collection<String> photoUris) {
        if (photoUris == null) return;
        for (String photoUri : photoUris) {
            deletePhoto(photoUri);
        }
    }

    public void deletePhotosNotIn(Collection<String> oldPhotoUris, Collection<String> keptPhotoUris) {
        if (oldPhotoUris == null) return;

        Set<String> kept = keptPhotoUris == null ? new HashSet<>() : new HashSet<>(keptPhotoUris);
        for (String oldPhotoUri : oldPhotoUris) {
            if (!kept.contains(oldPhotoUri)) {
                deletePhoto(oldPhotoUri);
            }
        }
    }

    private InputStream openInputStream(Uri uri) throws IOException {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new FileInputStream(new File(uri.getPath()));
        }

        InputStream input = context.getContentResolver().openInputStream(uri);
        if (input == null) {
            throw new IOException("No se pudo abrir la foto seleccionada");
        }
        return input;
    }

    private boolean isReadableLocalUri(Uri uri) {
        String scheme = uri.getScheme();
        return "content".equalsIgnoreCase(scheme) || "file".equalsIgnoreCase(scheme);
    }

    private boolean isStoredPhotoUri(Uri uri) {
        if (!"file".equalsIgnoreCase(uri.getScheme()) || uri.getPath() == null) return false;
        return isInsidePhotosRoot(new File(uri.getPath()));
    }

    private boolean isInsidePhotosRoot(File file) {
        try {
            String rootPath = photosRoot.getCanonicalPath();
            String filePath = file.getCanonicalPath();
            return filePath.equals(rootPath) || filePath.startsWith(rootPath + File.separator);
        } catch (IOException exception) {
            return false;
        }
    }

    private String sanitizePathSegment(String value) {
        if (value == null || value.isBlank()) return "general";
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private String sanitizeFileName(String value) {
        String sanitized = sanitizePathSegment(value);
        if (TextUtils.isEmpty(sanitized)) return "photo.jpg";
        return sanitized.toLowerCase(Locale.ROOT).endsWith(".jpg") ? sanitized : sanitized + ".jpg";
    }
}
