package syamwu.xchushi.fw.common.util;

import java.io.File;
import java.io.IOException;

import syamwu.xchushi.fw.common.Asset;

public class FileUtil {

    /**
     * 创建文件，若文件已经存在则不创建，返回false；若父文件夹不存在则创建
     * 
     * @param file
     * @return
     * @throws IOException
     * @author syam_wu
     */
    public static boolean createFile(File file) throws IOException {
        Asset.notNull(file);
        if (file.exists()) {
            return false;
        }
        if (file.getAbsolutePath().endsWith(File.separator)) {
            return false;
        }
        // 判断目标文件所在的目录是否存在
        File parent = file.getParentFile();
        while (!parent.exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!parent.mkdirs()) {
                return false;
            }
            parent = parent.getParentFile();
        }
        // 创建目标文件
        return file.createNewFile();
    }

    public static boolean createFile(String destFileName) throws IOException {
        return createFile(new File(destFileName));
    }

}
