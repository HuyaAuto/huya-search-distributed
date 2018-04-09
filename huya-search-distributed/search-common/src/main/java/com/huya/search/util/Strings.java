package com.huya.search.util;

import java.security.SecureRandom;
import java.util.List;

/**
 * @author colin.ke keqinwu@yy.com
 */
public class Strings {

	public static final String[] EMPTY_ARRAY = new String[0];

	public static String toUnderscoreCase(String value) {
		return toUnderscoreCase(value, null);
	}

	public static String toUnderscoreCase(String value, StringBuilder sb) {
		boolean changed = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isUpperCase(c)) {
				if (!changed) {
					if (sb != null) {
						sb.setLength(0);
					} else {
						sb = new StringBuilder();
					}
					// copy it over here
					for (int j = 0; j < i; j++) {
						sb.append(value.charAt(j));
					}
					changed = true;
					if (i == 0) {
						sb.append(Character.toLowerCase(c));
					} else {
						sb.append('_');
						sb.append(Character.toLowerCase(c));
					}
				} else {
					sb.append('_');
					sb.append(Character.toLowerCase(c));
				}
			} else {
				if (changed) {
					sb.append(c);
				}
			}
		}
		if (!changed) {
			return value;
		}
		return sb.toString();
	}

	/**
	 * Capitalize a <code>String</code>, changing the first letter to
	 * upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 *
	 * @param str the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize) {
			sb.append(Character.toUpperCase(str.charAt(0)));
		} else {
			sb.append(Character.toLowerCase(str.charAt(0)));
		}
		sb.append(str.substring(1));
		return sb.toString();
	}

	public static String toCamelCase(String value) {
		return toCamelCase(value, null);
	}

	public static String toCamelCase(String value, StringBuilder sb) {
		boolean changed = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '_') {
				if (!changed) {
					if (sb != null) {
						sb.setLength(0);
					} else {
						sb = new StringBuilder();
					}
					// copy it over here
					for (int j = 0; j < i; j++) {
						sb.append(value.charAt(j));
					}
					changed = true;
				}
				if (i < value.length() - 1) {
					sb.append(Character.toUpperCase(value.charAt(++i)));
				}
			} else {
				if (changed) {
					sb.append(c);
				}
			}
		}
		if (!changed) {
			return value;
		}
		return sb.toString();
	}

	public static String[] splitStringByCommaToArray(final String s) {
		return splitStringToArray(s, ',', false);
	}

	public static String[] splitStringByCommaToArray(final String s, boolean skipEmpty) {
		return splitStringToArray(s, ',', skipEmpty);
	}

	public static String[] splitStringToArray(final CharSequence s, final char c, boolean skipEmpty) {
		if (s == null || s.length() == 0) {
			return Strings.EMPTY_ARRAY;
		}
		int count = 1;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				count++;
			}
		}
		final String[] result = new String[count];
		final StringBuilder builder = new StringBuilder();
		int res = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				if (builder.length() > 0) {
					result[res++] = builder.toString();
					builder.setLength(0);
				} else if(!skipEmpty) {
					result[res++] = null;
				}

			} else {
				builder.append(s.charAt(i));
			}
		}
		if (builder.length() > 0) {
			result[res++] = builder.toString();
		}
		return result;
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 *
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <p><pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 *
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Return substring(beginIndex, endIndex) that is impervious to string length.
	 */
	public static String substring(String s, int beginIndex, int endIndex) {
		if (s == null) {
			return s;
		}

		int realEndIndex = s.length() > 0 ? s.length() - 1 : 0;

		if (endIndex > realEndIndex) {
			return s.substring(beginIndex);
		} else {
			return s.substring(beginIndex, endIndex);
		}
	}

	/**
	 * Test whether the given string matches the given substring
	 * at the given index.
	 *
	 * @param str       the original string (or StringBuilder)
	 * @param index     the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 */
	public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Format the double value with a single decimal points, trimming trailing '.0'.
	 */
	public static String format1Decimals(double value, String suffix) {
		String p = String.valueOf(value);
		int ix = p.indexOf('.') + 1;
		int ex = p.indexOf('E');
		char fraction = p.charAt(ix);
		if (fraction == '0') {
			if (ex != -1) {
				return p.substring(0, ix - 1) + p.substring(ex) + suffix;
			} else {
				return p.substring(0, ix - 1) + suffix;
			}
		} else {
			if (ex != -1) {
				return p.substring(0, ix) + fraction + p.substring(ex) + suffix;
			} else {
				return p.substring(0, ix) + fraction + suffix;
			}
		}
	}




	public static String join(List<String> strings, char ch) {
		if(null == strings || strings.isEmpty())
			return "";
		if(strings.size() == 1)
			return strings.get(0);

		StringBuilder sb = new StringBuilder();
		for(int i=0;i<strings.size() - 1;++i)
			sb.append(strings.get(i)).append(ch);
		sb.append(strings.get(strings.size() - 1));
		return sb.toString();
	}

	private static class Holder {
		static final SecureRandom numberGenerator = new SecureRandom();
	}

	public static String randomBase64UUID() {
		SecureRandom ng = Holder.numberGenerator;

		byte[] randomBytes = new byte[16];
		ng.nextBytes(randomBytes);
		randomBytes[6] &= 0x0f;  /* clear version        */
		randomBytes[6] |= 0x40;  /* set to version 4     */
		randomBytes[8] &= 0x3f;  /* clear variant        */
		randomBytes[8] |= 0x80;  /* set to IETF variant  */

		String encoded = Base64.encodeBytes(randomBytes);
		// we know the bytes are 16, and not a multi of 3, so remove the 2 padding chars that are added
		assert encoded.charAt(encoded.length() - 1) == '=';
		assert encoded.charAt(encoded.length() - 2) == '=';
		return encoded.substring(0, encoded.length() - 2);
	}

	/**
	 * same result as (strings[0] + strings[1] + ... strings[n]).hashCode()
	 */
	public static int hashCodeForStrings(String... strings) {
		int hash = 0;
		for(String str : strings) {
			for(int i=0;i<str.length();++i) {
				hash = 31 * hash + str.charAt(i);
			}
		}
		return hash;
	}

	public static void main(String[] args) {
		String[] arr = splitStringToArray("14999999601|Customer#14999999601|zo,UgJJ5PHo7IczfM|12|22-962-654-6617|7094.52|FURNITURE|quests. furiously ironic deposits |", '|', false);
	}
}
