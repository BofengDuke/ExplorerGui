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
	 * ��Ŀ¼srcFile �µ������ļ�Ŀ¼������ѹ�����ټ��ܣ�Ȼ�󱣴�Ϊdestfile
	 * @param srcFile
	 * @param destfile
	 * @param keyStr	��Կ
	 * @throws Exception
	 */
	public void encryptZip(String srcFile,String destfile,String keyStr) throws Exception{
		File temp = new File(UUID.randomUUID().toString() + ".zip");
		temp.deleteOnExit();
		// ѹ���ļ�
		new ZipUtil().zip(srcFile, temp.getAbsolutePath());
		// ���ļ�����
		new CipherUtil().encrypt(temp.getAbsolutePath(), destfile,keyStr);
		temp.delete();
	}
	
	
	/**
	 * ���ļ�srcfile�����Ƚ��ܺ��ѹ����Ȼ���ѹ����Ŀ¼destfile��
	 * @param srcfile
	 * @param destfile
	 * @param keyStr
	 * @throws Exception
	 */
	public void decryptUnzip(String srcfile,String destfile,String keyStr) throws Exception{
		File temp = new File(UUID.randomUUID().toString()+".zip");
		temp.deleteOnExit();
		// ���ļ�����
		new CipherUtil().decrypt(srcfile, temp.getAbsolutePath(), keyStr);
		// ��ѹ��
		new ZipUtil().unzip(temp.getAbsolutePath(), destfile);
		temp.delete();
	}
	
	public static void main(String[] args) throws Exception{
		// ����
		new ZipCipherUtil().encryptZip("d:\\test\\1.jpg", "d:\\test\\photo.zip", "12345");
		// ����
		new ZipCipherUtil().decryptUnzip("d:\\test\\photo.zip", "d:\\test\\11.jpg", "12345");
	}
	
}
