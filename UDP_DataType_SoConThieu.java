package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDP_DataType_SoConThieu {

    public static void main(String[] args) {
        String studentCode = "B22DCCN902";
        String host = "36.50.135.242";
        String qCode = "USkx0ZcS";
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

            // 4. Tach requestId, gioi han n va danh sach so
            String[] a = data.trim().split(";");
            String id = a[0];
            int n = Integer.parseInt(a[1]);
            String[] mang = a[2].trim().split(",");

            // 5. Dem cac so da xuat hien
            int[] dem = new int[1000005];
            for (int i = 0; i < mang.length; i++) {
                int x = Integer.parseInt(mang[i]);
                dem[x]++;
            }

            // 6. Tim cac so con thieu va ghep ket qua
            String kq = id + ";";
            for (int i = 1; i <= n; i++) {
                if (dem[i] == 0) {
                    kq += String.format("%d,", i);
                }
            }
            kq = kq.substring(0, kq.length() - 1);

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
