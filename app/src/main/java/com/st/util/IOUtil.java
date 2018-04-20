/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1257 $
  * Revision of last commit    :  $Rev: 1257 $
  * Date of last commit     :  $Date: 2015-10-22 16:02:56 +0200 (Thu, 22 Oct 2015) $ 
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2015 STMicroelectronics</center></h2>
  *
  * Licensed under ST MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/myliberty  
  *
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

package com.st.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class IOUtil {
      public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
          read = in.read(buffer);
          if (read != -1)
            out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
      }

      public static String toString(InputStream in) throws IOException {
        StringWriter out = new StringWriter();
        copy(new InputStreamReader(in),out);
        out.close();
        in.close();
        return out.toString();
      }

      public static int copy(InputStream in, OutputStream out) throws IOException {
        int count = 0;
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
          read = in.read(buffer);
          if (read != -1) {
            count += read;
            out.write(buffer,0,read);
          }
        }
        return count;
      }

      public static int copy(Reader input,Writer output) throws IOException {
            char[] buffer = new char[1024];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }



    public static byte[] longToBytes(long l) {
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        while (l != 0) {
            bytes.add((byte) (l % (0xff + 1)));
            l = l >> 8;
        }
        byte[] bytesp = new byte[bytes.size()];
        for (int i = bytes.size() - 1, j = 0; i >= 0; i--, j++) {
            bytesp[j] = bytes.get(i);
        }
        return bytesp;
    }

    }