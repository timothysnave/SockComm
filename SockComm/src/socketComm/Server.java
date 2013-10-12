package socketComm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Server {
	private int localCode, remoteCode;
	private ServerSocket ss;
	private Socket s;
	private DataOutputStream out;
	private DataInputStream in;
	private boolean connected;
	private ArrayDeque<Socket> connections;
	
	public Server(int port, int localCode, int remoteCode, ArrayDeque<Socket>connections)
	{
		// Set up variables
		this.localCode = localCode;
		this.remoteCode = remoteCode;
		connected = false;
		this.connections = connections;
		
		try {
			ss = new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		try {
			
			while(true){
				System.out.println("Server started");
				s = ss.accept();
				in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
				if (in.readInt()==remoteCode){
					out.writeInt(localCode);
					out.flush();
					System.out.println("Client contacted");
					InetAddress add = s.getInetAddress();
					s.close();
					in = null;
					out = null;
					s = ss.accept();
					if (s != null && s.getInetAddress().equals(add)) // VERIFY WITH A CODE THE PURPOSE OF THE CONNECTION!!!
					{
						// Multiple Connections Allowed
						connections.add(s);
						
						// Single Connection Allowed
//						in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
//						out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
//						System.out.println("Client connected");
//						connected = true;
					}
				}
				else{
					s.close();
					in = null;
					out = null;
				}
			}
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * This is the easiest way I could think up to get an ArrayList<Socket>
	 * in Jython:
	 */
	
	public static ArrayDeque<Socket> getNewSocketList()
	{
		return new ArrayDeque<Socket>();
	}
	
	public static DataInputStream getDataInputStream(Socket s)
	{
		DataInputStream i = null;
		try {
			i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		} catch (Exception e) {}
		return i;
	}
	
	public static DataOutputStream getDataOutputStream(Socket s)
	{
		DataOutputStream o = null;
		try {
			o = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
		} catch (Exception e) {}
		return o;
	}
	
	
	/*
	 * Methods below are only useful for a single connection.
	 * Multiple connections must be dealt with directly through
	 * the sockets, by opening DataInputStreams from
	 * BufferedInputStreams from the socket's getInputStream()
	 * method. Same for output.
	 */
	
	public void send(int b)
	{
		try {
			out.write(b);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int receive()
	{
		int result;
		try {
			result = (int)in.readFloat();
		} catch (Exception e) {
			result = -1;
			try {
				s.close();
			} catch (IOException e1) {}
			in = null;
			out = null;
			connected = false;
			System.out.println("Connection closed");
		}
		return result;
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	
}
