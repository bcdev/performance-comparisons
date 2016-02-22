package com.bc.perfcomp.ex2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class TestGapFiller {
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(args[0]));
        double[] doubles = new double[image.getWidth() * image.getHeight()];
        byte[] byteData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < byteData.length; i++) {
            int v = byteData[i] & 0xff;
            if (v == 0) {
                doubles[i] = Double.NaN;
            } else {
                doubles[i] = v;
            }
        }

        double[] fillGaps = GapFiller.fillGaps(image.getWidth(), image.getHeight(), doubles);
        for (int i = 0; i < fillGaps.length; i++) {
            byteData[i] = (byte) fillGaps[i];
        }

        ImageIO.write(image, "PNG", new File("output.png"));
    }
}
