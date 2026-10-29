package UDP;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class UDP2209_Student {
    public static void main(String[] args) {
        String studentCode = "B23DCCN290";
        String qCode = "KPXvN2NX";
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

            // 3. Nhan goi: 8 byte requestId + Student da serialize
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

            // 5. Doc Student tu byte so 9 tro di
            ByteArrayInputStream bis = new ByteArrayInputStream(b, off + 8, len - 8);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Student sv = (Student) ois.readObject();
            ois.close();
            System.out.println("Nhan: " + sv);

            // 6. Chuan hoa name, tao email
            String name = sv.getName().trim().toLowerCase(Locale.ROOT);
            if (name.isEmpty()) throw new Exception("Ten trong");
            String[] w = name.split("\\s+");
            String ten = "";
            String email = w[w.length - 1];
            for (int i = 0; i < w.length; i++) {
                String x = w[i];
                if (!ten.isEmpty()) ten += " ";
                ten += x.substring(0, 1).toUpperCase(Locale.ROOT) + x.substring(1);
                if (i < w.length - 1) email += x.charAt(0);
            }
            sv.setName(ten);
            sv.setEmail(email + "@ptit.edu.vn");
            System.out.println("Da sua: " + sv);

            // 7. Chuyen Student da sua thanh byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(sv);
            oos.flush();
            byte[] objB = baos.toByteArray();
            oos.close();

            // 8. Ghep 8 byte requestId + byte cua Student
            byte[] gui = new byte[8 + objB.length];
            System.arraycopy(idB, 0, gui, 0, 8);
            System.arraycopy(objB, 0, gui, 8, objB.length);

            // 9. Gui Object ve Server
            DatagramPacket kqua = new DatagramPacket(gui, gui.length, ip, port);
            socket.send(kqua);
            System.out.println("Da gui Student ve Server");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }
}
