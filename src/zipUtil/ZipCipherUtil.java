package zipUtil;

import java.io.File;
import java.util.UUID;

/**
 * 
 * @author 
 *	
 */

public class ZipCipherUtil {
	
	/**
	 * 对目录srcFile 下的所有文件目录进行先压缩后再加密，然后保存为destfile
	 * @param srcFile
	 * @param destfile
	 * @param keyStr	密钥
	 * @throws Exception
	 */
	public void encryptZip(String srcFile,String destfile,String keyStr) throws Exception{
		File temp = new File(UUID.randomUUID().toString() + ".zip");
		temp.deleteOnExit();
		// 压缩文件
		new ZipUtil().zip(srcFile, temp.getAbsolutePath());
		// 对文件加密
		new CipherUtil().encrypt(temp.getAbsolutePath(), destfile,keyStr);
		temp.delete();
	}
	
	
	/**
	 * 对文件srcfile进行先解密后解压缩，然后解压缩到目录destfile下
	 * @param srcfile
	 * @param destfile
	 * @param keyStr
	 * @throws Exception
	 */
	public void decryptUnzip(String srcfile,String destfile,String keyStr) throws Exception{
		File temp = new File(UUID.randomUUID().toString()+".zip");
		temp.deleteOnExit();
		// 对文件解密
		new CipherUtil().decrypt(srcfile, temp.getAbsolutePath(), keyStr);
		// 加压缩
		new ZipUtil().unzip(temp.getAbsolutePath(), destfile);
		temp.delete();
	}
	
	public static void main(String[] args) throws Exception{
		// 加密
		new ZipCipherUtil().encryptZip("d:\\test\\1.jpg", "d:\\test\\photo.zip", "12345");
		// 解密
		new ZipCipherUtil().decryptUnzip("d:\\test\\photo.zip", "d:\\test\\11.jpg", "12345");
	}
	
}
