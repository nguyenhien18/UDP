package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class XHMax {

    public static void main(String[] args) {
        String studentCode = "B21DCCN003";
        String host = "203.162.10.109";
        String qCode = "KFPR3GWV";
        int port = 2208;

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

            // 4. Tach requestId va chuoi
            String[] a = data.trim().split(";");
            String id = a[0];
            String chuoi = a[1];

            // 5. Dem va tim ky tu xuat hien nhieu nhat
            int[] dem = new int[1000];
            for (char x : chuoi.toCharArray()) {
                dem[x]++;
            }

            int max = -1;
            char kyTu = ' ';
            for (char x : chuoi.toCharArray()) {
                if (dem[x] > max) {
                    max = dem[x];
                    kyTu = x;
                }
            }

            // 6. Liet ke cac vi tri, bat dau tu 1
            String kq = id + ";" + kyTu + ":";
            for (int i = 0; i < chuoi.length(); i++) {
                if (chuoi.charAt(i) == kyTu) {
                    kq += String.format("%d,", i + 1);
                }
            }

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
