package com.bc.perfcomp.ex2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Norman Fomferra
 */
public class TestResizer {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (!(args.length == 2 || args.length == 3)) {
            throw new IllegalArgumentException("Usage: TestResizer <image-file> <factor> | <width> <height>");
        }
        File imageFile = new File(args[0]);
        double f = 0;
        int w = 0, h = 0;
        if (args.length == 2) {
             f = Double.parseDouble(args[1]);
        } else {
             w = Integer.parseInt(args[1]);
             h = Integer.parseInt(args[2]);
        }

        BufferedImage image = ImageIO.read(imageFile);
        if (f > 0) {
            w = (int)(f * image.getWidth());
            h = (int)(f * image.getHeight());
        }

        double[] doubleData = new double[image.getWidth() * image.getHeight()];
        byte[] byteData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int vMin = Integer.MAX_VALUE;
        int vMax = Integer.MIN_VALUE;
        for (int i = 0; i < byteData.length; i++) {
            int v = byteData[i] & 0xff;
            if (v == 0) {
                doubleData[i] = Double.NaN;
            } else {
                doubleData[i] = v;
                vMin = Math.min(vMin, v);
                vMax = Math.max(vMax, v);
            }
        }
        System.out.printf("vMin=%d, vMax=%d\n",  vMin, vMax);

        double[] resized = Resizer.resize(image.getWidth(), image.getHeight(), doubleData, w, h);
        BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        byteData = ((DataBufferByte) resizedImage.getRaster().getDataBuffer()).getData();
        int bMin = Integer.MAX_VALUE;
        int bMax = Integer.MIN_VALUE;
        for (int i = 0; i < byteData.length; i++) {
            int v = (int) resized[i];
            byte b = (byte) v;
            byteData[i] = b;

            bMin = Math.min(bMin, b & 0xff);
            bMax = Math.max(bMax, b & 0xff);
        }

        ImageIO.write(resizedImage, "PNG", new File(String.format("resize-%dx%d.png", w, h)));

        System.out.printf("bMin=%d, bMax=%d\n",  bMin, bMax);
    }
}
