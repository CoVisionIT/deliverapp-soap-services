package be.deliverapp.despatchadvice;

import be.covisionit.deliverapp.proto.DespatchAdvice;
import be.deliverapp.despatchadvice.mapper.DespatchAdviceMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Endpoint
public class DespatchAdviceEndpoint {
    private static final String NAMESPACE_URI = "http://deliverapp.be/despatchadvice";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getQRBinaryContentRequest")
    @ResponsePayload
    public GetQRBinaryContentResponse getQRBinaryContent(@RequestPayload GetQRBinaryContentRequest request) {
        GetQRBinaryContentResponse response = new GetQRBinaryContentResponse();

        DespatchAdvice despatchAdvice = DespatchAdviceMapper.INSTANCE.map(request.getDespatchAdvice());
        byte[] bytes = despatchAdvice.toByteArray();

        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02X", aByte));
        }

        response.setHex(sb.toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getQRImageRequest")
    @ResponsePayload
    public GetQRImageResponse getQRImage(@RequestPayload GetQRImageRequest request) throws WriterException, IOException {
        GetQRImageResponse response = new GetQRImageResponse();

        DespatchAdvice despatchAdvice = DespatchAdviceMapper.INSTANCE.map(request.getDespatchAdvice());
        byte[] bytes = despatchAdvice.toByteArray();

        String qrString = new String(bytes, StandardCharsets.ISO_8859_1);

        int size = request.size == 0 ? 300 : request.size;

        BufferedImage img = toImage(qrString, size);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", os);

        byte[] base64Bytes = Base64.getEncoder().encode(os.toByteArray());
        String base64String = new String(base64Bytes, StandardCharsets.UTF_8);

        response.setBase64(base64String);
        return response;
    }

    private BufferedImage toImage(String qrString, int size) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.ISO_8859_1);
        BitMatrix bitMatrix = writer.encode(qrString, BarcodeFormat.QR_CODE, size, size, hints);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_USHORT_565_RGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Color color = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

}
