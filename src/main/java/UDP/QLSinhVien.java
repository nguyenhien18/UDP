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

public class QLSinhVien {

    public static void main(String[] args) {
        String studentCode = "B22DCCN902";
        String host = "36.50.135.242";
        String qCode = "RIdaplG7";
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

            // 4. Tach 8 byte requestId va doc doi tuong Student
            if (pNhan.getLength() < 8) {
                throw new IllegalArgumentException("Goi tin khong du 8 byte requestId");
            }

            byte[] requestId = new byte[8];
            System.arraycopy(pNhan.getData(), pNhan.getOffset(), requestId, 0, 8);
            System.out.println("Request ID: " + new String(requestId, StandardCharsets.UTF_8));

            ByteArrayInputStream input = new ByteArrayInputStream(
                    pNhan.getData(), pNhan.getOffset() + 8, pNhan.getLength() - 8
            );
            Student student;
            try (ObjectInputStream ois = new ObjectInputStream(input)) {
                student = (Student) ois.readObject();
            }
            System.out.println(student);

            // 5. Chuan hoa ten sinh vien
            String[] mang = student.getName().trim().split("\\s+");
            String ten = "";
            for (int i = 0; i < mang.length; i++) {
                String tu = mang[i];
                ten += Character.toUpperCase(tu.charAt(0))
                        + tu.substring(1).toLowerCase() + " ";
            }
            student.setName(ten.trim());

            // 6. Tao email tu ten va chu cai dau cua ho, ten dem
            String email = mang[mang.length - 1].toLowerCase();
            for (int i = 0; i < mang.length - 1; i++) {
                email += Character.toLowerCase(mang[i].charAt(0));
            }
            email += "@ptit.edu.vn";
            student.setEmail(email);

            System.out.println(student);

            // 7. Chuyen doi tuong thanh mang byte
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(output)) {
                oos.writeObject(student);
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
}
