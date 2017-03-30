package com.geosde.filemanager.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtils {

	/**
	 * Write byte array to file
	 */
	public static void toFile(byte[] bytes, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Write a list of byte array to a file.
	 */
	public static void toFile(List<byte[]> list, String path, String filename) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		byte[] buffer = null;
		for (int i = 0; i < list.size(); i++) {
			byte[] b = list.get(i);
			bos.write(b, 0, b.length);
		}
		buffer = bos.toByteArray();
		toFile(buffer, path, filename);
	}

	/**
	 * Transform a file to a list of byte array.
	 */
	public static List<byte[]> split(String file_path, int buffer) {
		List<byte[]> list = null;
		FileInputStream fis = null;
		ByteArrayOutputStream bos = null;
		File source_file = new File(file_path);
		try {
			list = new ArrayList<>();
			fis = new FileInputStream(source_file);
			bos = new ByteArrayOutputStream(buffer);
			byte[] b = new byte[buffer * 1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				byte[] temp = new byte[n];
				System.arraycopy(b, 0, temp, 0, n);
				list.add(temp);
			}
			fis.close();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<byte[]>  split(byte[] file, int buffer) {
		List<byte[]> list = null;
		ByteArrayInputStream is = new ByteArrayInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer);
		String uuid = UUID.randomUUID().toString();
		String filename = "";
		long size = 0;
		String extName = "";
		byte[] b = new byte[buffer * 1024];
		int n;
		int count = 0;
		list = new ArrayList<>();
		size = 0;
		try {
			while ((n = is.read(b)) != -1) {
				size += n;
				byte[] temp = new byte[n];
				System.arraycopy(b, 0, temp, 0, n);
				list.add(temp);
				count++;
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}


	public static long getFileSizes(File f) throws Exception {// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

}
