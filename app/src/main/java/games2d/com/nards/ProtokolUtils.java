package games2d.com.nards;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


public class ProtokolUtils 
{
	static public int htonl(int value) 
	{
		  if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) )
		  {
		     return value;
		  }
		  return Integer.reverseBytes(value);
	}

	static public long htonl2(long value)
	{
		if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) )
		{
			return value;
		}

		return Long.reverseBytes(value);
	}
	
	static public short htons(short value) 
	{
		  if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) )
		  {
		     return value;
		  }
		  return Short.reverseBytes(value);
	}

	static public int get_crc32(byte[] bytes)
	{
		int[] table = {
				0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,
				0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,
				0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
				0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,
				0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
				0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
				0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
				0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,
				0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
				0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
				0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,
				0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
				0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,
				0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
				0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
				0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,
				0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,
				0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
				0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,
				0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
				0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
				0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,
				0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,
				0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
				0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
				0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,
				0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
				0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
				0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
				0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
				0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,
				0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d,
		};


		//byte[] bytes = a.getBytes();
		int crc0 = 0xffffffff;
		for (byte b : bytes) {
			crc0 = (crc0 >>> 8) ^ table[(crc0 ^ b) & 0xff];
		}

		// flip bits
		crc0 = crc0 ^ 0xffffffff;

		//Log.d("TAG", ": " + String.valueOf(crc0));

		crc0  = 0xFFFFFFFF;       // initial contents of LFBSR
		int poly = 0xEDB88320;   // reverse polynomial

		for (byte b : bytes) {
			int temp = (crc0 ^ b) & 0xff;

			// read 8 bits one at a time
			for (int i = 0; i < 8; i++) {
				if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
				else                 temp = (temp >>> 1);
			}
			crc0 = (crc0 >>> 8) ^ temp;
		}

		// flip bits
		crc0 = crc0 ^ 0xffffffff;

		return  crc0;
	}
	
	static public int fromByteArray(byte[] bytes) 
	{
	     return ByteBuffer.wrap(bytes).getInt();
	}
	
	static public short fromByteArray_short(byte[] bytes) 
	{
	     return ByteBuffer.wrap(bytes).getShort();
	}
	
	static public ReadCommand read_command_encrypt(InputStream is, String password)
	{
		ReadCommand res = new ReadCommand();
		
		int num_read        = 0;
		byte[] token        = new byte[1];
		byte[] byte_array_2 = new byte[2];
		byte[] len_message  = new byte[4];
		byte[] crc32        = new byte[4];
		int offset          = 0;
		
		try
		{
			num_read = is.read(len_message, 0, 4); offset += 4;
			
			//Log.i("TAG", "num_read " + String.valueOf(num_read));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return res;
		}
		
		if(num_read == -1)
		{
			res.close_sock = true;
			return res;
		}
		
		if(num_read < 4)
		{
			return res;
		}
		
		int len = htonl( fromByteArray( len_message ) );

		len -= 4; // crc32
		
		/*try
		{
			num_read = is.read(token, 0, 1);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return res;
		}
		
		if(num_read < 1) { return res; }
		
		if( (new String( token )).equalsIgnoreCase("*") == false )
		{
			return res;
		}*/

		//Log.i("TAG", "len " + String.valueOf(len));

		if( len < 0 )
		{
			//return res;
		}
		
		///------------------------------------------------------------------------------------------
		ByteBuffer b = ByteBuffer.allocate( len );
		
		int local_buffer_size = 1024;
		byte[] local_buffer = new byte[local_buffer_size];
		
		int total_read    = 0;
		int count_read    = 0;
		int need_download = len;
		boolean error     = false;

		/*if(len == 73704)
		{
			//Log.i("TAG", "OK");
		}*/
		
		while(true)
		{
			if(need_download - total_read > local_buffer_size)
            {
				try
				{
					count_read = is.read(local_buffer, 0, local_buffer_size);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					break;
				}
            }
            else
            {
            	try
				{
                	count_read = is.read(local_buffer, 0, (need_download - total_read) );
            	}
                catch (IOException e)
				{
					e.printStackTrace();
					break;
				}
            }

            if(count_read < 0)
            {
                if((need_download - total_read != 0) )
                { error = true; }
                break;
            }
            
            total_read += count_read;

            //fwrite(buffer_local, 1, count_read, f);
            b.put(local_buffer, 0, count_read);

            if(need_download - total_read == 0)
            {
                break;
            }
            //-------------
		}

		if( error )
		{
			Log.e("TAG", "Error read 560000");
			return res;
		}

		///------------------------------------------------------------------------------------------
 		//Log.i("TAG", "total_read " + String.valueOf(total_read) + " " + String.valueOf(b.position()));

		try
		{
			num_read = is.read(crc32, 0, 4); offset += 4;

			if(num_read != 4)
			{
				Log.i("TAG", "error num_read " + String.valueOf(num_read));
				return res;
			}

			//Log.i("TAG", "num_read " + String.valueOf(num_read));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return res;
		}

		int read_crc32 = htonl( fromByteArray( crc32 ) );
		int calc_crc32 = ProtokolUtils.get_crc32(b.array());

		if( read_crc32 != calc_crc32 )
		{
			Log.e("TAG", "ERROR READ TCP " + String.valueOf(need_download) + " crc32: " + Integer.toBinaryString(read_crc32) );
			return res;
		}

		
		byte[] read_byff = JniApi.dataEncrypt(b.array(), password);
		//byte[] read_byff = b.array();
		offset = 0;

		byte[] buffer_timestamp = new byte[8];
		System.arraycopy(read_byff, offset, buffer_timestamp, 0, 8); offset += 8;
		res.timestamp = htonl2( ByteBuffer.wrap(buffer_timestamp).getLong() );
		
		System.arraycopy(read_byff, offset, byte_array_2, 0, 2); offset += 2;
		
		len = htons( fromByteArray_short( byte_array_2 ) );
		
		String command = "";
		byte[] _bcommand = new byte[ len ];
		
		System.arraycopy(read_byff, offset, _bcommand, 0, len); offset += len;
		
		command = new String( _bcommand );
		
		Log.i("TAG", "command: " + command );
		
		res.cmd = command;

		byte[] _count_params = new byte[1];
		
		System.arraycopy(read_byff, offset, _count_params, 0, 1); offset += 1;
		
		//len = htonl( fromByteArray( len_message ) );
		byte count_params = _count_params[0];
		
		if(count_params == 0)
		{
			res.params = null;
		}
		else
		{
			res.params = new ArrayList<byte[]>();
			
			for(int i = 0; i < count_params; i++)
			{
				System.arraycopy(read_byff, offset, byte_array_2, 0, 2); offset += 2;
				
				int len_param_item = htons( fromByteArray_short( byte_array_2 ) );
				
				String p_command  = "";
				byte[] p_bcommand = new byte[ len_param_item ];
				
				System.arraycopy(read_byff, offset, p_bcommand, 0, len_param_item); offset += len_param_item;
				
				//p_command = new String( p_bcommand );
				
				res.params.add( p_bcommand );
			}
		}
		
		// len data
		System.arraycopy(read_byff, offset, len_message, 0, 4); offset += 4;
		
		len = htonl( fromByteArray( len_message ) );
		
		if(len > 0)
		{
			res.data = new byte[len];
			System.arraycopy(read_byff, offset, res.data, 0, len); offset += len;
		}
		
		res.init_ok = true;
		
		return res;
	}
	
	static public byte[] get_buffer_command_encrypt(String cmd, ArrayList<String> params, byte[] add_buffer, String pass, long timestamp)
	{
		byte[] res     = null;
		byte[] encrypt = null;
		
		if(cmd.trim().length() == 0)
		{
			return null;
		}
		
		int total_len = 4 + cmd.getBytes().length + 1;
		byte count_params = 0;
		
		if( params != null )
		{
			count_params = (byte) params.size();
			
			for(int i = 0; i < params.size(); i++)
			{
				total_len += 2 + params.get(i).getBytes().length;
			}
		}
		
		total_len += 4; // длинна буффера
		
		if(add_buffer != null)
		{
			total_len += add_buffer.length;
		}

		total_len += 4; // crc32
		total_len += 8; // timestamp
		
		res     = new byte[ total_len + 4 ];
		encrypt = new byte[ total_len - 4];
		

		int offset   = 0;
		int offset_2 = 0;

		byte[] _len_message = ByteBuffer.allocate(4).putInt( htonl(total_len) ).array();
		
		System.arraycopy(_len_message,    0, res, offset, _len_message.length); offset += _len_message.length; /// сохранить обший размер пакета

		// encrypt mass
		_len_message = ByteBuffer.allocate(8).putLong(htonl2(timestamp)).array();

		System.arraycopy(_len_message,    0, encrypt, offset_2, _len_message.length); offset_2 += _len_message.length;
		
		//System.arraycopy("*".getBytes(),  0, encrypt, offset_2, 1);                   offset_2 += 1;
		
		_len_message = ByteBuffer.allocate(2).putShort( htons( (short) cmd.length() ) ).array();
		System.arraycopy(_len_message,    0, encrypt, offset_2, _len_message.length); offset_2 += _len_message.length;
		
		byte[] _bcmd = cmd.getBytes();
		System.arraycopy(_bcmd,    0, encrypt, offset_2, _bcmd.length); offset_2 += _bcmd.length;
		
		_len_message = ByteBuffer.allocate(1).put(count_params ).array();
		System.arraycopy(_len_message,    0, encrypt, offset_2, _len_message.length); offset_2 += _len_message.length;
		
		if( params != null )
		{
			for(int i = 0; i < params.size(); i++)
			{
				String param_item = params.get(i);
				
				_len_message = ByteBuffer.allocate(2).putShort( htons((short) param_item.getBytes().length) ).array();
				System.arraycopy(_len_message,    0, encrypt, offset_2, _len_message.length); offset_2 += _len_message.length;
				
				_bcmd = param_item.getBytes();
				System.arraycopy(_bcmd,    0, encrypt, offset_2, _bcmd.length); offset_2 += _bcmd.length;
			}
		}
		
		int len_add_byffer = 0;
		
		if( add_buffer != null )
		{
			len_add_byffer = add_buffer.length;
		}
		
		_len_message = ByteBuffer.allocate(4).putInt( htonl( len_add_byffer ) ).array();
		System.arraycopy(_len_message,    0, encrypt, offset_2, _len_message.length); offset_2 += _len_message.length;
		
		if(add_buffer != null)
		{
			System.arraycopy(add_buffer,    0, encrypt, offset_2, add_buffer.length); offset_2 += add_buffer.length;
		}
		
		byte[] _t = JniApi.dataEncrypt(encrypt, pass);
		
		System.arraycopy(_t,    0, res, offset, _t.length); offset += _t.length;

		int calc_crc32 = ProtokolUtils.get_crc32(_t);

		_len_message = ByteBuffer.allocate(4).putInt( htonl( calc_crc32 ) ).array();

		System.arraycopy(_len_message,    0, res, offset, _len_message.length); offset += _len_message.length;
		
		return res;
	}
}
