package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class SXTheoVTGoc {

    public static void main(String[] args) {
        String studentCode = "B22DCCN015";
        String host = "203.162.10.109";
        String qCode = "aKZwZxWk";
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

            // 4. Tach requestId va cac cap chuoi:viTri
            String chuoi = data.replace(",", " ").replace(";", " ");
            String[] a = chuoi.trim().split("\\s+");
            String id = a[0];

            // 5. Sap xep cac chuoi theo vi tri goc
            TreeMap<Integer, String> mang = new TreeMap<>();
            for (int i = 1; i < a.length; i++) {
                String[] cap = a[i].split(":");
                int viTri = Integer.parseInt(cap[1]);
                mang.put(viTri, cap[0]);
            }

            // 6. Ghep cac chuoi theo thu tu
            String kq = id + ";";
            boolean daThem = false;
            for (String tu : mang.values()) {
                if (daThem) {
                    kq += ",";
                }
                kq += tu;
                daThem = true;
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

/*
[Mã câu hỏi (qCode): aKZwZxWk].  Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng ";studentCode;qCode". Ví dụ: ";B15DCCN009;F3E8B2D4".
b. Nhận thông điệp là một chuỗi từ server theo định dạng "requestId;string", với:
--- requestId là chuỗi ngẫu nhiên duy nhất.
---string là một chuỗi chứa các chuỗi con bị thay đổi vị trí. Ví dụ: "veM3xgA1g:4,IPFfgEanY:5,aWXlSzDwe:2,PHupvPc:3,PR3gH8ahN:6,UEEKHLIt:7,M6dpWTE:1"
c. Xử lý chuỗi xáo trộn và gửi về chuỗi sau khi sắp xếp: "requestId;string". Ví dụ chuỗi đã được xử lý: "M6dpWTE,aWXlSzDwe,PHupvPc,veM3xgA1g,IPFfgEanY,PR3gH8ahN,UEEKHLIt"
d. Đóng socket và kết thúc chương trình.
*/
