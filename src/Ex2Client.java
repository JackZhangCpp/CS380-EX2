import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.*;
import java.util.zip.CRC32;

public class Ex2Client {
	private static byte[] message; 
	private static byte[] checkSum; 
	private static int index = 0;
	private static int checkSumIndex = 0;
	public static void main(String[] args) {
		try  {
			Socket socket = new Socket("18.221.102.182", 38102);
			System.out.println("Connected to server");
			InputStream is = socket.getInputStream();
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
			message = new byte[100];
			System.out.println("Received bytes: ");
			System.out.print("   ");
			int count = 0; 
			for (int i = 0; i < 100; i++) {
				byte firstBits = (byte)is.read();
				System.out.print(Integer.toHexString(firstBits & 0xF));
				count++;
				firstBits = (byte)(firstBits << 4);
				byte secondBits = (byte)is.read();
				System.out.print(Integer.toHexString(secondBits & 0xF));
				count++;
				if (count % 20 == 0) {
					System.out.print("\n   ");
				}
				byte byteMes =  (byte)(firstBits | secondBits);
				message[index++] = byteMes;
			}
			CRC32 check = new CRC32();
			check.update(message,0, message.length); 
			System.out.println("\nGenerated CRC32: "+Long.toHexString(check.getValue()));
			checkSum = new byte[4];
			for (int i=24; i >= 0; i-=8) {
				Long shiftDone = check.getValue() >> i;
				byte bytePattern = (byte) (shiftDone & 0xFF); 
				checkSum[checkSumIndex++] = bytePattern;
			}
			out.write(checkSum, 0, checkSum.length);
			byte response = (byte) is.read();
			if (response == 1) { 
				System.out.println("Response Good.");
			} else {
				System.out.println("Response Bad.");
			}
		} catch (Exception e) {e.printStackTrace();}
	} 


}