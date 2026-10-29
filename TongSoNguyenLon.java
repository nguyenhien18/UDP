package UDP;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class TongSoNguyenLon {

    public static void main(String[] args) {
        String studentCode = "B21DCCN005";
        String host = "203.162.10.109";
        String qCode = "2sIjAYaU";
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

            // 4. Tach requestId va hai so nguyen lon
            String chuoi = data.trim().replace(';', ' ');
            String[] a = chuoi.trim().split("\\s+");
            String id = a[0];
            BigInteger so1 = new BigInteger(a[1]);
            BigInteger so2 = new BigInteger(a[2]);

            // 5. Tinh tong va hieu
            BigInteger tong = so1.add(so2);
            BigInteger hieu = so1.subtract(so2);

            // 6. Ghep requestId voi ket qua
            String kq = String.format("%s;%s,%s", id, tong.toString(), hieu.toString());

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
