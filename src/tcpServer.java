import java.io.*;
import java.net.*;
import java.sql.*;

public class tcpServer {

	public static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		// 建立ServerSocket
		ServerSocket serverSocket = null;
		ServerThread serverThread;
		final int portNum = 8080; // 使用11520发现一直是在创建serverSocket时失败
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("启动服务端时，发生错误....");
		}
		System.out.println("服务端已启动....\n等待客户端的连接......");
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				if (clientSocket.isConnected())
					System.out.println("客户端【" +clientSocket.getInetAddress().getHostAddress() + "】连接进入...");
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("等待客户端呼入时，发生错误....");
			}
			if (clientSocket.isConnected()) { // 增加对socket连接的判断，避免因为客户端的在中途断开连接引起的线程错误
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
				System.out.println("客户端【" + socket.getInetAddress().getHostAddress() + "】异常中断...");
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
				        String s1="学号："+rs.getString("number")+" "+"姓名："+rs.getString("name")+" "+"出生："+rs.getString("birth")+" "+"数学："+rs.getString("math")+"英语："+rs.getString("english");
				        outs.writeUTF(s1);
				      }
			    }
			    catch (Exception e) {
			      System.out.print("Error loading Mysql Driver!");
			      e.printStackTrace();
			    }
				if (s.equals("end")) {
					outs.writeUTF("连接断开");
					socket.close(); // 关闭当前客户端的连接
					System.out.println("客户端IP【" + socket.getInetAddress().getHostAddress() + "】正常退出连接！");
					this.stop(); // 当前线程结束
				} else {
					System.out.println("客户端IP【" + socket.getInetAddress().getHostAddress() + "】发来查询信息：" + s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("向客户端发送信息失败...");
			}
		}
	}
}
