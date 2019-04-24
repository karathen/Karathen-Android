package com.github.neo.core;



import com.github.ontio.common.UInt256;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;

/**
 *
 */
public class TransactionInput extends Serializable {
    /**
     *
     */
    public UInt256 prevHash;
    /**
     *
     */
    public short prevIndex; 

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (null == obj) {
        	return false;
        }
        if (!(obj instanceof TransactionInput)) {
        	return false;
        }
        TransactionInput other = (TransactionInput) obj;
        return prevHash.equals(other.prevHash) && prevIndex == other.prevIndex;
    }

    @Override
    public int hashCode() {
        return prevHash.hashCode() + prevIndex;
    }

    /**
	 * byte格式数据反序列化
	 */
    @Override
	public void deserialize(BinaryReader reader) throws IOException {
		try {
			prevHash = reader.readSerializable(UInt256.class);
			prevIndex = reader.readShort();
//			prevIndex = (short) reader.readVarInt();
		} catch (InstantiationException | IllegalAccessException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void serialize(BinaryWriter writer) throws Exception {
		writer.writeSerializable(prevHash);
		writer.writeShort(prevIndex);
//		writer.writeVarInt(prevIndex);
	}
	

	@Override
	public String toString() {
		return "TransactionInput [prevHash=" + prevHash + ", prevIndex="
				+ prevIndex + "]";
	}
}
