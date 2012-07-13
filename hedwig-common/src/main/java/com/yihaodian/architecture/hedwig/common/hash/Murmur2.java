/**
 * 
 */
package com.yihaodian.architecture.hedwig.common.hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * @author Archer Jiang
 *
 */
public class Murmur2 implements HashFunction {
	private int hash(String data, int seed) {
		return hash32(data.getBytes(), seed);
	}

	private int hash32(byte[] data, int seed) {
		int m = 0x5bd1e995;
		int r = 24;

		int h = seed ^ data.length;

		int len = data.length;
		int len_4 = len >> 2;

		for (int i = 0; i < len_4; i++) {
			int i_4 = i << 2;
			int k = data[i_4 + 3];
			k = k << 8;
			k = k | (data[i_4 + 2] & 0xff);
			k = k << 8;
			k = k | (data[i_4 + 1] & 0xff);
			k = k << 8;
			k = k | (data[i_4 + 0] & 0xff);
			k *= m;
			k ^= k >>> r;
			k *= m;
			h *= m;
			h ^= k;
		}

		int len_m = len_4 << 2;
		int left = len - len_m;

		if (left != 0) {
			if (left >= 3) {
				h ^= (int) data[len - 3] << 16;
			}
			if (left >= 2) {
				h ^= (int) data[len - 2] << 8;
			}
			if (left >= 1) {
				h ^= (int) data[len - 1];
			}

			h *= m;
		}

		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;

		return h;
	}

	@Override
	public int hash(Object data, int seed) {
		int hashCode;
		if (data instanceof String) {
			hashCode = hash(data, seed);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] objBytes = bos.toByteArray();
			hashCode = hash32(objBytes, seed);
		}
		return hashCode;
	}

	@Override
	public int hash(Object data) {
		return hash(data, 2 << 16);
	}
}
