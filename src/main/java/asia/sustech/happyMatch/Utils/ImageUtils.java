package asia.sustech.happyMatch.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

public class ImageUtils {
    public static void saveBase64ImageAsJpg(String base64Image, String filePath) {
        BufferedImage image = null;
        try {
            // 移除base64编码前缀部分
            String imageData = base64Image.split(",")[1];
            // 解码base64数据
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            // 将字节数组转换为图像
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(bis);
            bis.close();
        } catch (IOException e) {
            Logger.getLogger("ImageUtils").warning("图像解码失败！");
        }

        if (image != null) {
            try {
                File outputFile = new File(filePath);
                // 创建文件夹
                File parentDir = outputFile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                ImageIO.write(image, "jpg", outputFile); // 将图像写入文件
                ImageIO.write(image, "png", outputFile); // 将图像写入文件
                System.out.println("图像保存成功！");
            } catch (IOException e) {
                Logger.getLogger("ImageUtils").warning("图像保存失败！");
            }
        }
    }
}
