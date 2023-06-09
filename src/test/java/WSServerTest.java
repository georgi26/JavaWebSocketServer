import com.biolabi.ws.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.net.Socket;

public class WSServerTest {

    final String REQUEST = """
                GET /test HTTP/1.1
                Host: localhost:8080
                Connection: Upgrade
                Pragma: no-cache
                Cache-Control: no-cache
                User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36
                Upgrade: websocket
                Origin: chrome-extension://fgponpodhbmadfljofbimhhlengambbn
                Sec-WebSocket-Version: 13
                Accept-Encoding: gzip, deflate, br
                Accept-Language: en-US,en;q=0.9,bg;q=0.8
                Sec-WebSocket-Key: amu6FMQZpCK92miwugZUGg==
                Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits
            """;

    @Test
    void testScanner() throws WSException {
        InputStream is = new ByteArrayInputStream(REQUEST.getBytes());
        WSConnection wsServer = new WSConnection(new Socket());
        assertEquals("amu6FMQZpCK92miwugZUGg==", wsServer.readSecWebSocketKey(is), "Failed to decode Sec-Web-Socket-Key");
    }

    @Test
    void testDecodeFirst() throws WSException {
        WSDecoder decoder = new WSDecoder(new int[]{129, 131, 45, 15, 252, 34, 106, 72, 152});
        Opcode opcode = decoder.getOpcode();
        assertEquals(Opcode.TEXT, opcode);
        assertEquals(3, decoder.getLength());
        assertEquals(1, decoder.getMask());
        int[] data = decoder.getBinaryData();
        assertEquals(71, data[0]);
        assertEquals("GGd", decoder.getText());
        decoder = new WSDecoder(new int[]{136, 128, 142, 232, 6, 234});
        assertEquals(Opcode.CLOSE, decoder.getOpcode());
    }

    @Test
    void testEncodeFrame() throws WSException {
        WSEncoder wsEncoder = new WSEncoder();
        int firstText = wsEncoder.createFirstTextOpcodeByte();
        assertEquals(129, firstText);
        byte[] result = wsEncoder.encodeText("Test Me!");
        int mlByte = wsEncoder.createMaskAndLengthByte("Test Me!");
        assertEquals(8, mlByte);
        WSDecoder wsDecoder = new WSDecoder(result);
        assertEquals(0, wsDecoder.getMask());
        assertEquals("Test Me!", wsDecoder.getText());
    }
}
