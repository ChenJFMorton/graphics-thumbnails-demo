package com.cjf.draw.component;


import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.cjf.draw.bo.ShareContext;
import com.cjf.draw.util.DrawUtil;
import com.cjf.draw.util.MyImageIO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 分享图
 *
 * @author chenjf
 * @date 2022/8/31 10:26
 */
@Slf4j
public class ShareComponent extends DrawComponent {

	// 主图长宽
	private final int FULL_HEIGHT = 1554;
	private final int FULL_WIDTH = 750;

	private byte[] wxCode;
	private ShareContext ctx;

	public ShareComponent(ShareContext ctx, byte[] wxCode) {
		this.ctx = ctx;
		this.wxCode = wxCode;
	}

	@Override
	public void paintComponent(Graphics2D g2d) {

		try {

			// 分享图质量
			DrawUtil.applyQualityRenderingHints(g2d);

			// 设置背景图片
			this.drawBgImage(g2d, ctx.getBgImgPath());

			// 设置画作
			this.drawPaintImage(g2d);

			// 设置二维码
			ByteArrayInputStream bais = new ByteArrayInputStream(wxCode);
			BufferedImage read = ImageIO.read(bais);
			DrawUtil.drawQRCode(g2d, read, 0);

		} catch (Exception e) {
			log.error("绘制分享图片时失败", e);
		}

	}
	
	public void drawBgImage(Graphics2D g2d, String bgImgPath) {
	
		BufferedImage toDraw = MyImageIO.readFromPath(bgImgPath);
		if (toDraw == null) {
			log.warn("主图获取失败(超时)+ctx.getMainPicUrl()");
			return;
		}
		DrawUtil.drawImageFit(g2d, 0, 0, FULL_WIDTH, FULL_HEIGHT, toDraw);
		 

	}

	public void drawPaintImage(Graphics2D g2d) throws IOException {

		BufferedImage oriImage = MyImageIO.readFromPath("image/pig.jpg");
		if (ctx.getIsRound()) {
			// 图片圆形裁剪
			BufferedImage image = DrawUtil.clipRound(oriImage);

			// 按照尺寸缩放
			DrawUtil.size(image, 400, 400);

			DrawUtil.drawImageFit(g2d, 32, -108, 400, 400, image);
		} else {
			// 图片旋转
			BufferedImage image = DrawUtil.rotate(oriImage, 310, 420, -72.78);

			DrawUtil.drawImageFit(g2d, 304, 50, 494, 422, image);
		}

	}

	@Override
	public int getHeight() {
		return FULL_HEIGHT;
	}

	@Override
	public int getWidth() {
		return FULL_WIDTH;
	}
	
	@Override
	protected BufferedImage afterPaintComplete(Graphics2D g2d, BufferedImage image) {
		 return image.getSubimage(0, 0, FULL_WIDTH, FULL_HEIGHT);
	}

	public static void main(String[] args) throws IOException {
		// 二维码生成
		File qrCodeFile = QrCodeUtil.generate("https://chenjfmorton.github.io/", 192, 192, FileUtil.file("./qrcode.jpg"));
		byte[] wxCode = Files.readAllBytes(qrCodeFile.toPath());


		ShareContext ctx = new ShareContext();
		ctx.setIsRound(false); // 方形图片旋转还是方形图片裁剪成圆形
		ctx.setBgImgPath(ctx.getIsRound() ? "image/bg-round.png":"image/bg-rotate.png");

		DrawComponent shareComponent = new ShareComponent(ctx, wxCode);
		// 画图
		BufferedImage draw = shareComponent.draw();


		Thumbnails.of(draw)
				.size(draw.getWidth(),draw.getHeight())
				.outputQuality(1)
				.toFile("./result.png");
	}
}