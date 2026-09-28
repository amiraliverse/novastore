package com.app.novastore.util;

import com.app.novastore.storage.manager.ObjectFileEntityType;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public final class ImageCompressorUtil {

    public static File compressImage(File inputFile, ObjectFileEntityType entityType) throws IOException {
        BufferedImage original = null;
        if (UserFileUtils.getFileExtension(inputFile.getAbsolutePath()).equalsIgnoreCase("pdf")) {
            original = convertPdfToBufferedImage(inputFile);
        } else {
            original = ImageIO.read(inputFile);
        }

        if (original == null) {
            throw new IOException("Cannot read image: " + inputFile.getAbsolutePath());
        }

        BufferedImage resized = resizeImage(original, entityType.getMaxWidth(), entityType.getMaxHeight());

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No WebP writer found (check if TwelveMonkeys WebP plugin is loaded)");
        }

        ImageWriter writer = writers.next();

        File outputFile = File.createTempFile("compressed_", ".webp");
        float quality = entityType.getQuality();

        for (int i = 0; i < 5; i++) {
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionType(param.getCompressionTypes()[0]);
                    param.setCompressionQuality(quality);
                }

                writer.write(null, new IIOImage(resized, null, null), param);
            }

            long sizeKB = outputFile.length() / 1024;
            if (sizeKB <= entityType.getTargetKB() || quality <= 0.4f) {
                break;
            }
            quality -= 0.1f;
        }

        writer.dispose();
        return outputFile;
    }

    private static BufferedImage convertPdfToBufferedImage(File pdfFile) throws IOException {
//        try (PDDocument document = PDDocument.load(pdfFile)) {
//            PDFRenderer pdfRenderer = new PDFRenderer(document);
//            return pdfRenderer.renderImageWithDPI(0, 300);
//        }
//        throw new NotImplementedYetException("pdf files not supported");
        return null;
    }

    private static BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();

        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
        if (scale >= 1.0) return original;

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        Image scaled = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();

        return resized;
    }

}
