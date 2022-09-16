package com.cjf.draw.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 画图公用方法
 *
 * @author chenjf
 * @since 2022-08-04
 */
public class DrawUtil {
	
	 

	public static void drawImageFit(Graphics2D g2d, int x, int y, float width, float height, BufferedImage original) {

		int originalH = original.getHeight();
		int originalW = original.getWidth();

		float origialRatio = (float) originalW / (float) originalH;

		float targetRatio = width / height;
		boolean isWideImage = origialRatio >= targetRatio;

		float zoomFactor = isWideImage ? (height / originalH) : width / originalW;

		int newWidth = (int) (originalW * zoomFactor);
		int newHeight = (int) (originalH * zoomFactor);
		if (isWideImage) {
			int dx = (int) ((newWidth - width) / 2);
			int cutX= (int)(dx/zoomFactor);
			original = original.getSubimage(cutX, 0, originalW-2*cutX, originalH);
			g2d.drawImage(original, x, y, (int) width ,(int) height, null);
		} else {
			int dy = (int) ((newHeight - height) / 2);
			int cutY = (int) (dy / zoomFactor);
			// 这里是取了长图的中间一段
			original = original.getSubimage(0, cutY, originalW, originalH - 2*cutY);			
			g2d.drawImage(original, x, y, (int) width, (int) height, null);
		}

	}

	/**
	 * 靠左对齐画多行文字，如果文字过长，截取...
	 * 
	 * @param g2d
	 * @param lines  表示行起点终点的信息
	 * @param content
	 * @param x 左起x
	 * @param y 左起y
	 * @param lineDistance 行间距
	 * @return  返回左起的x和右边最大的x和最下的y坐标
	 */
	public static Triple<Integer, Integer,Integer> drawMultiLineText(Graphics2D g2d, List<Pair<Integer, Integer>> lines, String content, int x,
																	 int y, int lineDistance) {
		
		int ellipsisWidth =(int) g2d.getFontMetrics().getStringBounds("...", g2d).getWidth();
		int endX=0;
		
		for (int i = 0; i < lines.size(); i++) {

			Pair<Integer, Integer> line = lines.get(i);

			
			String textToDraw = content.substring(line.getLeft(), line.getRight());
			 
			if (line.getRight() < content.length() && i == lines.size() - 1) {
				//按照省略号切一下
				textToDraw = getEndLineCutted(g2d,textToDraw,ellipsisWidth)+"...";
			 
			}
			g2d.drawString(textToDraw, x, y);
			
			int rightX = (int)g2d.getFontMetrics().getStringBounds(textToDraw, g2d).getWidth() + x;
			if(rightX>endX) {
				endX=rightX;
			}
			
			int fsize = g2d.getFont().getSize();
			y = y + fsize + 2 + lineDistance;
		}
		return Triple.of(x, endX,y);
	}

	private static String getEndLineCutted(Graphics2D g2d,String textToDraw, int ellipsisWidth) {
		for(int i=textToDraw.length()-1;i>=0;i--) {
			String lastPart = textToDraw.substring(i, textToDraw.length());
			double width = g2d.getFontMetrics().getStringBounds(lastPart, g2d).getWidth();
			if(width>=ellipsisWidth) {
				return textToDraw.substring(0,i);
			}
			
		}
		return textToDraw;
	}

	public static void applyQualityRenderingHints(Graphics2D g2d) {

		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

	}

	/**
	 * 旋转图片
	 * @param image
	 * @param width
	 * @param height
	 * @param angle 旋转角度
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage rotate(BufferedImage image, int width, int height, double angle) throws IOException{
		return Thumbnails.of(image)
				.sourceRegion(Positions.CENTER, width,height) // 裁剪
				.size(width, height)
				.rotate(angle)
				.imageType(BufferedImage.TYPE_INT_ARGB) // 去除黑底
				.outputQuality(1)
				.asBufferedImage();
	}

	/**
	 * 裁剪图片
	 * @param oriImage 原始图片
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage clipRound(BufferedImage oriImage) throws IOException {
		int height = oriImage.getHeight();
		int width = oriImage.getWidth();
		int radius = height >= width ? width:height;
		// 透明底的图片
		BufferedImage roundImage = new BufferedImage(radius, radius, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = roundImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//图片是一个圆型
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, radius, radius);
		//需要保留的区域
		graphics.setClip(shape);
		graphics.drawImage(oriImage, 0, 0, radius, radius, null);

		return roundImage;
	}

	/**
	 * 按照尺寸缩放
	 * @return
	 */
	public static BufferedImage size(BufferedImage image, int width, int height) throws IOException {
		return Thumbnails.of(image)
				.size(width, height)
				.outputQuality(1)
				.asBufferedImage();
	}

	public static void drawQRCode(Graphics2D g2d, BufferedImage qrCode, int bottomPartTopY) {
		int x = 66;
		int y = bottomPartTopY + 1370 - 177;
		g2d.drawImage(qrCode, x, y, 192, 192, null);
	}

}