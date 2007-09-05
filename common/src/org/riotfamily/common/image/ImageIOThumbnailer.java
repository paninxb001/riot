/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.util.Assert;

/**
 * Thumbnailer that uses the Java ImageIO API.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ImageIOThumbnailer implements Thumbnailer {
	
    static {
        System.setProperty("java.awt.headless", "true");
    }
    
    private static final String FORMAT_JPG = "jpg";
    
    private static final String FORMAT_PNG = "png";
    
	private int maxCrop = 0; 
		
	public void setMaxCrop(int maxCrop) {
		this.maxCrop = maxCrop;
	}

	public void renderThumbnail(File source, File dest, int width, int height)
			throws IOException {
		
		BufferedImage originalImage = readImage(new FileInputStream(source));
        boolean alpha = originalImage.getColorModel().hasAlpha();
        
        int imageWidth = originalImage.getWidth(null);
        int imageHeight = originalImage.getHeight(null);
        
        int thumbWidth;
        int thumbHeight;
        
        double scaleX = (double) width / (double) imageWidth; 
        double scaleY = (double) height / (double) imageHeight;
        
        double scale = Math.min(Math.max(scaleX, scaleY), 1);
		thumbWidth = (int) (imageWidth * scale);
    	thumbHeight = (int) (imageHeight * scale);
    	double cropFactor = (double) maxCrop / 100;

    	if ((thumbWidth - width > cropFactor * thumbWidth) 
    			|| (thumbHeight - height > cropFactor * thumbHeight)) {
    		
    		scaleX = width / (imageWidth - cropFactor * imageWidth);
    		scaleY = height / (imageHeight - cropFactor * imageHeight);
    		scale = Math.min(Math.min(scaleX, scaleY), 1);
    	}
	
        thumbWidth = (int) (imageWidth * scale);
	    thumbHeight = (int) (imageHeight * scale);
        
        BufferedImage thumbImage = new BufferedImage(thumbWidth, 
                thumbHeight, alpha 
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        graphics2D.drawImage(originalImage, 0, 0, thumbWidth, thumbHeight, null);
        
        int x = Math.max((thumbWidth - width) / 2, 0);
        int y = Math.max((thumbHeight - height) / 2, 0);
        if (x > 0 || y > 0) {
        	thumbWidth = Math.min(thumbWidth, width);
        	thumbHeight = Math.min(thumbHeight, height);
        	thumbImage = thumbImage.getSubimage(x, y, thumbWidth, thumbHeight);
        }
        
        writeImage(thumbImage, dest);
	}

	private BufferedImage readImage(InputStream in) throws IOException {
    	try {
    		return ImageIO.read(in);
    	}
    	finally {
    		try {
    			in.close();
    		}
    		catch (Exception e) {
    		}
    	}
    }
    
    private void writeImage(RenderedImage im, File dest) throws IOException {
    	ImageWriter writer = null;
        ImageOutputStream ios = null;
        try {
        	String formatName = FormatUtils.getExtension(dest.getName()).toLowerCase();
        	if (!formatName.equals(FORMAT_JPG) && !formatName.equals(FORMAT_PNG)) {
        		formatName = FORMAT_JPG;
        	}
	        Iterator it = ImageIO.getImageWritersByFormatName(formatName);
	        if (it.hasNext()) {
	            writer = (ImageWriter) it.next();
	        }
	        Assert.notNull(writer, "No ImageWriter available for format " 
	        		+ formatName);
	        
	        ios = ImageIO.createImageOutputStream(dest);
	        writer.setOutput(ios);
	        
	        ImageWriteParam iwparam = null;
	        
	        if (formatName.equals(FORMAT_JPG)) {
		        iwparam = new JPEGImageWriteParam(Locale.getDefault());
		        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		        iwparam.setCompressionQuality(1);
	        }
	        
	        writer.write(null, new IIOImage(im, null, null), iwparam);
	        ios.flush();
        }
        finally {
        	if (writer != null) {
        		writer.dispose();
        	}
        	if (ios != null) {
        		ios.close();
        	}
        }
    }

}