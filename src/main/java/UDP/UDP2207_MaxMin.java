package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP2207_MaxMin {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "3ocRFDSJ";
        String host = "36.50.135.242";
        int port = 2207;
        DatagramSocket socket = null;

        try {
            // 1. Tao Socket, IP
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // 2. Gui ;studentCode;qCode
            String req = ";" + studentCode + ";" + qCode;
            byte[] a = req.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(a, a.length, ip, port);
            socket.send(p);
            System.out.println("Gui: " + req);

            // 3. Nhan requestId;a1,a2,...,a50
            byte[] b = new byte[10000];
            DatagramPacket nhan = new DatagramPacket(b, b.length);
            socket.receive(nhan);
            String data = new String(nhan.getData(), nhan.getOffset(),
                    nhan.getLength(), StandardCharsets.UTF_8);
            System.out.println("Nhan: " + data);

            // 4. Tach requestId va danh sach so
            String[] c = data.split(";", 2);
            if (c.length != 2 || c[1].trim().isEmpty()) {
                throw new Exception("Sai dinh dang Server");
            }
            String ma = c[0];
            String[] mang = c[1].split(",");

            // 5. Tim max va min (dung voi ca so am)
            int max = Integer.parseInt(mang[0].trim());
            int min = max;
            for (int i = 1; i < mang.length; i++) {
                int x = Integer.parseInt(mang[i].trim());
                if (x > max) max = x;
                if (x < min) min = x;
            }

            // 6. Gui requestId;max,min
            String kq = ma + ";" + max + "," + min;
            byte[] gui = kq.getBytes(StandardCharsets.UTF_8);
            DatagramPacket kqua = new DatagramPacket(gui, gui.length, ip, port);
            socket.send(kqua);
            System.out.println("Ket qua da gui: " + kq);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
