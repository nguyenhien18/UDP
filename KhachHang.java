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

public class KhachHang {

    public static void main(String[] args) {
        String studentCode = "B22DCCN902";
        String host = "36.50.135.242";
        String qCode = "rvSNGkgt";
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

            // 4. Tach 8 byte requestId va doc doi tuong Customer
            if (pNhan.getLength() < 8) {
                throw new IllegalArgumentException("Goi tin khong du 8 byte requestId");
            }

            byte[] requestId = new byte[8];
            System.arraycopy(pNhan.getData(), pNhan.getOffset(), requestId, 0, 8);
            System.out.println("Request ID: " + new String(requestId, StandardCharsets.UTF_8));

            ByteArrayInputStream input = new ByteArrayInputStream(
                    pNhan.getData(), pNhan.getOffset() + 8, pNhan.getLength() - 8
            );
            Customer customer;
            try (ObjectInputStream ois = new ObjectInputStream(input)) {
                customer = (Customer) ois.readObject();
            }
            System.out.println(customer);

            // 5. Chuan hoa ten khach hang
            String[] mang = customer.getName().split("\\s+");
            String ten = mang[mang.length - 1].toUpperCase() + ", ";
            for (int i = 0; i < mang.length - 1; i++) {
                String tu = mang[i];
                ten += Character.toUpperCase(tu.charAt(0))
                        + tu.substring(1).toLowerCase() + " ";
            }
            customer.setName(ten.trim());

            // 6. Doi dinh dang ngay sinh
            String[] ngay = customer.getDayOfBirth().split("-");
            String ngaySinh = ngay[1] + "/" + ngay[0] + "/" + ngay[2];
            customer.setDayOfBirth(ngaySinh);

            // 7. Tao ten dang nhap
            String userName = "";
            for (int i = 0; i < mang.length - 1; i++) {
                userName += Character.toLowerCase(mang[i].charAt(0));
            }
            userName += mang[mang.length - 1].toLowerCase();
            customer.setUserName(userName);

            System.out.println(customer);

            // 8. Chuyen doi tuong thanh mang byte
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(output)) {
                oos.writeObject(customer);
                oos.flush();
            }

            // 9. Ghep requestId voi doi tuong va gui ve server
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
            // 10. Dong socket khi ket thuc, ke ca khi co loi
            if (socket != null) {
                socket.close();
            }
        }
    }
}
