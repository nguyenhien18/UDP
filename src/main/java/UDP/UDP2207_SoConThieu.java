package UDP;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP2207_SoConThieu {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "3Du3KS4X";
        String host = "36.50.135.242";
        int port = 2207;
        DatagramSocket socket = null;

        try {
            // 1. Tao UDP Socket, chuan bi IP Server
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // 2. Gui ;studentCode;qCode
            String req = ";" + studentCode + ";" + qCode;
            byte[] a = req.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(a, a.length, ip, port);
            socket.send(p);
            System.out.println("Gui: " + req);

            // 3. Nhan requestId;n;A1,A2,...
            byte[] b = new byte[10000];
            DatagramPacket nhan = new DatagramPacket(b, b.length);
            socket.receive(nhan);
            String data = new String(nhan.getData(), nhan.getOffset(),
                    nhan.getLength(), StandardCharsets.UTF_8);
            System.out.println("Nhan: " + data);

            // 4. Tach requestId, n, danh sach so
            String[] c = data.split(";", 3);
            if (c.length != 3) throw new Exception("Sai dinh dang Server");
            String ma = c[0];
            int n = Integer.parseInt(c[1].trim());
            if (n < 1 || n >= 100) throw new Exception("n khong hop le");

            // 5. Danh dau cac gia tri da xuat hien
            boolean[] check = new boolean[n + 1];
            if (!c[2].trim().isEmpty()) {
                String[] mang = c[2].split(",");
                for (int i = 0; i < mang.length; i++) {
                    int x = Integer.parseInt(mang[i].trim());
                    if (x >= 1 && x <= n) check[x] = true;
                }
            }

            // 6. Tim cac so con thieu tu 1 den n
            String kq = "";
            for (int i = 1; i <= n; i++) {
                if (!check[i]) {
                    if (!kq.isEmpty()) kq += ",";
                    kq += i;
                }
            }

            // 7. Gui requestId;B1,B2,... ve Server
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
