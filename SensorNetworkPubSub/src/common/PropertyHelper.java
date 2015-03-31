package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class PropertyHelper {
	public static ReadWriteLock lock = new ReentrantReadWriteLock(true);//Fancy lock to ensure synchronized read/writes
	public static void writeToProperty(String key, String value){		
		writeToProperty("config", key, value);
	}

	public static Timestamp getFileTimestamp(){
			lock.readLock().lock();
			File f = new File("temperature.properties");
			long time = f.lastModified();
			lock.readLock().unlock();
			return time == 0 ? new Timestamp(time) : null;
			
		
	}
	public static void writeToProperty(String filename, String key, String value){
		lock.writeLock().lock();

		InputStream input = null;
		Properties prop = new Properties();	
		OutputStream output = null;

		try{
			input = new FileInputStream(filename + ".properties");
			prop.load(input);
			input.close();
		} catch(Exception npe){
			System.err.println("File not found. File will be created.");
		}
		try {
			output = new FileOutputStream(filename + ".properties", false);	
		} catch (FileNotFoundException f){
			System.err.println("PropertyHelper writeToProperty() - file not found exception");
		} 
		// save properties to project root folder
		prop.put(key, value);
		try {
			if (output != null) {
				prop.store(output, null);
				output.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lock.writeLock().unlock();
	}


	public static String readFromProperty(String key){
		return readFromProperty("config", key);
	}
	public static String readFromProperty(String filename, String key){
		lock.readLock().lock();
		String value = null;
		Properties prop = new Properties();
		InputStream in = null;
		try {
			//			System.out.println(fileName + ".properties");

			in = new FileInputStream(filename + ".properties");
			prop.load(in);
			prop.size();
//			File test = new File(filename+".properties");
//			test.lastModified();
			value = prop.getProperty(key);

		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported encoding in property");
		} catch (FileNotFoundException e) {
			System.err.println("file is not found: " + key);
			//			System.err.println("path: " + configPath);
		} catch (IOException e) {
		} finally{
			try {

				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				System.err.println("failed to load inputStreamReader in property");
			}
		}
		lock.readLock().unlock();
		return value;
	}

	// testing if overwriting is done correctly and if read and write itself is working properly, even if does not exist 
	public static void main(String[] args){
		//		PropertyHelper p = new PropertyHelper();
		//		p.writeToProperty("test", "key1", "value1");
		//		System.out.println("init: " + p.readFromProperty("test", "key1"));
		//		p.writeToProperty("test", "key2", "value2");
		//		System.out.println("1: " + p.readFromProperty("test", "key1"));
		//		System.out.println("2: " + p.readFromProperty("test", "key2"));
		//		p.writeToProperty("test", "key1", "value6");
		//		System.out.println("3: " + p.readFromProperty("test", "key1"));
	}

	public synchronized static int findLastIndex() {
		lock.readLock().lock();
		int i = 0;
		while(true){
			if(readFromProperty("temperature", String.valueOf(i)) == null) {
				lock.readLock().unlock();
				return i;
			}
			i++;
		}

	}
}
