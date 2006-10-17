package com.vtls.opensource.security;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This Class is used to create an SHA-1 hash of an InputStream.
 * 
 * @author Chris Hall
 * @version 4.0
 */
public class SHA1MessageDigester {
	/**
	 * The hashing algorithm to be used.
	 */
	public static String DigestAlgorithm = "SHA-1";
	
	/**
	 * Used to produce a hash as an array of bytes.
	 * 
	 * @param _input The InputStream to be hashed.
	 * 
	 * @return The hash as an array of bytes.
	 */
	public static byte[] digest(BufferedInputStream _input)
	throws IOException {
		if (_input != null) {
			try {
				//Create the MessageDigester.
				MessageDigest digester = MessageDigest.getInstance(DigestAlgorithm);
				
				//Run through the InputStream and hash 4K at a time.
			    byte[] bytes = new byte[4096];
			    for (int n; (n = _input.read(bytes)) != -1;) {
			    	//Hash the current 4K block;
			    	digester.update(bytes, 0, n);
			    }
			    
			    //Return the hash.
			    return digester.digest();
			} catch (NoSuchAlgorithmException nsae) {
				//This will never happen.
				return null;
			}
		} else {
			//We need data to hash...
			throw new IOException("Null InputStream");
		}
	}
	
	/**
	 * Used to produce a hash as a hexadecimal String.
	 * 
	 * @param _input The InputStream to be hashed.
	 * 
	 * @return The hash as a hexadecimal String.
	 */
	public static String digestToHexString(InputStream _input)
	throws IOException {
		//Get the hash.
	    byte[] hash = digest(new BufferedInputStream(_input));
		
	    //Convert the hash byte[] into hexadecimal Strings.
	    StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
	    	hex.append(Integer.toHexString(hash[i] & 0xff));
        }
	    //Return them as one hexadecimal String.
	    return hex.toString();
	}
	
	public static String getDigestAlgorithm() {
		return DigestAlgorithm;
	}
}