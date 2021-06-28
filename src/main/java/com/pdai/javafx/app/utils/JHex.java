package com.pdai.javafx.app.utils;


import java.math.BigInteger;

/**
 * 十六进制数据处理工具类
 *
 */
public class JHex {

   private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
         'E', 'F' };

   public static byte[] decode(char[] data) {
      int len = data.length;
      if ((len & 0x01) != 0) {
         throw new IllegalArgumentException("Odd number of characters.");
      }
      byte[] out = new byte[len >> 1];
      // two characters form the hex value.
      for (int i = 0, j = 0; j < len; i++) {
         int f = toDigit(data[j], j) << 4;
         j++;
         f = f | toDigit(data[j], j);
         j++;
         out[i] = (byte) (f & 0xFF);
      }
      return out;
   }

   public static char[] encode(byte[] data) {
      return encode(data, DIGITS_UPPER);
   }

   private static char[] encode(byte[] data, char[] toDigits) {
      int l = data.length;
      char[] out = new char[l << 1];
      // two characters form the hex value.
      for (int i = 0, j = 0; i < l; i++) {
         out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
         out[j++] = toDigits[0x0F & data[i]];
      }
      return out;
   }

   public static String encodeString(byte[] data) {
      return new String(encode(data));
   }
   public static Double hexStrToDouble(String hexStr){
       long longBits = Long.valueOf(hexStr, 16);
       return Double.longBitsToDouble(longBits);
   }

   private static int toDigit(char ch, int index) {
      final int digit = Character.digit(ch, 16);
      if (digit == -1) {
         throw new IllegalArgumentException("Illegal hexadecimal character " + ch + " at index " + index);
      }
      return digit;
   }


   public static String xor(String str1, String str2) {
      BigInteger big1= new BigInteger(str1, 16);
      BigInteger big2= new BigInteger(str2, 16);
      return big1.xor(big2).toString(16);

   }
}