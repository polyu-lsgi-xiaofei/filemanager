package com.geosde.filemanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.geosde.filemanager.db.CassandraConnector;
import com.geosde.filemanager.utils.FileUtils;

public class FileManager {

	private static final long serialVersionUID = -3100028422371321159L;
	private boolean isAllowed;
	private String upFileName;
	private String workspace;

	// 定义合法后缀名的数组
	private String[] allowedExtName = new String[] { "zip", "rar", // 压缩文件
			"txt", "doc", "wps", "docx", "java", // 文本
			"xls", "xlsx", // 表格
			"ppt", "pptx", // 幻灯片
			"pdf", // pdf
			"jpg", "jpeg", "bmp", "gif", "png"// 图片
	};

	Connection dbConn;
	File uploadFile = null;
	Map<String, String> params = new HashMap<>();
	CassandraConnector connector = new CassandraConnector();

	public FileManager(String workspace) {
		this.workspace=workspace;
		init();
	}

	public void init() {
		Session session = CassandraConnector.connect();
		String create_ws = "CREATE KEYSPACE IF NOT EXISTS " + workspace + " WITH replication = {"
				+ "'class':'SimpleStrategy'," + "'replication_factor': '2'};";
		session.execute(create_ws);
		String create_file_table = "CREATE TABLE IF NOT EXISTS " + workspace
				+ ".file (id uuid,file_part_no int,data blob,PRIMARY KEY (id, file_part_no));";
		session.execute(create_file_table);
	}

	public byte[] getFile(String uuid) {
		byte[] data = null;
		try {
			Session session = CassandraConnector.connect();
			SimpleStatement statement = new SimpleStatement("SELECT * FROM " + workspace + ".file WHERE id=?",
					UUID.fromString(uuid));
			ResultSet rs = session.execute(statement);
			List<byte[]> list = new ArrayList<>(12);
			for (Row row : rs) {
				ByteBuffer buffer = row.getBytes("data");
				byte[] bytes = buffer.array();
				int number = row.getInt("file_part_no");
				list.add(number, bytes);
			}
			data = generateFile(list);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public String putFile(String file_path) {
		String uuid = UUID.randomUUID().toString();
		List<byte[]> list = FileUtils.split(file_path, 1024);
		feedFile(uuid, list);
		return uuid;

	}

	public String putFile(byte[] file) {
		String uuid = UUID.randomUUID().toString();
		List<byte[]> list = FileUtils.split(file, 1024);
		feedFile(uuid, list);
		return uuid;
	}
	
	public void close(){
		connector.close();
	}

	/*********************** Private methods *************************/

	private byte[] generateFile(List<byte[]> list) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		byte[] buffer = null;
		for (int i = 0; i < list.size(); i++) {
			byte[] b = list.get(i);
			bos.write(b, 0, b.length);
		}
		buffer = bos.toByteArray();
		return buffer;
	}

	private void feedFile(String uuid, List<byte[]> list) {
		UUID id = UUID.fromString(uuid);
		Session session = CassandraConnector.connect();
		session.execute("use " + workspace + ";");
		for (int i = 0; i < list.size(); i++) {
			session.executeAsync(new SimpleStatement("INSERT INTO file (id,file_part_no,data) VALUES (?,?,?)", id, i,
					ByteBuffer.wrap(list.get(i))));
		}
		// session.close();

	}

	private long getFileSizes(File f) throws Exception {// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	/**
	 * 为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名
	 *
	 * @param filename
	 *            原文件名
	 * @return 生成的唯一文件名
	 */
	private String makeFileName(String filename) {

		return UUID.randomUUID().toString() + "_" + filename;
	}
	
	public static void main(String[] args) {
		//File file=new File("C:\\Users\\Phil\\Desktop\\时空数据引擎工作进展汇报-2017-03-08 -xf.pptx");
		FileManager manager=new FileManager("bz");
		//manager.putFile("C:\\Users\\Phil\\Desktop\\时空数据引擎工作进展汇报-2017-03-08 -xf.pptx");
		byte[] bytes=manager.getFile("3d0d58ff-0b5c-4741-97d0-a65a6d2e4358");
		FileUtils.toFile(bytes, "C:\\Users\\Phil\\Desktop", "1.pptx");
		manager.close();
	}
}
