package playRGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class imageReader{
	public BufferedImage[] imgSrc;
	
	public imageReader(String args){
		// Input images are fixed size
		int width = 352;//Integer.parseInt(args[1]);
		int height = 288;//Integer.parseInt(args[2]);
		
		try {
			// need to regenerate file name
			// the current folder has one .wav file and .rgb file's'
			int numImage = new File(new File(args).getParent()).list().length - 1;
			imgSrc = new BufferedImage[numImage];
			for(int i = 0; i < numImage; ++i)
			{
				// regenerate file name
				// fileName example : Sample.rgb -> Sample001.rgb
				String fileName = args.split("\\.")[0] + String.format("%03d", i + 1) + ".rgb";
				
				File file = new File(fileName);				
				@SuppressWarnings("resource")
				InputStream is = new FileInputStream(file);
	
				long len = file.length();
				byte[] bytes = new byte[(int)len];
	
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}
	
				imgSrc[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);				
				int ind = 0;
				for(int y = 0; y < height; y++){	
					for(int x = 0; x < width; x++){
	
						//byte a = 0;
						byte r = bytes[ind];
						byte g = bytes[ind+height*width];
						byte b = bytes[ind+height*width*2]; 
	
						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
						imgSrc[i].setRGB(x,y,pix);
						ind++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
