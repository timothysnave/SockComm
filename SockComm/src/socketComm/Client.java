package socketComm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class Client {
	private int localCode, remoteCode, port;
	private Socket s;
	private DataOutputStream out;
	private DataInputStream in;
	private boolean connected, found;
	private String handle;
	private ArrayDeque<Socket> connections;
	
	public Client(int port, int localCode, int remoteCode, ArrayDeque<Socket>connections)
	{
		// Set up variables
		this.localCode = localCode;
		this.remoteCode = remoteCode;
		this.port = port;
		connected = false;
		this.connections = connections;
	}
	
//	public boolean scan(HashMap<String,InetAddress>addresses)
	public boolean scan()
	{
		System.out.println("Scanning...");
		/*
		 * This method will search for servers
		 * that match this client's remoteCode.
		 * 
		 * Return true if it finds a server
		 * Return false if none found
		 */
		found = false;
		try {
			
			// Get interfaces
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) e.nextElement();
				System.out.println("Interface: "+n.getDisplayName());
				Enumeration<InetAddress> ee = n.getInetAddresses();
				
				// For each interface, get addresses
				while(ee.hasMoreElements())
				{
					InetAddress a = ee.nextElement();
					System.out.println("Next Address...");
					String ip = a.getHostName();
					String ip2 = a.getHostAddress();
					System.out.println("HostName: "+ip);
					System.out.println("HostAddress: "+ip2);
					if (ip2.matches("\\d?\\d?\\d?.\\d?\\d?\\d?.\\d?\\d?\\d?.\\d?\\d?\\d?")
							&& !ip2.substring(0, 3).equals("127"))
					{
						String subnet = ip2.substring(0, ip2.lastIndexOf('.'));
						System.out.println("Subnet: "+subnet);
						for (int i=0; i<256; i++)
						{
							// Get subnet
							String thisIp = subnet + "." + i;
							InetAddress address = InetAddress.getByName(thisIp);
							System.out.println("Trying: "+address.getHostAddress());
							
							// Check each IP in subnet
							if (!address.getHostAddress().equals(ip) && address.isReachable(200))
							{
								Socket sock = null;
								DataInputStream input = null;
								DataOutputStream output = null;
								try{
								sock= new Socket(address, port);
								input = new DataInputStream(sock.getInputStream());
								output = new DataOutputStream(sock.getOutputStream());
								output.writeInt(localCode);
								output.flush();
								
								// If server is there and codes match...
								if (input.readInt()==remoteCode)
								{
									connections.add(sock);
								}
								}catch(Exception ex){
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		
		return found;
	}
	
	public boolean connect()
	{
		/*
		 * Stub method
		 * Complete later
		 */
		return false;
	}
	
	public void send(byte[] b)
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
			byte[] b={};
			result = in.read(b);
//			result = (int)in.readFloat();
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
	
	public static String parseBytes(byte[] ba)
	{
		String s = "";
		for (byte b: ba)
			s += Character.valueOf((char) b);
		return s;
	}
	
	public static byte[] getBytes(String s)
	{
		return s.getBytes();
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	
}