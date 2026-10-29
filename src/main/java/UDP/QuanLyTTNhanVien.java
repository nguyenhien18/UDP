package UDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class QuanLyTTNhanVien {

    public static void main(String[] args) {
        String studentCode = "B21DCCN005";
        String host = "203.162.10.109";
        String qCode = "ySsumsIE";
        int port = 2209;

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

            // 4. Tach 8 byte requestId va doc doi tuong Employee
            if (pNhan.getLength() < 8) {
                throw new IllegalArgumentException("Goi tin khong du 8 byte requestId");
            }

            byte[] requestId = new byte[8];
            System.arraycopy(pNhan.getData(), pNhan.getOffset(), requestId, 0, 8);
            System.out.println("Request ID: " + new String(requestId, StandardCharsets.UTF_8));

            ByteArrayInputStream input = new ByteArrayInputStream(
                    pNhan.getData(), pNhan.getOffset() + 8, pNhan.getLength() - 8
            );
            Employee employee;
            try (ObjectInputStream ois = new ObjectInputStream(input)) {
                employee = (Employee) ois.readObject();
            }
            System.out.println(employee);

            // 5. Chuan hoa ten va ngay vao lam
            employee.setName(chuanHoaTen(employee.getName()));
            employee.setHireDate(chuanhoaNgay(employee.getHireDate()));

            // 6. Tang luong theo nam vao lam da chuan hoa
            employee.setSalary(tangLuong(employee.getHireDate(), employee.getSalary()));

            System.out.println(employee);

            // 7. Chuyen doi tuong thanh mang byte
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(output)) {
                oos.writeObject(employee);
                oos.flush();
            }

            // 8. Ghep requestId voi doi tuong va gui ve server
            byte[] objectData = output.toByteArray();
            byte[] out = new byte[8 + objectData.length];
            System.arraycopy(requestId, 0, out, 0, 8);
            System.arraycopy(objectData, 0, out, 8, objectData.length);
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
            // 9. Dong socket khi ket thuc, ke ca khi co loi
            if (socket != null) {
                socket.close();
            }
        }
    }

    public static String chuanHoa1(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String chuanHoaTen(String s) {
        String[] mang = s.trim().split("\\s+");
        String kq = "";
        for (String tu : mang) {
            kq += chuanHoa1(tu) + " ";
        }
        return kq.substring(0, kq.length() - 1);
    }

    public static String chuanhoaNgay(String s) {
        String[] ngay = s.replace("-", " ").split("\\s+");
        return ngay[2] + "/" + ngay[1] + "/" + ngay[0];
    }

    public static double tangLuong(String ngay, double luongCu) {
        // Ngay dau vao da duoc chuan hoa thanh dd/MM/yyyy
        String[] mang = ngay.replace("/", " ").split("\\s+");
        int tong = 0;
        for (char x : mang[2].toCharArray()) {
            tong += x - '0';
        }
        return luongCu * (1 + tong / 100.0);
    }
}
