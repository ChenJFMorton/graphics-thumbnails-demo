# graphics-thumbnails-demo
## 1、功能
分享海报制作图片处理：使用Java Graphics2D及thumbnails工具包对分享海报中图片进行旋转、裁剪操作

## 2、效果图一览
### 2.1、方形图片旋转效果
------------
**原图：**

<figure class="half">
    <img src="./src/main/resources/image/bg-rotate.png" width="200">
    <img src="./src/main/resources/image/11.jpg" width="200">
</figure>


**效果图：**

<img src="./src/main/resources/image/rotate-result.png" title="Logo" width="200" /> 

### 2.2、方形图片裁剪成圆图效果

**原图：**

<figure class="half">
    <img src="./src/main/resources/image/bg-round.png" width="200">
    <img src="./src/main/resources/image/11.jpg" width="200">
</figure>

**效果图：**

<img src="./src/main/resources/image/round-result.png" title="Logo" width="200" /> 

## 3、使用方法
### 3.1、环境要求
jdk1.8+

maven依赖
```
<!--web应用基本环境配置 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.4.0</version>
    </dependency>

    <!-- lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.16</version>
    </dependency>

    <!-- 图片处理工具包 -->
    <dependency>
        <groupId>net.coobird</groupId>
        <artifactId>thumbnailator</artifactId>
        <version>0.4.17</version>
    </dependency>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.6</version>
    </dependency>

    <!-- 工具包（https://hutool.cn/） -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.8.5</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.3.0</version>
    </dependency>
```

### 3.2、如何使用
```java
public static void main(String[] args) throws IOException {
		// 二维码生成
		File qrCodeFile = QrCodeUtil.generate("https://chenjfmorton.github.io/", 192, 192, FileUtil.file("./qrcode.jpg"));
		byte[] wxCode = Files.readAllBytes(qrCodeFile.toPath());


		ShareContext ctx = new ShareContext();
		ctx.setIsRound(true); // 方形图片旋转还是方形图片裁剪成圆形
		ctx.setBgImgPath(ctx.getIsRound() ? "image/bg-round.png":"image/bg-rotate.png");

		DrawComponent shareComponent = new ShareComponent(ctx, wxCode);
		// 画图
		BufferedImage draw = shareComponent.draw();


		Thumbnails.of(draw)
				.size(draw.getWidth(),draw.getHeight())
				.outputQuality(1)
				.toFile("./result.png");
	}
```
通过`com/cjf/draw/component/ShareComponent.java`文件中main来进行分享图生成，代码中的坐标、长宽根据具体要求来进行配置，达到你需要的效果！