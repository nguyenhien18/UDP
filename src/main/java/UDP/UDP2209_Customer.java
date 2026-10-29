package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class UDP2209_Customer {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "aP0zrub8";
        String host = "36.50.135.242";
        int port = 2209;
        DatagramSocket socket = null;

        try {
            // 1. Tao Socket va IP
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // 2. Gui ;studentCode;qCode
            String req = ";" + studentCode + ";" + qCode;
            byte[] a = req.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(a, a.length, ip, port);
            socket.send(p);
            System.out.println("Gui: " + req);

            // 3. Nhan goi: 8 byte requestId + Customer da serialize
            byte[] b = new byte[65507];
            DatagramPacket nhan = new DatagramPacket(b, b.length);
            socket.receive(nhan);
            int off = nhan.getOffset();
            int len = nhan.getLength();
            if (len <= 8) throw new Exception("Goi tin khong du 8 byte + Object");

            // 4. Luu NGUYEN 8 byte requestId
            byte[] idB = Arrays.copyOfRange(b, off, off + 8);
            System.out.println("requestId: " +
                    new String(idB, StandardCharsets.US_ASCII));

            // 5. Doc Customer tu byte so 9 tro di
            ByteArrayInputStream bis = new ByteArrayInputStream(b, off + 8, len - 8);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Customer kh = (Customer) ois.readObject();
            ois.close();
            System.out.println("Nhan: " + kh);

            // 6. Chuan hoa name, dayOfBirth, userName
            String name = kh.getName().trim().toLowerCase(Locale.ROOT);
            if (name.isEmpty()) throw new Exception("Ten trong");
            String[] w = name.split("\\s+");
            String last = w[w.length - 1];

            // Ten moi: DUONG, Nguyen Van Hai
            String ten = last.toUpperCase(Locale.ROOT);
            if (w.length > 1) ten += ", ";
            String user = "";
            for (int i = 0; i < w.length - 1; i++) {
                String x = w[i];
                if (i > 0) ten += " ";
                ten += x.substring(0, 1).toUpperCase(Locale.ROOT) + x.substring(1);
                user += x.charAt(0);
            }
            user += last;

            // MM-dd-yyyy -> dd/MM/yyyy
            String[] d = kh.getDayOfBirth().trim().split("-");
            if (d.length != 3) throw new Exception("Ngay sinh sai dinh dang");
            String ngay = d[1] + "/" + d[0] + "/" + d[2];

            kh.setName(ten);
            kh.setDayOfBirth(ngay);
            kh.setUserName(user);
            System.out.println("Da sua: " + kh);

            // 7. Chuyen Customer da sua thanh byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(kh);
            oos.flush();
            byte[] objB = baos.toByteArray();
            oos.close();

            // 8. Ghep 8 byte requestId + byte cua Customer
            byte[] gui = new byte[8 + objB.length];
            System.arraycopy(idB, 0, gui, 0, 8);
            System.arraycopy(objB, 0, gui, 8, objB.length);

            // 9. Gui Object ve Server
            DatagramPacket kqua = new DatagramPacket(gui, gui.length, ip, port);
            socket.send(kqua);
            System.out.println("Da gui Customer ve Server");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
