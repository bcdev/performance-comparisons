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
public class TestGapFiller {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: TestGapFiller <image-file> <method-name> <valid-count-min>");
        }
        File imageFile = new File(args[0]);
        String methodName = args[1];
        int validCountMin = Integer.parseInt(args[2]);

        BufferedImage image = ImageIO.read(imageFile);
        double[] doubleData = new double[image.getWidth() * image.getHeight()];
        byte[] byteData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < byteData.length; i++) {
            int v = byteData[i] & 0xff;
            if (v == 0) {
                doubleData[i] = Double.NaN;
            } else {
                doubleData[i] = v;
            }
        }

        Method method = GapFiller.class.getMethod(methodName, Integer.TYPE, Integer.TYPE, double[].class, Integer.TYPE);
        double[] fillGaps = (double[]) method.invoke(null, image.getWidth(), image.getHeight(), doubleData, validCountMin);
        for (int i = 0; i < fillGaps.length; i++) {
            byteData[i] = (byte) fillGaps[i];
        }

        ImageIO.write(image, "PNG", new File(String.format("%s-%d.png", methodName, validCountMin)));
    }
}
