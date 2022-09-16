package com.cjf.draw.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 *
 * @author chenjf
 * @since 2022-08-04
 */
@Slf4j
public class MyImageIO {

	public static BufferedImage readFromPath(String path) {
		try {
			InputStream input = MyImageIO.class.getClassLoader().getResourceAsStream(path);
			BufferedImage read = ImageIO.read(input);
			return read;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage readFromByteArray (byte[] i) {
		try {
			return ImageIO.read(new ByteArrayInputStream(i));
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * 带有超时的readFromURL. NOS是支持keep-alive的，所以这里不需要额外设置，java的默认连接池的大小是5.
	 * 如果没有读到 内部会重试一次
	 * 
	 * @param u
	 * @return
	 */
	public static BufferedImage readFromURL(String u) {
		if(StringUtils.isEmpty(u)) {
			return null;
		}
		
		BufferedImage bi = doReadFromUrl(u);
		if (bi != null) {
			return bi;

		}
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// ignore
		}
		return doReadFromUrl(u);

	}

	private static BufferedImage doReadFromUrl(String u) {
		
		HttpURLConnection con = null;
		InputStream is = null;
		try {
			URL url = new URL(u);

			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000);
			con.setReadTimeout(3000);
			is = con.getInputStream();
			BufferedImage img = ImageIO.read(is);
			if (img != null) {
				return img;
			} else {
				return null;
			}
		} catch (IOException ex) {
			log.warn("图片" + u + "加载失败", ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// handle close failure
				}
			}

			if (con != null) {
				con.disconnect();
			}
		}

		return null;
	}

}