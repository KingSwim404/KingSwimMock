package com.kingswim.mock;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/13
 *     desc  : utils about convert
 *     为了减少依赖借鉴的部分代码
 * </pre>
 */
public final class ConvertUtils {

    private static final int    BUFFER_SIZE      = 8192;

    private static final char SEP = File.separatorChar;

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static String getLegalSegment(String segment) {
        int st = -1, end = -1;
        char[] charArray = segment.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (c != SEP) {
                if (st == -1) {
                    st = i;
                }
                end = i;
            }
        }
        if (st >= 0 && end >= st) {
            return segment.substring(st, end + 1);
        }
        throw new IllegalArgumentException("segment of <" + segment + "> is illegal");
    }

    public static String join(String parent, String child) {
        if (TextUtils.isEmpty(child)) return parent;
        if (parent == null) {
            parent = "";
        }
        int len = parent.length();
        String legalSegment = getLegalSegment(child);
        String newPath;
        if (len == 0) {
            newPath = SEP + legalSegment;
        } else if (parent.charAt(len - 1) == SEP) {
            newPath = parent + legalSegment;
        } else {
            newPath = parent + SEP + legalSegment;
        }
        return newPath;
    }

    /**
     * Input stream to output stream.
     */
    public static ByteArrayOutputStream input2OutputStream(final InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
                os.write(b, 0, len);
            }
            return os;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Input stream to string.
     */
    public static String inputStream2String(final InputStream is, final String charsetName) {
        if (is == null) return "";
        try {
            ByteArrayOutputStream baos = input2OutputStream(is);
            if (baos == null) return "";
            return baos.toString(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
