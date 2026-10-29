package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class so {

    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String host = "36.50.135.242";
        String qCode = "3Du3KS4X";
        int port = 2207;

        DatagramSocket socket = null;

        try {
            // 1. Tao socket va dat thoi gian cho toi da 5 giay
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // 2. Gui ma sinh vien va ma cau hoi
            String req = ";" + studentCode + ";" + qCode;
            byte[] b = req.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(
                    b, b.length, ip, port
            );
            socket.send(p);
            System.out.println(req);

            // 3. Nhan du lieu tu server
            byte[] buf = new byte[8192];
            DatagramPacket pNhan = new DatagramPacket(
                    buf, buf.length
            );
            socket.receive(pNhan);

            String data = new String(
                    pNhan.getData(),
                    pNhan.getOffset(),
                    pNhan.getLength(),
                    StandardCharsets.UTF_8
            );
            System.out.println(data);

            // 4. Tach du lieu: requestId;n;z1,z2,...
            String[] a = data.split(";", 3);
            String id = a[0];
            int n = Integer.parseInt(a[1].trim());
            String[] mang = a[2].split(",");

            // 5. Danh dau cac so da co
            boolean[] co = new boolean[n + 1];
            for (int i = 0; i < mang.length; i++) {
                int x = Integer.parseInt(mang[i].trim());
                if (x >= 1 && x <= n) {
                    co[x] = true;
                }
            }

            // 6. Tim cac so con thieu va ghep requestId
            String kq = "";
            for (int i = 1; i <= n; i++) {
                if (!co[i]) {
                    if (!kq.isEmpty()) {
                        kq += ",";
                    }
                    kq += i;
                }
            }
            kq = id + ";" + kq;

            System.out.println(kq);

            // 7. Gui ket qua ve server
            byte[] out = kq.getBytes(StandardCharsets.UTF_8);
            DatagramPacket pGui = new DatagramPacket(
                    out, out.length, ip, port
            );
            socket.send(pGui);
            System.out.println("Da gui ket qua");

        } catch (SocketTimeoutException e) {
            System.out.println("Khong nhan duoc phan hoi tu server sau 5 giay");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8. Dong socket khi ket thuc, ke ca khi co loi
            if (socket != null) {
                socket.close();
            }
        }
    }
}
