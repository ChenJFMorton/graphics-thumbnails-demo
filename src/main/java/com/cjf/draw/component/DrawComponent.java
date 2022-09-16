package com.cjf.draw.component;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 *
 * @author chenjf
 * @since 2022-08-04
 */
public abstract class DrawComponent {

	// https://blog.csdn.net/comeonyangzi/article/details/54913878 透明
	public BufferedImage draw() {
		BufferedImage bi=new BufferedImage(getWidth()	, getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		bi = g2d.getDeviceConfiguration().createCompatibleImage(getWidth()	, getHeight(),Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = bi.createGraphics();
		paintComponent(g2d);
		bi = afterPaintComplete(g2d, bi);
		g2d.dispose();
		return bi;
	}

	public abstract int getHeight() ;
	
	public abstract int getWidth();
	
	/**
	 * 子类覆盖这个方法做一些返回前的处理
	 * @param g2d
	 * @param image
	 */
	protected BufferedImage afterPaintComplete(Graphics2D g2d, BufferedImage image) {
		return image;
	}
	
	
	protected abstract void paintComponent(Graphics2D g2d);


	public String cutString(String text,int limit) {
		if(StringUtils.isEmpty(text)) {
			return "";
		}
		return text.length()>limit? text.substring(0,limit): text;
	}

}
