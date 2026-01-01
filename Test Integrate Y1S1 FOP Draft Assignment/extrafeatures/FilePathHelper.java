package extrafeatures;

import java.io.File;

class FilePathHelper {
    // Try common locations (project root, src, build/classes) for reading existing CSVs
    public static String resolveReadPath(String filename) {
        String[] candidates = {
            filename,
            "src/" + filename,
            "../" + filename,
            "../../" + filename,
            "build/classes/" + filename,
            "dist/" + filename
        };
        for (int i = 0; i < candidates.length; i++) {
            String c = candidates[i];
            File f = new File(c);
            if (f.exists() && f.isFile()) return c;
        }
        return filename; // fallback
    }

    // For writing: if file exists somewhere, overwrite that exact one; else write to project root filename
    public static String resolveWritePath(String filename) {
        String existing = resolveReadPath(filename);
        return existing;
    }
}
