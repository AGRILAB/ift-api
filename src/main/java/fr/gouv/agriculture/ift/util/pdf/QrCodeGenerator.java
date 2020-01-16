package fr.gouv.agriculture.ift.util.pdf;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QrCodeGenerator {

    public static byte[] generateQrCode(String url) {
        try {
            final String data = url;
            final int size = 400;

            // encode
            final BitMatrix bitMatrix = generateMatrix(data, size);

            // write in a file
            return writeImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BitMatrix generateMatrix(final String data, final int size) throws WriterException {
        final BitMatrix bitMatrix = new QRCodeWriter()
                .encode(data,
                        BarcodeFormat.QR_CODE,
                        size,
                        size);
        return bitMatrix;
    }

    private static byte[] writeImage(final BitMatrix bitMatrix) throws IOException {
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        return os.toByteArray();
    }
}
