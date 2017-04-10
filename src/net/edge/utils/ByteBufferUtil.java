package net.edge.utils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * A static-utility class containing extension or helper methods for {@link ByteBuffer}s.
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class ByteBufferUtil {
	
	/**
	 * The terminator used within the client, equal to <tt>10</tt> and otherwise know as the Jagex {@code String}
	 * terminator.
	 */
	public static final char J_STRING_TERMINATOR = '\n';
	
	/**
	 * The default {@code String} terminator, equal to <tt>0</tt> and otherwise known as the 'null' {@code String}
	 * terminator.
	 */
	public static final char DEFAULT_STRING_TERMINATOR = '\0';
	
	/**
	 * Gets a 24-bit medium integer from the specified {@link ByteBuffer}, this method does not mark the ByteBuffers
	 * current position.
	 * @param buffer The ByteBuffer to read from.
	 * @return The read 24-bit medium integer.
	 */
	public static int getMedium(ByteBuffer buffer) {
		return (buffer.getShort() & 0xFFFF) << 8 | buffer.get() & 0xFF;
	}
	
	/**
	 * Gets a null-terminated String from the specified ByteBuffer.
	 * @param buffer The ByteBuffer to read from.
	 * @return The null-terminated String.
	 */
	public static String getString(ByteBuffer buffer) {
		return getString(buffer, DEFAULT_STRING_TERMINATOR);
	}
	
	/**
	 * Gets a newline-terminated String from the specified ByteBuffer.
	 * @param buffer The ByteBuffer to read from.
	 * @return The newline-terminated String.
	 */
	public static String getJString(ByteBuffer buffer) {
		return getString(buffer, J_STRING_TERMINATOR);
	}
	
	/**
	 * Reads {@code length} bytes from the specified {@link ByteBuffer}.
	 * @param buffer The ByteBuffer to read from.
	 * @param length The amount of bytes to read.
	 * @return The read bytes.
	 */
	public static byte[] get(ByteBuffer buffer, int length) {
		byte[] data = new byte[length];
		buffer.get(data);
		return data;
	}
	
	/**
	 * Gets a {@link String} from the specified {@link ByteBuffer}, the ByteBuffer will continue to get until the
	 * specified {@code terminator} is reached.
	 * <p>
	 * We use a {@link ByteArrayOutputStream} as it is self expanding. We don't want to waste precious time determining
	 * a fixed length for the {@code String}.
	 * </p>
	 * @param buffer     The ByteBuffer to read from.
	 * @param terminator The terminator which denotes when to stop reading.
	 * @return The read String.
	 */
	public static String getString(ByteBuffer buffer, char terminator) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		for(; ; ) {
			int read = buffer.get() & 0xFF;
			if(read == terminator) {
				break;
			}
			os.write(read);
		}
		return new String(os.toByteArray());
	}
	
	/**
	 * Reads a 'smart' (either a {@code byte} or {@code short} depending on the
	 * value) from the specified buffer.
	 * @param buffer The buffer.
	 * @return The 'smart'.
	 */
	public static int getSmart(ByteBuffer buffer) {
		int peek = buffer.get(buffer.position()) & 0xFF;
		if(peek < 128) {
			//System.out.println("reading byte");
			return buffer.get() & 0xFF;
		}
		//System.out.println("reading short");
		return (buffer.getShort() & 0xFFFF) - 32768;
	}
	
	/**
	 * Puts a 'smart' (either a {@code byte} or {@code short}.
	 * @param buffer The buffer.
	 * @param value  The value to write.
	 */
	public static void putSmart(ByteBuffer buffer, int value) {
		if(value < 128) {
			//System.out.println("writing byte");
			buffer.put((byte) value);
		} else {
			//System.out.println("writing short");
			value += 32768;
			buffer.put((byte) (value >> 8));
			buffer.put((byte) value);
		}
	}
	
	/**
	 * Sole private constructor to discourage instantiation of this class.
	 */
	private ByteBufferUtil() {
	}
	
}