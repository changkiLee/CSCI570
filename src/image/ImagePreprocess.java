package image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImagePreprocess {

	static final int WIDTH = 352;
	static final int HEIGHT = 288;

	static final int IMG_WIDTH = 299;
	static final int IMG_HEIGHT = 299;
	static final String VIDEO_DIR = "resource/databse_videos/";
	static final String QUERY_DIR = "resource/query/";
	
	public static File getImgFile(String args) {
		File file = new File(args);
		return file;
	}

	public static byte[] getRGBbyte(File file) {
		long len = file.length();
		byte[] bytes = new byte[(int) len];
		try {
			InputStream is = new FileInputStream(file);
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;

	}

	public static BufferedImage showIms(byte[] bytes) {
		BufferedImage img;
		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		int ind = 0;
		for (int y = 0; y < HEIGHT; y++) {

			for (int x = 0; x < WIDTH; x++) {

				byte r = bytes[ind];
				byte g = bytes[ind + HEIGHT * WIDTH];
				byte b = bytes[ind + HEIGHT * WIDTH * 2];

				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
				img.setRGB(x, y, pix);
				ind++;
			}
		}
		return img;
	}

	private static BufferedImage resizeImage(BufferedImage originalImage, int type) throws IOException {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
	}

	public static byte[] imageRead(File im) throws IOException {
		byte[] imageInByte;
		byte[] oi = getRGBbyte(im);
		BufferedImage originalImage = showIms(oi);
		BufferedImage resizedImage = resizeImage(originalImage, BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "jpg", baos);
		baos.flush();
		imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	public static File[] getVideoImages(String label, String videoDir) throws IOException {
		File dir = new File(videoDir + label);
		File[] foundFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith(label.toLowerCase()) && name.endsWith(".rgb");
			}
		});
		return foundFiles;
	}

	public static File[] getVideoImages(String label) throws IOException {
		return getVideoImages(label, VIDEO_DIR);
	}

	public static File[] getQueryVideoImages(String label) throws IOException {
		return getVideoImages(label, QUERY_DIR);
	}

	public static List<byte[]> ImagesToBytes(File[] foundFiles) throws IOException {
		List<byte[]> images = new ArrayList<>();
		for (File file : foundFiles) {
			// System.out.println(file.getName());
			images.add(imageRead(file));
			// Process file
		}
		return images;
	}

	public static List<byte[]> ImagesToBytesWithSampling(File[] foundFiles, int rate) throws IOException {
		List<byte[]> images = new ArrayList<>();
		int index = 0;
		for (File file : foundFiles) {
			if (index % rate == 0)
				images.add(ImagePreprocess.imageRead(file));
			index++;
		}
		return images;
	}
}