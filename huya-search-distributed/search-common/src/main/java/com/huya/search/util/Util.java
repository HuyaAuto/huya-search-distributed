package com.huya.search.util;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.local.LocalFile;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.util.*;

public class Util {
	public static String[] emptyStringArray = new String[] {};

	public static boolean equals(Object s, Object t) {
		if (s == t) {
			return true;
		}
		if (s == null || t == null) {
			return false;
		}
		return s.equals(t);
	}

	public static boolean equals(String s, String t) {
		return equals((Object) s, (Object) t);
	}

	public static String[] splitJumpEscape(String text, char split) {
		int wordStartIndex = 0;
		int parseStartIndexNow = 0;
		char[] array = text.toCharArray();
		List<String> result = new ArrayList<String>();
		while (true) {
			int indexNow = text.indexOf(split, parseStartIndexNow);
			if (indexNow < 0) {
				result.add(text.substring(wordStartIndex));
				break;
			} else {
				if (indexNow > 0) {
					// 判定前一个字符是否是转移字符"\"
					if (array[indexNow - 1] == '\\') {
						// 是转义字符，不算
						parseStartIndexNow = indexNow + 1;
						continue;
					}
				}
				result.add(text.substring(wordStartIndex, indexNow));
				parseStartIndexNow = indexNow + 1;
				wordStartIndex = indexNow + 1;
			}
		}
		return result.toArray(emptyStringArray);
	}

	public static String stringifyException(Throwable e) {
		StringWriter stm = new StringWriter();
		PrintWriter wrt = new PrintWriter(stm);
		e.printStackTrace(wrt);
		wrt.close();
		return stm.toString();
	}

	public static RuntimeException newError(String string) {
		return new RuntimeException(string);
	}

	public static RuntimeException newCombinError(List<Exception> exes) {
		StringBuilder msg = new StringBuilder();
		for (Exception e : exes) {
			msg.append(Util.stringifyException(e)).append("\r\n");
		}
		return new RuntimeException(msg.toString());
	}

	public static void assertTrue(boolean b) {
		if (!b) {
			throw new AssertionError();
		}
	}

	public static RuntimeException notImpleException() {
		throw new RuntimeException();
	}

	public static RuntimeException notExpect(Exception e) {
		return new RuntimeException("不应该发生此错误，应该是触发了某个bug");
	}

	public static RuntimeException unExpect() {
		return new RuntimeException("不应该发生此错误，应该是触发了某个bug,请联系系统管理员");
	}

	public static RuntimeException newError(Exception e) {
		return new RuntimeException(e);
	}

	public static <K, T> T getRequireValue(Map<K, T> map, K key) {
		T result = map.get(key);
		if (result == null) {
			throw newError(String.format("%s must not be empty!", key));
		}
		return result;

	}

	public static <K, T> T getValue(Map<K, T> map, K key, T defaultValue) {
		T result = map.get(key);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	public static RuntimeException unExpect(Exception e) {
		return new RuntimeException(e);
	}

	public static RuntimeException notImpleException(String msg) {
		return new RuntimeException(msg);
	}

	public static RuntimeException unExpect(String msg) {
		return new RuntimeException(msg + ",此处应该遇到了潜在的bug");
	}

	public static RuntimeException notSupport() {
		return new java.lang.UnsupportedOperationException();
	}

	public static RuntimeException notSupport(String string) {
		return new java.lang.UnsupportedOperationException(string);
	}

	public static void assertTrue(boolean b, String string) {
		if (!b) {
			throw new AssertionError(string);
		}
	}

	static Method getMappedByteBufferMethod;
	static Method cleanMappedByteBufferMethod;
	static {
		try {
			getMappedByteBufferMethod = Class.forName(
					"java.nio.DirectByteBuffer").getMethod("cleaner",
					new Class[0]);
			getMappedByteBufferMethod.setAccessible(true);
			cleanMappedByteBufferMethod = Class.forName("sun.misc.Cleaner")
					.getMethod("clean", new Class[0]);
			cleanMappedByteBufferMethod.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			throw Util.unExpect(e);
		}
	}

	public static void cleanMappedByteBuffer(MappedByteBuffer buffer) {
		try {
			Object cleaner = getMappedByteBufferMethod.invoke(buffer,
					new Object[0]);
			cleanMappedByteBufferMethod.invoke(cleaner, new Object[0]);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw Util.unExpect();
		}
	}

	public static int findMix(int[] rowNos) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static <T> List<T> join(List<T> a, List<T> b) {
		Set<T> tmp = new HashSet<>(a);
		tmp.removeAll(b);
		Set<T> result = new HashSet<>(a);
		result.removeAll(tmp);
		return new ArrayList<>(result);
	}

	public static <T> List<T> union(List<T> a, List<T> b) {
		Set<T> result = new HashSet<T>();
		result.addAll(a);
		result.addAll(b);
		return new ArrayList<>(result);
	}

	public static RuntimeException unExpect(Enum enumV) {
		throw unExpect("此处不希望出现枚举值" + enumV);
	}

	public static File vfs2File(FileObject vfsObj) {
		if (vfsObj instanceof LocalFile) {
			String fullName = vfsObj.getName().toString();
			return new File(fullName.substring("file:///".length()));
		} else {
			throw unExpect("vfs对象不是本地文件系统" + vfsObj);
		}
	}

	public static FileObject local2Vfs(File file) {
		try {
			return VFS.getManager().resolveFile(file.toString());
		} catch (FileSystemException e) {
			throw unExpect();
		}
	}
	
	public static void sleep(long ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ignored){}
	}
	
	public static Timer addTimer(String thread, int delay, int period, TimerTask task){
		Timer timer = new Timer(thread, true);
		timer.schedule(task, delay, period);
		return timer;
	}

	public static void main(String[] args) {
		FileObject local2Vfs = local2Vfs(new File("c:\\serviceKey"));
		System.out.println(local2Vfs);
		File file = vfs2File(local2Vfs);
		System.out.println(file);
		System.out.println(file.exists());
	}
	
}
