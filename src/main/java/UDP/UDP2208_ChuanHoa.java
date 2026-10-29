package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UDP2208_ChuanHoa {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "Yx3CHCJB";
        String host = "36.50.135.242";
        int port = 2208;
        DatagramSocket socket = null;

        try {
            // 1. Tao Socket va IP
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // 2. Gui ;studentCode;qCode
            String req = ";" + studentCode + ";" + qCode;
            byte[] a = req.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(a, a.length, ip, port);
            socket.send(p);
            System.out.println("Gui: " + req);

            // 3. Nhan requestId;data
            byte[] b = new byte[10000];
            DatagramPacket nhan = new DatagramPacket(b, b.length);
            socket.receive(nhan);
            String data = new String(nhan.getData(), nhan.getOffset(),
                    nhan.getLength(), StandardCharsets.UTF_8);
            System.out.println("Nhan: " + data);

            // 4. Tach requestId va chuoi
            String[] c = data.split(";", 2);
            if (c.length != 2) throw new Exception("Sai dinh dang Server");
            String ma = c[0];
            String s = c[1].trim().toLowerCase(Locale.ROOT);

            // 5. Chuan hoa: chu dau in hoa, con lai in thuong
            String kq = "";
            if (!s.isEmpty()) {
                String[] w = s.split("\\s+");
                for (int i = 0; i < w.length; i++) {
                    String x = w[i];
                    x = x.substring(0, 1).toUpperCase(Locale.ROOT)
                            + x.substring(1);
                    if (!kq.isEmpty()) kq += " ";
                    kq += x;
                }
            }

            // 6. Gui requestId;chuoiChuanHoa
            kq = ma + ";" + kq;
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
