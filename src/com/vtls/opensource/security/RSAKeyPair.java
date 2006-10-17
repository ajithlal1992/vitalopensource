package com.vtls.opensource.security;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.security.Security;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Provider;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This class is used to create, save and read RSA KeyPairs and/or invdividual Public/Private Keys.
 * This class can also be used to encrypt and decrypt InputStreams using a given RSA key of the same encryption strength
 * 
 * General usage:<p>
 * 		<code>KeyPair key_pair = RSAKeyPair.generate();</code><br>
 * 		<code>RSAKeyPair.write(key_pair, "public.key", "private.key");</code><br>
 * 		<code>key_pair = RSAKeyPair.read("public.key", "private.key");</code><br>
 * 		<code>encrypt(input, output, key_pair.getPrivate());</code><br>
 * 		<code>decrypt(input, output, key_pair.getPublic());</code><br>
 * 
 * @author Chris Hall
 * @version 1.0
 */
public class RSAKeyPair {
	/**
	 * The logger.
	 */
	private static final Logger m_logger = Log4JLogger.getLogger(RSAKeyPair.class);
	
	/**
	 * The strength of encryption to use.
	 */
	private static int ENCRYPTION_STRENGTH = 1024;
	
	/**
	 * The block size to use during encryption.
	 */
	private static int ENCRYPTION_BLOCK_SIZE = 100;
	
	/**
	 * The block size to use during decryption.
	 */
	private static int DECRYPTION_BLOCK_SIZE = 128;
	
	/**
	 * The algorithm for the actual cipher to use.
	 */
	private static String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
	
	/**
	 * The algorithm used to make/load the Keys.
	 */
	private static String KEYPAIR_ALGORITHM = "RSA";
	
	/**
	 * What to use to make a random number.
	 */
	private static String RANDOM_SOURCE = "SHA1PRNG";
	
	/**
	 * How to seed the random number.
	 */
	private static String RANDOM_SEED = "SUN";
	
	/**
	 * Generates a KeyPair with a given block size.
	 * 
	 * 
	 * @return A KeyPair with the given block size.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 */
	public static KeyPair create()
	throws NoSuchAlgorithmException,
			NoSuchProviderException {
		//Instantiate the RSA KeyPair generator.
		KeyPairGenerator generator = KeyPairGenerator.getInstance(KEYPAIR_ALGORITHM);
		//Instatiate the random source and seed it into the generator.
		generator.initialize(ENCRYPTION_STRENGTH, SecureRandom.getInstance(RANDOM_SOURCE, RANDOM_SEED));
		//Generate the KeyPair.
		return generator.generateKeyPair();
	}
	
	/**
	 * Attempts to load a KeyPair from disk by opening FileInputStreama and calling read().
	 * 
	 * @param public_key_filename The filename of the PublicKey.
	 * @param private_key_filename The filename of the PrivateKey.
	 * 
	 * @return The KeyPair read from disk.
	 * 
	 * @throws FileNotFoundException If the file does not exists.
	 * @throws IOException If reading from the InputStream fails.
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the InputStream is not a properly formatted key.
	 * @throws InvalidKeySpecException If the InputStream is not a properly formatted key.
	 */
	public static KeyPair read(String public_key_filename, String private_key_filename)
	throws FileNotFoundException,
			IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			InvalidKeySpecException {
		return read(new FileInputStream(public_key_filename), new FileInputStream(private_key_filename));
	}
	
	/**
	 * Attempts to load a KeyPair from InputStreams by calling read() for each key.
	 * 
	 * @param public_key_input The InputStream of the PublicKey.
	 * @param private_key_input The InputStream of the PrivateKey.
	 * 
	 * @return The KeyPair read from the InputStreams.
	 * 
	 * @throws IOException If reading from the InputStream fails.
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the InputStream is not a properly formatted key.
	 * @throws InvalidKeySpecException If the InputStream is not a properly formatted key.
	 */
	public static KeyPair read(InputStream public_key_input, InputStream private_key_input)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			InvalidKeySpecException {
		return new KeyPair(
				(PublicKey) read(public_key_input),
				(PrivateKey) read(private_key_input));
	}
	
	/**
	 * Attempts to write a KeyPair to OutputStreams by calling write() for each key.
	 * 
	 * @param key_pair The KeyPair to be written to the OutputStreams.
	 * @param public_key_output The OutputStream of the PublicKey.
	 * @param private_key_output The OutputStream of the PrivateKey.
	 * 
	 * @throws IOException If writing to the OutputStream fails.
	 */
	public static void write(KeyPair key_pair, OutputStream public_key_output, OutputStream private_key_output) 
	throws IOException {
		write(key_pair.getPublic(), public_key_output);
		write(key_pair.getPrivate(), private_key_output);
	}
	
	/**
	 * Attempts to write a KeyPair to files by calling write() for each key.
	 * 
	 * @param key_pair The KeyPair to be written to the files.
	 * @param public_key_filename The filename of the PublicKey.
	 * @param private_key_filename The filename of the PrivateKey.
	 * 
	 * @throws FileNotFoundException If the file does not exists.
	 * @throws IOException If writing to the OutputStream fails.
	 */
	public static void write(KeyPair key_pair, String public_key_filename, String private_key_filename)
	throws FileNotFoundException,
			IOException {
		write(key_pair, new FileOutputStream(public_key_filename), new FileOutputStream(private_key_filename));
	}
	
	
	/**
	 * Attempts to write a Key to a file..
	 * 
	 * @param key The key to be written to the file.
	 * @param key_filename The filename to be written to.
	 * 
	 * @throws FileNotFoundException If the file does not exists.
	 * @throws IOException If writing to the OutputStream fails.
	 */
	public static void write(Key key, String key_filename)
		throws FileNotFoundException,
			IOException {
		write(key, new FileOutputStream(key_filename));
	}
	
	/**
	 * Attempts to write a Key to an OutputStream.
	 * 
	 * @param key The key to be written to the OutputStream.
	 * @param key_output The OutputStream to be written to.
	 * 
	 * @throws IOException If writing to the OutputStream fails.
	 */
	public static void write(Key key, OutputStream key_output)
	throws IOException {
		key_output.write(key.getEncoded());
		key_output.flush();
	}
	
	/**
	 * Attempts to read a Key from a file.
	 * 
	 * @param key_filename The filename of the file containing the Key.
	 * 
	 * @return The Key read from the file.
	 * 
	 * @throws FileNotFoundException If the file does not exist.
	 * @throws IOException If reading from the InputStream fails.
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the InputStream is not a properly formatted key.
	 * @throws InvalidKeySpecException If the InputStream is not a properly formatted key.
	 */
	public static Key read(String key_filename)
	throws FileNotFoundException,
			IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			InvalidKeySpecException {
		return read(new FileInputStream(key_filename));
	}
	
	/**
	 * Attempts to read a Key from an InputStream.
	 * 
	 * @param key_input The InputStream containing the Key.
	 * 
	 * @return The Key read from the InputStream.
	 * 
	 * @throws IOException If reading from the InputStream fails.
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the InputStream is not a properly formatted key.
	 * @throws InvalidKeySpecException If the InputStream is not a properly formatted key.
	 */
	public static Key read(InputStream key_input)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			InvalidKeySpecException {
		//Read the InputStream into an array of bytes.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    for (int n; (n = key_input.read()) != -1;) {
	    	baos.write(n);
	    }
	    try {
			//Attempt to get the PublicKey.
			return KeyFactory.getInstance(KEYPAIR_ALGORITHM).generatePublic(new X509EncodedKeySpec(baos.toByteArray()));
		} catch (InvalidKeySpecException ikse) {
			//Invalid key found.  Try again as a PrivateKey.
			return KeyFactory.getInstance(KEYPAIR_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(baos.toByteArray()));
		}
	}
	
	/**
	 * A front-end to crypt() which tells it to encrypt.
	 * 
	 * @param input The data to be encrypted.
	 * @param output The encrypted data.
	 * @param key The Key used to encrypt.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the Key is not a properly formatted key.
	 * @throws IOException If reading from the InputStream or writing to the OutputStream fails.
	 * @throws IllegalBlockSizeException The data to be decrypted is not of valid length.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 * @throws BadPaddingException The data to be decrypted is not of valid length.
	 * @throws NoSuchPaddingException Can't find the specified padding format.  Won't be thrown unless default values are change 
	 */
	public static void encrypt(InputStream input, OutputStream output, Key key)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			IllegalBlockSizeException,
			NoSuchProviderException,
			BadPaddingException,
			NoSuchPaddingException {
		crypt(input, output, key, Cipher.ENCRYPT_MODE);
	}
	
	/**
	 * Encrypts a String and returns it in base64 encoding.
	 * 
	 * @param input The String to be encrypted.
	 * @param key The Key to use during encryption.
	 * 
	 * @return The base64-encoded, encrypted String.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the Key is not a properly formatted key.
	 * @throws IOException If reading from the InputStream or writing to the OutputStream fails.
	 * @throws IllegalBlockSizeException The data to be decrypted is not of valid length.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 * @throws BadPaddingException The data to be decrypted is not of valid length.
	 * @throws NoSuchPaddingException Can't find the specified padding format.  Won't be thrown unless default values are change
	 */
	public static String encrypt(String input, Key key)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			IllegalBlockSizeException,
			NoSuchProviderException,
			BadPaddingException,
			NoSuchPaddingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encrypt(new ByteArrayInputStream(input.getBytes()), baos, key);
		return encode64(baos.toByteArray());
	}
	
	/**
	 * A front-end to crypt() which tells it to decrypt.
	 * 
	 * @param input The data to be decrypted.
	 * @param output The decrypted data.
	 * @param key The Key used to decrypt.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the Key is not a properly formatted key.
	 * @throws IOException If reading from the InputStream or writing to the OutputStream fails.
	 * @throws IllegalBlockSizeException The data to be decrypted is not of valid length.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 * @throws BadPaddingException The data to be decrypted is not of valid length.
	 * @throws NoSuchPaddingException Can't find the specified padding format.  Won't be thrown unless default values are change
	 */
	public static void decrypt(InputStream input, OutputStream output, Key key)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			IllegalBlockSizeException,
			NoSuchProviderException,
			BadPaddingException,
			NoSuchPaddingException {
		crypt(input, output, key, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * Decrypts a base64-encoded, encrypted String.
	 * 
	 * @param input The base64-encoded, encrypted String to be decrypted.
	 * @param key The Key to use during decryption.
	 * 
	 * @return The decrypted String.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the Key is not a properly formatted key.
	 * @throws IOException If reading from the InputStream or writing to the OutputStream fails.
	 * @throws IllegalBlockSizeException The data to be decrypted is not of valid length.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 * @throws BadPaddingException The data to be decrypted is not of valid length.
	 * @throws NoSuchPaddingException Can't find the specified padding format.  Won't be thrown unless default values are change
	 */
	public static String decrypt(String input, Key key)
	throws IOException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			IllegalBlockSizeException,
			NoSuchProviderException,
			BadPaddingException,
			NoSuchPaddingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		decrypt(new ByteArrayInputStream(decode64(input)), baos, key);
		return new String(baos.toByteArray());
	}
	
	/**
	 * Does the actual encrypting/decrypting of the InputStream.
	 * 
	 * @param input The data to be decrypted.
	 * @param output The decrypted data.
	 * @param key The Key used to decrypt.
	 * @param mode Whether or not we're encrypting or decrypting.
	 * 
	 * @throws NoSuchAlgorithmException Can't find the specified algorithm.  Won't be thrown unless default values are changed.
	 * @throws InvalidKeyException If the Key is not a properly formatted key.
	 * @throws IOException If reading from the InputStream or writing to the OutputStream fails.
	 * @throws IllegalBlockSizeException The data to be decrypted is not of valid length.
	 * @throws NoSuchProviderException Can't find the specified provider.  Won't be thrown unless default values are changed.
	 * @throws BadPaddingException The data to be decrypted is not of valid length.
	 * @throws NoSuchPaddingException Can't find the specified padding format.  Won't be thrown unless default values are changed.
	 */
	private static void crypt(InputStream input, OutputStream output, Key key, int mode)
	throws NoSuchAlgorithmException,
			InvalidKeyException,
			IOException,
			IllegalBlockSizeException,
			NoSuchProviderException,
			BadPaddingException,
			NoSuchPaddingException {
		//We need a provider to use RSA.
		Provider bouncy_castle = new BouncyCastleProvider();
		Security.addProvider(bouncy_castle);
		//Instantiate the Cipher and tell it to use BouncyCastle.
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, bouncy_castle.getName());
		
		//We have to do the encrypting/decrypting in chunks.
		byte[] block = null;
		switch (mode) {
			case Cipher.ENCRYPT_MODE:
				//Set the cipher to encrypt.
				cipher.init(Cipher.ENCRYPT_MODE, key);
				//Set the encryption block size.
				block = new byte[ENCRYPTION_BLOCK_SIZE];
				break;
			case Cipher.DECRYPT_MODE:
				//Set the cipher to decrypt.
				cipher.init(Cipher.DECRYPT_MODE, key);
				//Set the decryption block size.
				block = new byte[DECRYPTION_BLOCK_SIZE];
				break;
			default:
				break;
		}
		
		int length;
		//Walk through the InputStream, writing encrypted/decrypted blocks to the OutputStream.
		while ((length = input.read(block)) != -1) {
			output.write(cipher.doFinal(copy(block,length)));
		}
		//Flush the output.
		output.flush();
		//Remove the RSA provider.
		Security.removeProvider(bouncy_castle.getName());
	}
	
	/**
	 * Copies bytes from one array to a new one of varying size.
	 * 
	 * @param source The bytes to be copied.
	 * @param length The size of the new array and how much to copy from the source.
	 * 
	 * @return The new array or the orginal if the lengths matched.
	 */
	private static byte[] copy(byte[] source, int length) {
		byte[] target = null;
		if (source.length == length) {
			//Just return the orginal.
			target = source;
		} else {
			target = new byte[length];
			//Walk through and copy it byte by byte.
			for (int i = 0; i < length; i++) {
				target[i] = source[i];
			}
		}
		return target;
	}
	
	/** 
	 * Converts a byte array into a base64-encoded String.
	 * 
	 * @param bytes The byte to be encoded.
	 * 
	 * @return The base64-encoded String.
	 */
    private static String encode64(byte[] bytes) {
        return (new BASE64Encoder()).encode(bytes);
    }

    /**
     * Converts a base64-encoded String into a byte array.
     * 
     * @param text The base64-encoded String.
     * @param The decoded String as an array of bytes.
     * 
     * @throws IOException If the String is not in proper base64 format.
     */
    private static byte[] decode64(String text)
    throws IOException {
    	return (new BASE64Decoder()).decodeBuffer(text);
    }
}
