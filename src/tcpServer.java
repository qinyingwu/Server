import java.io.*;
import java.net.*;
import java.sql.*;

public class tcpServer {

	public static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		// ����ServerSocket
		ServerSocket serverSocket = null;
		ServerThread serverThread;
		final int portNum = 8080; // ʹ��11520����һֱ���ڴ���serverSocketʱʧ��
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("���������ʱ����������....");
		}
		System.out.println("�����������....\n�ȴ��ͻ��˵�����......");
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				if (clientSocket.isConnected())
					System.out.println("�ͻ��ˡ�" +clientSocket.getInetAddress().getHostAddress() + "�����ӽ���...");
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("�ȴ��ͻ��˺���ʱ����������....");
			}
			if (clientSocket.isConnected()) { // ���Ӷ�socket���ӵ��жϣ�������Ϊ�ͻ��˵�����;�Ͽ�����������̴߳���
				serverThread = new ServerThread(clientSocket);
				serverThread.start();
			} else {
				continue;
			}
		}
	}

}

class ServerThread extends Thread {
	Socket socket;
	String s = null;
	DataOutputStream outs = null;
	DataInputStream ins = null;

	ServerThread(Socket sct) {
		socket = sct;
		try {
			ins = new DataInputStream(sct.getInputStream());
			outs = new DataOutputStream(sct.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (socket.isConnected())
					s = ins.readUTF();
				else
					break;
			} catch (IOException e) {
				System.out.println("�ͻ��ˡ�" + socket.getInetAddress().getHostAddress() + "���쳣�ж�...");
				break;
			}

			try {
				try {
					java.sql.Connection conn = null;
					Class.forName("com.mysql.jdbc.Driver");
					conn = java.sql.DriverManager.getConnection(
							"jdbc:mysql://localhost/a?useUnicode=true&characterEncoding=utf-8&useSSL=false", "774405003",
							"qin137869346");
					Statement stmt = conn.createStatement();
				    ResultSet rs = stmt.executeQuery("select * from chengji where number='"+s+"'");
				    while (rs.next()) {
				        String s1="ѧ�ţ�"+rs.getString("number")+" "+"������"+rs.getString("name")+" "+"������"+rs.getString("birth")+" "+"��ѧ��"+rs.getString("math")+"Ӣ�"+rs.getString("english");
				        outs.writeUTF(s1);
				      }
			    }
			    catch (Exception e) {
			      System.out.print("Error loading Mysql Driver!");
			      e.printStackTrace();
			    }
				if (s.equals("end")) {
					outs.writeUTF("���ӶϿ�");
					socket.close(); // �رյ�ǰ�ͻ��˵�����
					System.out.println("�ͻ���IP��" + socket.getInetAddress().getHostAddress() + "�������˳����ӣ�");
					this.stop(); // ��ǰ�߳̽���
				} else {
					System.out.println("�ͻ���IP��" + socket.getInetAddress().getHostAddress() + "��������ѯ��Ϣ��" + s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("��ͻ��˷�����Ϣʧ��...");
			}
		}
	}
}
