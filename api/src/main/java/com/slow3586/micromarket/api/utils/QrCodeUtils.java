package com.slow3586.micromarket.api.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class QrCodeUtils {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();

    public byte[] generateQRCodeImage(String barcodeText) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(
                MatrixToImageWriter.toBufferedImage(
                    qrCodeWriter.encode(
                        barcodeText,
                        BarcodeFormat.QR_CODE,
                        200,
                        200)),
                "png",
                outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
