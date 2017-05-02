package zipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ���ļ����ļ��н���ѹ���ͽ�ѹ
 * @author Duke
 * 
 * ���ڸıࣺ http://blog.csdn.net/hualizide/article/details/8206841
 *
 */

public class ZipUtil {
	
	static final int BUFFER = 10240;
	
	private void directoryZip(ZipOutputStream out,File f,String base) throws IOException{
		if(f.isDirectory()){
			File[] files = f.listFiles();
			// ����ѹ������Ŀ¼
			out.putNextEntry(new ZipEntry(base+"/"));
			if(base.length() == 0){
				base = "";
			}else{
				base = base + "/";
			}
			for(int i=0;i<files.length;i++){
				directoryZip(out,files[i],base+files[i].getName());
			}
		}else{
			// ��ѹ���ļ����뵽zip��
			out.putNextEntry(new ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			byte[] byteRead = new byte[BUFFER];
			int buff = 0;
			while((buff = in.read(byteRead))!= -1){
				out.write(byteRead,0,buff);
			}
			in.close();
		}
	}
	
	// ѹ���ļ�
	private void fileZip(ZipOutputStream zos,File file) throws IOException{
		if(file.isFile()){
			zos.putNextEntry(new ZipEntry(file.getName()));
			FileInputStream in = new FileInputStream(file);
			byte[] byteRead = new byte[BUFFER];
			int buff = 0;
			while((buff = in.read(byteRead)) != -1){
				zos.write(byteRead,0,buff);
			}
			in.close();
		}else{
			directoryZip(zos,file,"");
		}
	}
	
	// ��ѹ���ļ�
	private void fileUnZip(ZipInputStream zis,File file) throws IOException{
		ZipEntry zip = zis.getNextEntry();
		if(zip == null){
			return;
		}
		String name = zip.getName();
		File f = new File(file.getAbsolutePath()+ "/"+name);
		if(zip.isDirectory()){
			f.mkdirs();
			fileUnZip(zis,file);
		}else{
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			byte[] byteRead = new byte[BUFFER];
			int buff = 0;
			while((buff = zis.read(byteRead))!=-1){
				fos.write(byteRead, 0, buff);
			}
			fos.close();
			fileUnZip(zis,file);
		}
	}
	
	// ����filePath ������Ӧ��Ŀ¼
	private File mkdirFiles(String filePath) throws IOException{
		File file = new File(filePath);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		file.createNewFile();
		return file;
	}
	
	// ��zipBeforeFileĿ¼�µ��ļ�ѹ��������Ϊָ�����ļ�zipAfterFile
	public void zip(String zipBeforeFile,String zipAfterFile){
		try{
			FileOutputStream out = new FileOutputStream(mkdirFiles(zipAfterFile));
			ZipOutputStream zos = new ZipOutputStream(out);
			fileZip(zos,new File(zipBeforeFile));
			zos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// ��ѹ���ļ� unZipBeforeFile ������unZipAfterFileĿ¼��
	public void unzip(String unZipBeforeFile,String unZipAfterFile){
		try{
			ZipInputStream zis = new ZipInputStream(new FileInputStream(unZipBeforeFile));
			File f = new File(unZipAfterFile);
			f.mkdirs();
			fileUnZip(zis,f);
			zis.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
