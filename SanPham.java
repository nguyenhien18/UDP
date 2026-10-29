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

public class SanPham {

    public static void main(String[] args) {
        String studentCode = "B21DCCN319";
        String host = "203.162.10.109";
        String qCode = "tjedvqT8";
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

            // 4. Tach 8 byte requestId va doc doi tuong Product
            if (pNhan.getLength() < 8) {
                throw new IllegalArgumentException("Goi tin khong du 8 byte requestId");
            }

            byte[] requestId = new byte[8];
            System.arraycopy(pNhan.getData(), pNhan.getOffset(), requestId, 0, 8);
            System.out.println("Request ID: " + new String(requestId, StandardCharsets.UTF_8));

            ByteArrayInputStream input = new ByteArrayInputStream(
                    pNhan.getData(), pNhan.getOffset() + 8, pNhan.getLength() - 8
            );
            Product product;
            try (ObjectInputStream ois = new ObjectInputStream(input)) {
                product = (Product) ois.readObject();
            }
            System.out.println(product);

            // 5. Doi cho tu dau va tu cuoi trong ten san pham
            String[] mang = product.getName().trim().split("\\s+");
            if (mang.length > 1) {
                String tam = mang[0];
                mang[0] = mang[mang.length - 1];
                mang[mang.length - 1] = tam;
            }

            String ten = "";
            for (String tu : mang) {
                ten += tu + " ";
            }
            product.setName(ten.trim());

            // 6. Dao nguoc cac chu so cua so luong
            String soLuong = String.valueOf(product.getQuantity());
            String daoNguoc = "";
            for (int i = soLuong.length() - 1; i >= 0; i--) {
                daoNguoc += soLuong.charAt(i);
            }
            product.setQuantity(Integer.parseInt(daoNguoc));

            System.out.println(product);

            // 7. Chuyen doi tuong thanh mang byte
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(output)) {
                oos.writeObject(product);
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

/*
IntrinsicCandidate
    public static native void arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);

public String(Luồng byte từ package, chỉ số bắt đầu, độ rộng) {
*/
