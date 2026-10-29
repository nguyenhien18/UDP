package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP2208_KyTuNhieuNhat {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "t2D3wSUL";
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
            if (c.length != 2 || c[1].isEmpty()) {
                throw new Exception("Sai dinh dang Server");
            }
            String ma = c[0];
            String s = c[1];

            // 5. Tim ky tu xuat hien nhieu nhat
            int max = 0;
            char kt = s.charAt(0);
            for (int i = 0; i < s.length(); i++) {
                int dem = 0;
                for (int j = 0; j < s.length(); j++) {
                    if (s.charAt(i) == s.charAt(j)) dem++;
                }
                if (dem > max) {
                    max = dem;
                    kt = s.charAt(i);
                }
            }

            // 6. Liet ke vi tri (bat dau tu 1, co dau phay cuoi)
            String kq = ma + ";" + kt + ":";
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == kt) kq += (i + 1) + ",";
            }

            // 7. Gui requestId;kyTu:viTri1,viTri2,
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
