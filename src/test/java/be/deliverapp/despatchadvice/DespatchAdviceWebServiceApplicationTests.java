package be.deliverapp.despatchadvice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DespatchAdviceWebServiceApplicationTests {

	private final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

	@LocalServerPort
	private int port = 0;

	@BeforeEach
	public void init() throws Exception {
		marshaller.setPackagesToScan(ClassUtils.getPackageName(GetQRBinaryContentRequest.class));
		marshaller.setPackagesToScan(ClassUtils.getPackageName(GetQRImageRequest.class));
		marshaller.afterPropertiesSet();
	}

	@Test
	public void testGetQRBinaryContentRequest() {
		WebServiceTemplate ws = new WebServiceTemplate(marshaller);
		GetQRBinaryContentRequest request = new GetQRBinaryContentRequest();
		request.setDespatchAdvice(TestData.INSTANCE.buildTestDespatchAdvice());

		GetQRBinaryContentResponse received =
				(GetQRBinaryContentResponse) ws.marshalSendAndReceive("http://localhost:" + port + "/ws", request);
		assertThat(!StringUtils.isEmpty(received.hex));
	}

	@Test
	public void testGetQRImageRequest() throws IOException {
		WebServiceTemplate ws = new WebServiceTemplate(marshaller);
		GetQRImageRequest request = new GetQRImageRequest();
		request.setDespatchAdvice(TestData.INSTANCE.buildTestDespatchAdvice());
		int size = 200;
		request.setSize(size);

		GetQRImageResponse received =
				(GetQRImageResponse) ws.marshalSendAndReceive("http://localhost:" + port + "/ws", request);

		assertThat(!StringUtils.isEmpty(received.base64));

		// can we read the image?
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(received.base64)));

		assertThat(image.getHeight() == size);
		assertThat(image.getWidth() == size);
	}

}
